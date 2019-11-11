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

import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.BranchInstruction;
import org.mmadt.machine.object.model.composite.inst.FilterInstruction;
import org.mmadt.machine.object.model.composite.inst.InitialInstruction;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.processor.Processor;
import org.mmadt.processor.ProcessorFactory;
import org.mmadt.util.EmptyIterator;
import org.mmadt.util.IteratorUtils;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class FastProcessor<S extends Obj, E extends Obj> implements Processor<S, E>, ProcessorFactory {

    protected final Inst bytecode;

    public FastProcessor(final Inst inst) {
        this.bytecode = inst;
    }

    private static <E extends Obj> Iterator<E> processTraverser(final E start, final Inst inst) {
        try {
            if (inst instanceof BranchInstruction)
                return ((BranchInstruction<E, E>) inst).distribute(start);
            else if (inst instanceof FilterInstruction)
                return ((FilterInstruction<E>) inst).testt(start) ? IteratorUtils.of(start) : EmptyIterator.instance();
            else if (inst instanceof MapInstruction)
                return IteratorUtils.of(((MapInstruction<E, E>) inst).apply(start));
            else if (inst instanceof InitialInstruction)
                return ((InitialInstruction<E>) inst).gett();
            else if (inst instanceof ReduceInstruction)
                return IteratorUtils.of(IteratorUtils.stream(((List<E>) start.get()).iterator()).
                        reduce(((ReduceInstruction<E, E>) inst).getInitialValue(), ((ReduceInstruction<E, E>) inst)::apply));
            else if (inst instanceof SideEffectInstruction) {
                ((SideEffectInstruction) inst).accept(start);
                return IteratorUtils.of(start);
            } else
                throw new UnsupportedOperationException("This is not implemented yet: " + inst + "--" + start);
        } catch (final ClassCastException e) {
            throw Processor.Exceptions.objDoesNotSupportInst(start, inst);
        }
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public void stop() {
    }

    @Override
    public Iterator<E> iterator(final Iterator<S> starts) {
        Stream<E> stream = (Stream<E>) IteratorUtils.stream(starts);
        for (final Inst inst : this.bytecode.iterable()) {
            if (inst instanceof ReduceInstruction)
                stream = IteratorUtils.stream(FastProcessor.processTraverser((E) TLst.of(stream.collect(Collectors.toList())), inst));
            else
                stream = stream.flatMap(s -> IteratorUtils.stream(FastProcessor.processTraverser(s, inst)));

            stream = stream.filter(s -> !s.q().isZero());
        }
        return stream.map(s -> (E) s.label(this.bytecode.label())).iterator();
    }

    @Override
    public void subscribe(final Iterator<S> starts, final Consumer<E> consumer) {
        this.iterator(starts).forEachRemaining(consumer);
    }
}