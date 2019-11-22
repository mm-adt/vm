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

package org.mmadt.processor.util;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.BarrierInstruction;
import org.mmadt.processor.Processor;
import org.mmadt.processor.ProcessorFactory;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.mmadt.machine.object.impl.composite.TInst.ID;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class FastProcessor<S extends Obj> implements Processor<S>, ProcessorFactory {

    private FastProcessor() {
        // hidden constructor
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public void stop() {
    }

    @Override
    public Iterator<S> iterator(final S obj) {
        final Inst bytecode = obj.access();
        // System.out.println("\nPROCESSING: " + obj);
        Stream<S> stream = Stream.of((S)obj.access().domain().access(ID()));
        for (final Inst inst : bytecode.iterable()) {
            if (inst instanceof BarrierInstruction)  // two patterns: *-to-* and 1-to-*.
                stream = IteratorUtils.stream(((BarrierInstruction<S, S>) inst).createIterator(
                        stream.reduce(((BarrierInstruction<S, S>) inst).getInitialValue(), ((BarrierInstruction<S, S>) inst)::apply)));
            else
                stream = stream.map(((Function<S, S>) inst)::apply).flatMap(s -> IteratorUtils.stream(s.get() instanceof Iterator ? s.get() : IteratorUtils.of(s)));
            stream = stream.filter(s -> !s.q().isZero());
        }
        return stream.map(s -> s.<S>label(bytecode.label())).filter(s -> !s.q().isZero()).iterator();
    }

    @Override
    public void subscribe(final S obj, final Consumer<S> consumer) {
        this.iterator(obj).forEachRemaining(consumer);
    }

    public static <S extends Obj> Iterator<S> process(final S obj) {
        return new FastProcessor<S>().iterator(obj);
    }
}