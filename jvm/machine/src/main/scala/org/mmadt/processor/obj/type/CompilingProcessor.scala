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

import org.mmadt.language.LanguageException
import org.mmadt.language.model.Model
import org.mmadt.language.model.rewrite.{LeftRightCompiler, LeftRightSweepRewrite}
import org.mmadt.language.obj.`type`.Type
import org.mmadt.language.obj.{OType, Obj}
import org.mmadt.processor.Processor

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class CompilingProcessor(val model: Model = Model.id) extends Processor {
  override def apply[S <: Obj, E <: Obj](domainObj: S, rangeType: Type[E]): E = {
    // ProcessorException.testRootedType(domainObj,this)
    /*
     val query:E = domainObj.compute(rangeType).asInstanceOf[E]
      DefineOp.traceScanCompiler(DefineOp.getDefines(query),query,DefineOp.replaceRewrite)
     */
    LanguageException.testTypeCheck(domainObj, rangeType.domain)
    if (model == Model.id) LeftRightCompiler.execute(domainObj.compute(rangeType).asInstanceOf[E])
    else {
      val domainType: OType[E] = model(domainObj).asInstanceOf[OType[E]]
      var mutating: E = domainType
      var previous: E = rangeType.asInstanceOf[E]
      while (previous != mutating) {
        mutating = previous
        previous = LeftRightSweepRewrite.rewrite(model, mutating.asInstanceOf[Type[E]], domainType, domainType)
      }
      mutating
    }
  }
}