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

package org.mmadt.processor.function.branch;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.util.StringFactory;
import org.mmadt.processor.compiler.IR;
import org.mmadt.processor.function.AbstractFunction;
import org.mmadt.processor.util.FilterProcessor;
import org.mmadt.processor.util.LoopsProcessor;
import org.mmadt.processor.util.MinimalProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class RepeatBranch<S extends Obj> extends AbstractFunction {

    private IR<S, S> repeatCompilation;
    private IR<S, ?> untilCompilation;
    private IR<S, ?> emitCompilation;
    private int untilLocation = 0;
    private int emitLocation = 0;
    private boolean hasStartPredicates = false;
    private boolean hasEndPredicates = false;

    public RepeatBranch(final Q quantifier, final String label, final List<Object> arguments) {
        super(quantifier, label);
        int location = 1;
        for (int i = 0; i < arguments.size(); i = i + 2) {
            final Character type = (Character) arguments.get(i);
            if ('e' == type) {
                this.emitCompilation = (IR<S, ?>) arguments.get(i + 1);
                this.emitLocation = location++;
                if (this.emitLocation < 3)
                    this.hasStartPredicates = true;
                else
                    this.hasEndPredicates = true;
            } else if ('u' == type) {
                this.untilCompilation = (IR<S, ?>) arguments.get(i + 1);
                this.untilLocation = location++;
                if (this.untilLocation < 3)
                    this.hasStartPredicates = true;
                else
                    this.hasEndPredicates = true;
            } else {
                this.repeatCompilation = (IR<S, S>) arguments.get(i + 1);
                location = 3;
            }
        }
    }

    @Override
    public String toString() {
        return StringFactory.function(this, repeatCompilation, untilCompilation, emitCompilation); // todo: this is random
    }

    public IR<S, S> getRepeat() {
        return this.repeatCompilation;
    }

    public IR<S, ?> getUntil() {
        return this.untilCompilation;
    }

    public IR<S, ?> getEmit() {
        return this.emitCompilation;
    }

    public int getEmitLocation() {
        return this.emitLocation;
    }

    public int getUntilLocation() {
        return this.untilLocation;
    }

    public boolean hasStartPredicates() {
        return this.hasStartPredicates;
    }

    public boolean hasEndPredicates() {
        return this.hasEndPredicates;
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ this.repeatCompilation.hashCode() ^ this.emitCompilation.hashCode() ^ this.untilCompilation.hashCode() ^
                this.emitLocation ^ this.untilLocation;
    }

    @Override
    public RepeatBranch<S> clone() {
        final RepeatBranch<S> clone = (RepeatBranch<S>) super.clone();
        clone.repeatCompilation = this.repeatCompilation.clone();
        clone.emitCompilation = null == this.emitCompilation ? null : this.emitCompilation.clone();
        clone.untilCompilation = null == this.untilCompilation ? null : this.untilCompilation.clone();
        return clone;
    }

    public static <S extends Obj> RepeatBranch<S> compile(final Inst inst) {
        final List<Object> objects = new ArrayList<>();
        for (final Object arg : inst.args()) {
            if (arg instanceof Inst)
                objects.add(new MinimalProcessor<>((Inst) arg)); // TODO: this needs to be dynamically determined
            else if (arg instanceof Character)
                objects.add(arg);
            else if (arg instanceof Integer)
                objects.add(new LoopsProcessor<>((int) arg));
            else if (arg instanceof Boolean)
                objects.add(new FilterProcessor<>((boolean) arg));
        }
        return new RepeatBranch<>(inst.q(), inst.variable(), objects);
    }
}
