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

package org.mmadt.language.compiler;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.util.BytecodeHelper;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Rewriting {

    public static Inst rewrite(final Model model, final Obj domain, final Inst oldBc) {
        return Rewriting.rewrite(new Bindings(), domain, oldBc, model);
    }

    public static Inst rewrite(final Model model, final Inst oldBc) {
        return Rewriting.rewrite(new Bindings(), TObj.none(), oldBc, model);// the domain starts off object{0} (its an initial)
    }

    static Inst rewrite(final Model model, final Inst oldBc, final Bindings bindings) {
        return Rewriting.rewrite(bindings, TObj.none(), oldBc, model);// the domain starts off object{0} (its an initial)
    }

    private static Inst rewrite(final Bindings bindings, Obj domain, final Inst oldBc, final Model model) {
        final LinkedList<Inst> newBc = new LinkedList<>(); // create an empty queue of instructions that will later be wrapped as bytecode
        Obj range;
        boolean inReferenceGraph = false;
        final LinkedList<Inst> refBc = new LinkedList<>();
        for (final Inst oldInst : oldBc.iterable()) {
            final Inst bcMatch = BytecodeHelper.inst(new Bindings(bindings), model, oldInst, domain); // get an instruction match on the type or its symbolic form. if no where, you give back the instruction bound by bindings
            // if the bytecode is empty, that is a noop (move on to the next instruction -- don't mess with domain/range)
            for (final Inst oldInst2 : bcMatch.iterable()) {
                final boolean ref = oldInst2.opcode().get().equals(Tokens.REF);
                range = ref ? BytecodeHelper.reference(oldInst2) : Instructions.getRange(oldInst2, domain, model);
                if (ref) { // the current instruction is a reference and thus, traversing the referencing graph
                    inReferenceGraph = true;
                    if (BytecodeHelper.isSubset(TInst.of(newBc), ObjectHelper.access(range))) { // if the bytecode is a subset of the current reference, remove it (the reference handles the processing)
                        newBc.clear();
                    }
                    int counter = 0;
                    while (!refBc.isEmpty() && // removes previous references that are a sub-references of the current reference
                            ++counter < ObjectHelper.access(range).q().high().<Integer>get() &&
                            BytecodeHelper.isSubset(refBc.peekLast().opcode().get().equals(Tokens.REF) ?
                                    BytecodeHelper.reference(refBc.peekLast()).access() : refBc.peekLast(), ObjectHelper.access(range))) {
                        domain = refBc.removeLast().domain();
                    }
                    Rewriting.insertInstruction(model, refBc, ObjectHelper.access(range).q().equals(domain.q().zero()) ? oldInst : oldInst2, domain, range);
                    domain = range;
                } else { // if its not a ref, then drain the reference graph
                    if (inReferenceGraph) {
                        if (domain.inst(bindings, oldInst).isEmpty()) // only allow those access-less references that did not lead to an instruction bind
                            drainReferences(model, refBc, newBc);
                        if (null != newBc.peekLast())
                            domain = newBc.getLast().range();
                        refBc.clear();
                        inReferenceGraph = false;
                    }
                    Rewriting.insertInstruction(model, newBc, oldInst2, domain, range);
                    domain = null != newBc.peekLast() ? newBc.getLast().range() : range;
                }
            }
        }
        drainReferences(model, refBc, newBc);
        return TInst.of(newBc);
    }

    private static void drainReferences(final Model model, final List<Inst> refBc, final List<Inst> newBc) {
        for (final Inst i : refBc) {
            Rewriting.insertInstruction(model, newBc, i, newBc.isEmpty() ? TObj.none() : i.domain(), i.range());
        }
        refBc.clear();
    }

    private static void insertInstruction(final Model model, final List<Inst> newBc, final Inst oldInst, final Obj domain, final Obj range) {
        if (range.constant() && range.q().constant()) { // TODO: ghetto, but this is the right idea.
            newBc.clear();
            newBc.add(TInst.of(Tokens.START, (Object) range).range(range.peek())); // if the type is a constant, then use the constant! (you have derived a solution through compilation)
        } else {
            final PList<Obj> args = new PList<>();
            for (final Obj arg : oldInst.args()) {
                if (arg instanceof Inst)
                    args.add(Rewriting.rewrite(model, domain.q(domain.q().one()), (Inst) arg)); // QONE assumes map nests
                else
                    args.add(arg);
            }
            final Inst newInst = TInst.of(oldInst.opcode(), args).q(oldInst.q());
            newBc.add(newInst.domainAndRange(domain, Instructions.getRange(newInst, domain, model))); // clone the old instruction with new domain/range modifiers
        }
    }
}

