/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 *  mm-ADT is free software: you can redistribute it and/or modify it under
 *  the terms of the GNU Affero General Public License as published by the
 *  Free Software Foundation, either version 3 of the License, or (at your option)
 *  any later version.
 *
 *  mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 *  WARRANTY; without even the implied warranty of MERCHANTABILITY or
 *  FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 *  License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 *  You can be released from the requirements of the license by purchasing a
 *  commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.obj.`type`

import org.mmadt.language.Tokens
import org.mmadt.language.model.Model
import org.mmadt.language.model.rewrite.LeftRightSweepRewrite
import org.mmadt.language.obj.`type`.{Type, TypeChecker, __}
import org.mmadt.language.obj.op.TraverserInstruction
import org.mmadt.language.obj.value.Value
import org.mmadt.language.obj.{OType, Obj}
import org.mmadt.processor.{Processor, Traverser}

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor(val model:Model = Model.id) extends Processor {
  override def apply[S <: Obj,E <: Obj](domainObj:S,rangeType:Type[E]):E ={
    assert(!domainObj.isInstanceOf[Value[Obj]],"The compiling processor only accepts types: " + domainObj)
    assert(!rangeType.isInstanceOf[__],"The compiling processor can not compile anonymous types: " + rangeType)
    TypeChecker.typeCheck(domainObj,rangeType.domain())
    if (skipCompilation(rangeType)) Traverser.standard(domainObj).apply(rangeType).obj()
    else {
      val domainType       :OType[E]     = domainObj.asInstanceOf[OType[E]]
      var mutatingTraverser:Traverser[E] = Traverser.standard(obj = domainType,model = this.model)
      var previousTraverser:Traverser[E] = Traverser.standard(obj = rangeType.asInstanceOf[E],model = this.model)
      while (previousTraverser != mutatingTraverser) {
        mutatingTraverser = previousTraverser
        previousTraverser = LeftRightSweepRewrite.rewrite(model,mutatingTraverser.obj().asInstanceOf[Type[E]],domainType.asInstanceOf[Type[E]],Traverser.standard(obj = domainType,model = this.model))
      }
      TypeChecker.typeCheck(mutatingTraverser.obj(),rangeType.range)
      new TypeFunctorTraverser[E](domainObj.asInstanceOf[E],Map.empty,model).apply(mutatingTraverser.obj().asInstanceOf[Type[E]]).obj() // TODO: do we want type resolution at compilation
    }
  }
  def skipCompilation[E <: Obj](rangeType:Type[E]):Boolean = model.equals(Model.id) ||
                                                             rangeType.insts.filter(x => x._2.isInstanceOf[TraverserInstruction] ||
                                                                                         x._2.op().equals(Tokens.explain)).iterator.hasNext
}