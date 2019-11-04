/*
 * Copyright (c) 2019-2029 RReduX,Inc. [http://rredux.com]
 *
 * This file is part of mm-ADT.
 *
 * mm-ADT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mm-ADT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mm-ADT. If not, see <https://www.gnu.org/licenses/>.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license from RReduX,Inc. at [info@rredux.com].
 */

package org.mmadt.language;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.algebra.WithOrder;
import org.mmadt.machine.object.model.type.algebra.WithPlus;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class Context<S extends Obj, E extends Obj> implements Supplier<E> {

    private Model model;
    private S obj;

    private Context(final Model model, final S obj) {
        this.model = model;
        this.obj = obj;
    }


    public static <S extends Obj> Context<S, S> start(final S obj) {
        return Context.start(TModel.of("ex"), obj);
    }

    public static <S extends Obj> Context<S, S> start(final Model model, final S obj) {
        return new Context<>(model, obj);
    }

    public Context<S, E> plus(final Object object) {
       /* if (this.model.has(this.obj.symbol())) {
            final Optional<Inst> inst = this.model.get(this.obj.symbol()).inst(new Bindings(), TInst.of(Tokens.PLUS, prep(object, this.obj)));
            if (inst.isPresent()) {
                Context<S, E> context = this;
                for (final Inst i : inst.get().iterable()) {
                    context = context.compose((S)i.apply(context.obj));
                }
                return context;
            }
        }*/

        return this.compose(((WithPlus<S>) this.obj).plus(prep(object, this.obj)));

    }

    public Context gt(final Object object) {
        return this.compose((S) ((WithOrder<S>) this.obj).gt(prep(object, this.obj)));
    }

    public Context is(final Object object) {
        return this.compose(this.obj.is(prep(object, TBool.of())));
    }

    @Override
    public E get() {
        return (E) this.obj;
    }

    private Context<S, E> compose(final S supplier) {
        this.obj = supplier;
        return this;
    }

    private static <A extends Obj> A prep(final Object object, final Obj start) {
        if (object instanceof Query)
            return start.access(((Query) object).bytecode());
        else
            return (A) ObjectHelper.from(object);
    }
}
