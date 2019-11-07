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

package org.mmadt.process.mmproc;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.FilterInstruction;
import org.mmadt.machine.object.model.composite.inst.InitialInstruction;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.process.mmproc.util.InMemoryReducer;
import org.mmadt.processor.Processor;
import org.mmadt.processor.compiler.IR;
import org.mmadt.util.IteratorUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Proc<S extends Obj, E extends Obj> implements Processor<S, E> {

    private final List<Step<?, ?>> steps = new ArrayList<>();
    private Step<?, E> endStep;
    private SourceStep<S> startStep;
    private AtomicBoolean alive = new AtomicBoolean(Boolean.FALSE);

    public Proc(final IR<S, E> compilation) {
        Step<?, S> previousStep = EmptyStep.instance();
        for (final Inst function : compilation.bytecode().iterable()) {
            final Step nextStep;
            if (this.steps.isEmpty() && !(function instanceof InitialInstruction)) {
                this.startStep = new SourceStep<>();
                this.steps.add(this.startStep);
                previousStep = this.startStep;
            }

            // if (function instanceof RepeatBranch)
            //    nextStep = new RepeatStep<>(previousStep, (RepeatBranch<S>) function);
            //else if (function instanceof BranchFunction)
            //    nextStep = new BranchStep<>(previousStep, (BranchFunction<S, E>) function);
            if (function instanceof FilterInstruction)
                nextStep = new FilterStep<>(previousStep, (FilterInstruction<S>) function);
                // else if (function instanceof FlatMapFunction)
                //     nextStep = new FlatMapStep<>(previousStep, (FlatMapFunction<S, E>) function);
            else if (function instanceof MapInstruction)
                nextStep = new MapStep<>(previousStep, (MapInstruction<S, E>) function);
            else if (function instanceof InitialInstruction)
                nextStep = new InitialStep<>((InitialInstruction<S>) function);
                //  else if (function instanceof BarrierFunction)
                //      nextStep = new BarrierStep<>(previousStep, (BarrierFunction<S, E, Object>) function);
            else if (function instanceof ReduceInstruction)
                nextStep = new ReduceStep<>(previousStep, (ReduceInstruction<S, E>) function, new InMemoryReducer<>((ReduceInstruction<S, E>) function));
            else if (function instanceof SideEffectInstruction)
                nextStep = new SideEffectStep<>(previousStep, (SideEffectInstruction<S>) function);
            else
                throw new RuntimeException("You need a new step type:" + function);

            this.steps.add(nextStep);
            previousStep = nextStep;
        }
        this.endStep = (Step<?, E>) previousStep;
    }

    @Override
    public void stop() {
        this.alive.set(Boolean.FALSE);
        for (final Step<?, ?> step : this.steps) {
            step.reset();
        }
    }

    @Override
    public boolean alive() {
        return this.alive.get();
    }

    @Override
    public Iterator<E> iterator(final Iterator<S> starts) {
        if (this.alive())
            throw Processor.Exceptions.processorIsCurrentlyRunning(this);

        this.alive.set(Boolean.TRUE);
        if (null != this.startStep)
            starts.forEachRemaining(this.startStep::addStart);
        return IteratorUtils.onLast(this.endStep, () -> this.alive.set(Boolean.FALSE));
    }


    @Override
    public void subscribe(final Iterator<S> starts, final Consumer<E> consumer) {
        if (this.alive())
            throw Processor.Exceptions.processorIsCurrentlyRunning(this);

        new Thread(() -> {
            final Iterator<E> iterator = this.iterator(starts);
            while (iterator.hasNext()) {
                if (!this.alive.get())
                    break;
                consumer.accept(iterator.next());
            }
        }).start();
    }

    @Override
    public String toString() {
        return this.steps.toString();
    }
}