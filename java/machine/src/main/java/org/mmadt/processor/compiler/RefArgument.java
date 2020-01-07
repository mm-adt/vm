/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing a
 * commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.processor.compiler;

import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.util.ModelHelper;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.IteratorUtils;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class RefArgument<S extends Obj, E extends Obj> implements Argument<S, E> {

    private final Integer count;
    private final Inst bytecode;

    RefArgument(final Obj reference) {
        this.bytecode = reference.access();
        this.count = (Integer) reference.q().<PList<Obj>>get().get(1).get(); // TODO: work within quantifier ring
    }

    @Override
    public E mapArg(final S object) {
        final List<E> list = IteratorUtils.list(FastProcessor.process((E) object.access(null).mapTo(ModelHelper.via(object, this.bytecode))));
        if (list.size() == 1)
            return list.get(0);
        final E e = list.get(0).type();
        return e.q(list.size()).access(StartInst.create(list.subList(0, this.count).toArray(new Object[]{})));
    }

    @Override
    public String toString() {
        return this.bytecode.toString();
    }
}
