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

package org.mmadt.machine.object.impl;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.Type;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PConjunction;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.POr;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.type.algebra.WithAnd;
import org.mmadt.machine.object.model.type.algebra.WithOr;
import org.mmadt.machine.object.model.type.algebra.WithRing;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TObj implements Obj, WithAnd<Obj>, WithOr<Obj> {

    private static final TObj SOME = new TObj(null);

    public static Obj some() {
        return SOME;
    }

    public static Obj all() {
        return new TObj(null).q(0, Integer.MAX_VALUE);
    }

    public static Obj none() {
        return new TObj(null).q(TInt.zeroInt());
    }

    private static String getBaseSymbol(final Obj obj) {
        if (obj instanceof Bool)
            return Tokens.BOOL;
        if (obj instanceof Str)
            return Tokens.STR;
        if (obj instanceof Int)
            return Tokens.INT;
        if (obj instanceof Real)
            return Tokens.REAL;
        if (obj instanceof Rec)
            return Tokens.REC;
        if (obj instanceof Inst)
            return Tokens.INST;
        if (obj instanceof Lst)
            return Tokens.LIST;
        return Tokens.OBJ;
    }

    ////////
    protected Object value;                             // mutually exclusive with pattern (instance data)
    protected Q quantifier = TQ.ONE;                    // the 'amount' of this object bundle
    protected Type types;                               // an object that abstractly defines this object's forms
    protected Obj type;                                 // TODO: gut

    public TObj(final Object value) {
        this.types = TType.of(TObj.getBaseSymbol(this));
        if (null != value) {
            if (!(value instanceof Pattern) || (((Pattern) value).constant() && !(value instanceof Inst)))
                this.value = value;
            else
                this.types = this.types.pattern((Pattern) value);
        }
        assert !(this.value instanceof Pattern) || ((Pattern) this.value).constant();

    }

    @Override
    public String symbol() {
        return this.types.symbol();
    }

    @Override
    public boolean constant() {
        return null != this.value &&
                !(this.value instanceof PConjunction) && !(this.value instanceof Inst); // TODO: remove pattern tests when stabilized
    }

    @Override
    public <B> B get() {
        return this.constant() ? (B) this.value : (B) this.types.pattern();
    }

    @Override
    public <B extends WithRing<B>> Q<B> q() {
        return this.quantifier;
    }

    @Override
    public String label() {
        return this.types.label();
    }

    @Override
    public Inst access() {
        return this.types.access();
    }

    @Override
    public PMap<Inst, Inst> instructions() {
        return this.types.instructions();
    }

    @Override
    public PMap<Obj, Obj> members() {
        return this.types.members();
    }

    @Override
    public Bool eq(final Obj object) {
        return TBool.of(Objects.equals(this.get(), object.get()));
    }

    @Override
    public <O extends Obj> O type(final O type) { // TODO: this should cause a clone as branches will have different types
        if (this == type)
            throw new RuntimeException("An object is already its own type: " + this + "::" + type);
        if (null == type) {
            this.type = null;
            return (O) this;
        }

        final Obj previous = this.type;
        this.type = type;
        if (null != type.get() && !(this.get() instanceof Stream) ? !type.<Pattern>get().test(this) : !type.test(this)) {
            this.type = previous;
            throw new RuntimeException("The specified type doesn't match the object: " + type + "::" + this);
        }
        return (O) this;
    }

    @Override
    public Obj type() {
        return this.type;
    }

    @Override
    public Obj and(final Obj object) {
        if (Objects.equals(this, object))
            return this;
        else
            return ObjectHelper.root(this, object).
                    set(ObjectHelper.andValues(this, (TObj) object)).
                    q(this.q().and(object.q())).
                    label(ObjectHelper.mergeLabels(this, object));
    }

    @Override
    public Obj or(final Obj object) {
        if (Objects.equals(this, object))
            return this;
        else if (null != this.get() &&
                this.get().equals(object.get()) &&
                TInst.none().equals(this.types.access()) &&
                null == this.types.instructions())
            return this.q(this.q().or(object.q()));
        else
            return ObjectHelper.root(this, object).set(POr.or(this.get() instanceof POr ? this.get() : this, object));
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.q(), this.types);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TObj &&
                ((this.q().isZero() && ((TObj) object).q().isZero()) ||
                        (Objects.equals(this.value, ((TObj) object).value) &&
                                Objects.equals(this.q(), ((TObj) object).q()) &&
                                Objects.equals(this.types, ((TObj) object).types)));
    }

    @Override
    public String toString() {
        return StringFactory.object(this);
    }

    @Override
    public TObj clone() {
        try {
            return (TObj) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public <O extends TObj> O strip() { // TODO: gut this at some point
        final O clone = (O) this.clone();
        clone.types = this.types.access(null).label(null);
        clone.quantifier = this.q().one();
        return clone;
    }

    @Override
    public <O extends Obj> O symbol(final String symbol) {
        final TObj clone = this.clone();
        clone.types = this.types.symbol(symbol);
        return (O) clone;
    }

    @Override
    public <O extends Obj> O set(final Object object) {
        final TObj clone = this.clone();
        if (null == object) {
            clone.value = null;
            clone.types = this.types.pattern(null);
        } else if (object instanceof Pattern && !((Pattern) object).constant()) {
            clone.value = null;
            clone.types = this.types.pattern((Pattern) object);
        } else {
            clone.value = object;
            clone.types = this.types.pattern(null);
        }
        return (O) clone;
    }

    @Override
    public <O extends Obj> O q(final Q quantifier) {
        final TObj clone = this.clone();
        clone.quantifier = quantifier;
        return (O) clone;
    }

    @Override
    public <O extends Obj> O label(final String variable) {
        final TObj clone = this.clone();
        clone.types = this.types.label(variable);
        return (O) clone;
    }

    @Override
    public <O extends Obj> O access(final Inst access) {
        final TObj clone = this.clone();
        clone.types = this.types.access(access);
        return (O) clone;
    }

    @Override
    public <O extends Obj> O inst(final Inst instA, final Inst instB) {
        final TObj clone = this.clone();
        clone.types = this.types.inst(instA, instB);
        return (O) clone;
    }

    public <O extends TObj> O member(final Obj name, final Obj value) {
        final O clone = (O) this.clone();
        clone.types = this.types.member(name, value);
        return clone;
    }

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts) {
        final TObj clone = this.clone();
        clone.types = this.types.insts(insts);
        return (O) clone;
    }
}
