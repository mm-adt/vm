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

package org.mmadt.machine.object.model;

import org.mmadt.language.Query;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.TStream;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.PConjunction;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.type.algebra.WithAnd;
import org.mmadt.machine.object.model.type.algebra.WithOr;
import org.mmadt.machine.object.model.type.algebra.WithProduct;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.machine.object.model.util.ObjectHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A Java representation of an mm-ADT {@code obj}.
 * This is the base structure for all mm-ADT objects.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Obj extends Pattern, Cloneable, WithAnd<Obj>, WithOr<Obj> {

    List<Object> TRAMPOLINE = new ArrayList<>(); // TODO: isolate this as its own tool (no more static)

    final Set<String> BASE_SYMBOLS = Set.of(Tokens.OBJ, Tokens.BOOL, Tokens.INT, Tokens.REAL, Tokens.STR, Tokens.LIST, Tokens.REC, Tokens.INST);

    public String symbol();

    public <B> B get();

    public default boolean named() {
        return null != this.symbol() && !BASE_SYMBOLS.contains(this.symbol());
    }

    public <B extends WithRing<B>> Q<B> q();

    public String label();

    public Inst access();

    public PMap<Inst, Inst> instructions();

    public PMap<Obj, Obj> members();

    public Bool eq(final Obj object);

    public default Bool neq(final Obj object) {
        return TBool.of(!this.eq(object).java());
    }

    public default <O extends Obj> O peek() {
        return (O) this.iterable().iterator().next();
    }

    public default <O extends Obj> O last() {
        final Iterator<O> itty = (Iterator<O>) this.iterable().iterator();
        O o = (O) TObj.none();
        while (itty.hasNext()) {
            o = itty.next();
        }
        return o;
    }

    public <O extends Obj> O type(final O type);

    public Obj type();

    public default Obj baseType() {
        return this.set(null);
    }

    public default Iterable<? extends Obj> iterable() {
        return null == this.get() ? List.of() : this.get() instanceof Stream ? this.<Stream>get() : List.of(this);
    }

    public <O extends Obj> O set(final Object object);

    public <O extends Obj> O q(final Q quantifier);

    public <O extends Obj> O label(final String variable);

    public <O extends Obj> O access(final Inst access);

    public <O extends Obj> O inst(final Inst instA, final Inst instB);

    public <O extends Obj> O symbol(final String symbol);

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts);

    @Override
    public default Obj bind(final Bindings bindings) {
        if (bindings.has(this.label()))
            return bindings.get(this.label());
        return this.insts(null == this.instructions() ? null : this.instructions().bind(bindings)).
                set(this.get() instanceof Pattern ? ((Pattern) this.get()).bind(bindings) : this.get()).
                access(this.access().equals(TInst.none()) ? null : this.access().bind(bindings));
    }

    @Override
    public default boolean test(final Obj object) {
        boolean root = TRAMPOLINE.isEmpty();
        if (TRAMPOLINE.contains(List.of(this, object)))
            return true;
        else
            TRAMPOLINE.add(List.of(this, object));
        try {
            if (TObj.none().equals(this) || TObj.none().equals(object))
                return this.q().test(object);
            else if (null != ObjectHelper.getName(this) && ObjectHelper.getName(this).equals(ObjectHelper.getName(object)))
                return true;
            else {
                final Object current = this.get();
                if (object.get() instanceof Stream)
                    return Stream.testStream(this, object);
                else if (this.get() instanceof PList && object.get() instanceof PList && !(this instanceof Inst))
                    return Stream.testStream(this.set(TStream.of(this.<PList>get())), object.set(TStream.of(object.<PList>get())));
                else if (null != current)
                    return current instanceof Pattern ? ((Pattern) current).test(object) : current.equals(object.get());
                else
                    return this.getClass().isAssignableFrom(object.getClass());
            }
        } finally {
            if (root)
                TRAMPOLINE.clear();
        }
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj object) {
        if (bindings.has(this.label()))
            return bindings.get(this.label()).test(object);
        else if (TObj.none().equals(this) || TObj.none().equals(object))
            return this.q().test(object);
        bindings.start();
        final Object current = this.get();
        if (null != current) {
            if (object.get() instanceof Stream) {
                if (!Stream.matchStream(bindings, this, object)) {
                    bindings.rollback();
                    return false;
                }
            } else if (current instanceof Pattern) {
                if (!((Pattern) current).match(bindings, object)) {
                    bindings.rollback();
                    return false;
                }
            } else if (!this.test(object)) {
                bindings.rollback();
                return false;
            }
        } else if (!this.getClass().isAssignableFrom(object.getClass())) {
            bindings.rollback();
            return false;
        }
        if (null != this.label())
            bindings.put(this.label(), object);
        if (this instanceof WithProduct)
            bindings.commit();
        return true;
    }

    public default Optional<Inst> inst(final Bindings bindings, final Inst inst) {
        ObjectHelper.members(this, bindings);
        if (null != this.instructions()) {
            for (final Map.Entry<Inst, Inst> entry : this.instructions().entrySet()) {
                if (entry.getKey().match(bindings, inst))
                    return Optional.of(entry.getValue().bind(bindings));
            }
        }
        if (null != this.type() && this.type().get() instanceof PConjunction) // TODO: this is why having a specialized variant class is important (so the logic is more recursive)
            return ((PConjunction) this.type().get()).inst(this, bindings, inst);
        else if (this.get() instanceof PConjunction)
            return ((PConjunction) this.get()).inst(this, bindings, inst);
        else
            return Optional.empty();
    }

    public Obj clone();

    //////////////

    public default boolean isType() {
        return !this.constant() && (TInst.none().equals(this.access()));//|| !this.access().constant());
    }

    public default boolean isReference() {
        return !this.constant() && !TInst.none().equals(this.access()) && this.access().constant();
    }

    public default boolean isInstance() {
        return this.constant() && (TInst.none() == this.access() || this.access().constant());
    }

    public default <O extends Obj> O access(final Query access) {
        return this.access(access.bytecode());
    }

    public default <O extends Obj> O q(final Q.Tag tag) {
        return this.q(tag.apply(this.q()));
    }

    public default <O extends Obj> O q(final Object low, final Object high) {
        return this.q(new TQ<>((WithRing) ObjectHelper.from(low), (WithRing) ObjectHelper.from(high)));
    }

    public default <O extends Obj> O q(final Object count) {
        return this.q((WithRing) ObjectHelper.from(count));
    }

    public default <O extends Obj> O q(final WithRing count) {
        return this.q(new TQ<>(count));
    }

}
