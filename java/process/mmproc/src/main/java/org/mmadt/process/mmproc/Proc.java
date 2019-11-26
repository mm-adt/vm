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

package org.mmadt.process.mmproc;

import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.BarrierInstruction;
import org.mmadt.machine.object.model.composite.inst.BranchInstruction;
import org.mmadt.machine.object.model.composite.inst.FilterInstruction;
import org.mmadt.machine.object.model.composite.inst.FlatMapInstruction;
import org.mmadt.machine.object.model.composite.inst.InitialInstruction;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.inst.ReduceInstruction;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.process.mmproc.util.InMemoryReducer;
import org.mmadt.processor.Processor;
import org.mmadt.util.IteratorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Proc<S extends Obj> implements Processor<S> {

    private final List<Step<?, ?>> steps = new ArrayList<>();
    private Step<?, S> endStep;
    private SourceStep<Obj> startStep;
    private AtomicBoolean alive = new AtomicBoolean(Boolean.FALSE);

    Proc(final Inst inst) {
        Step<?, S> previousStep = EmptyStep.instance();
        for (final Inst function : inst.iterable()) {
            final Step nextStep;
            if (this.steps.isEmpty() && !(function instanceof InitialInstruction)) {
                this.startStep = new SourceStep<>();
                this.steps.add(this.startStep);
                previousStep = (Step) this.startStep;
            }

            // if (function instanceof RepeatBranch)
            //    nextStep = new RepeatStep<>(previousStep, (RepeatBranch<S>) function);
            if (function instanceof BranchInstruction)
                nextStep = new BranchStep<>(previousStep, (BranchInstruction<S, S>) function);
            else if (function instanceof FilterInstruction)
                nextStep = new FilterStep<>(previousStep, (FilterInstruction<S>) function);
            else if (function instanceof FlatMapInstruction)
                nextStep = new FlatMapStep<>(previousStep, (FlatMapInstruction<S, S>) function);
            else if (function instanceof MapInstruction)
                nextStep = new MapStep<>(previousStep, (MapInstruction<S, S>) function);
            else if (function instanceof InitialInstruction)
                nextStep = new InitialStep<>((InitialInstruction<S>) function);
            else if (function instanceof BarrierInstruction)
                nextStep = new BarrierStep<>(previousStep, (BarrierInstruction<S, Object>) function);
            else if (function instanceof ReduceInstruction)
                nextStep = new ReduceStep<>(previousStep, (ReduceInstruction<S, S>) function, new InMemoryReducer<>((ReduceInstruction<S, S>) function));
            else if (function instanceof SideEffectInstruction)
                nextStep = new SideEffectStep<>(previousStep, (SideEffectInstruction<S>) function);
            else
                throw new RuntimeException("You need a new step type:" + function + "::" + Arrays.toString(function.getClass().getInterfaces()));

            this.steps.add(nextStep);
            previousStep = nextStep;
        }
        this.endStep = previousStep;
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
    public Iterator<S> iterator(final S obj) {
        if (this.alive())
            throw Processor.Exceptions.processorIsCurrentlyRunning(this);

        this.alive.set(Boolean.TRUE);
        if (null != this.startStep)
            this.startStep.addStart(obj);
        return IteratorUtils.onLast(this.endStep, () -> this.alive.set(Boolean.FALSE));
    }


    @Override
    public void subscribe(final S obj, final Consumer<S> consumer) {
        if (this.alive())
            throw Processor.Exceptions.processorIsCurrentlyRunning(this);

        new Thread(() -> {
            final Iterator<S> iterator = this.iterator(obj);
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