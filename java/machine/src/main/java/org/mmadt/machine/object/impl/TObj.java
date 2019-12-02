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

package org.mmadt.machine.object.impl;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Type;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Real;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.type.algebra.WithAnd;
import org.mmadt.machine.object.model.type.algebra.WithOr;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TObj implements Obj, WithAnd<Obj>, WithOr<Obj> {

    public static Obj single(final Object... objects) {
        return new TObj(null);
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
            return Tokens.LST;
        return Tokens.OBJ;
    }

    ////////
    protected Object value;                             // mutually exclusive with pattern (instance data)
    private Q quantifier = null;                        // the 'amount' of this object bundle
    Type types;                                         // an object that abstractly defines this object's forms
    private boolean typeSet = false;                    // TODO: this is because we have a distinction of 'type not set' (will remove at of point)
    private Map<Str, Obj> state;                        // random access variable state associated with the computation

    public TObj(final Object value) {
        this.types = TType.of(TObj.getBaseSymbol(this));
        if (null != value) {
            if (!(value instanceof Pattern) || (((Pattern) value).constant() && !(value instanceof Inst)))
                this.value = value;
            else
                this.types = this.types.pattern((Pattern) value);
        }
        assert !(this.value instanceof Obj) && (!(this.value instanceof Pattern) || ((Pattern) this.value).constant()); // TODO: Remove when proved
    }

    @Override
    public String symbol() {
        return this.types.symbol();
    }

    @Override
    public boolean constant() {
        return null != this.value;
    }

    @Override
    public <B> B get() {
        return this.constant() ? (B) this.value : (B) this.types.pattern();
    }

    @Override
    public <B extends WithOrderedRing<B>> Q<B> q() {
        return null == this.quantifier ? TQ.ONE : this.quantifier;
    }

    @Override
    public String label() {
        return this.types.label();
    }

    @Override
    public <O extends Obj> O type(final O type) { // TODO: this might need to clone obj as branches may have different types (variants)
        if (this == type)
            throw new RuntimeException("An object is already its own type: " + this + "::" + type);
        if (null == type)
            this.typeSet = false;
        else {
            if (!type.test(this))
                throw new RuntimeException("The specified type does not match the object: " + type + "::" + this);
            this.types = ((TObj) type).types;
            this.typeSet = true;
        }
        return (O) this;
    }

    @Override
    public Map<Str, Obj> state() {
        return null == this.state ? Map.of() : this.state;
    }

    @Override
    public <O extends Obj> O state(final Str name, final Obj obj) {
        if (!name.isInstance())
            return (O) this;
        final TObj clone = this.clone();
        clone.state = new LinkedHashMap<>();
        if (null != this.state)
            clone.state.putAll(this.state);
        clone.state.put(name, obj);
        return (O) clone;
    }

    @Override
    public Obj type() {
        if (this.typeSet) { // TODO: remove when types are always required
            final TObj clone = this.clone();
            clone.value = null;
            return clone;
        } else
            return null;
    }

    @Override
    public Obj and(final Obj object) {
        if (Objects.equals(this, object))
            return this;
        else
            return ObjectHelper.root(this, object).
                    set(ObjectHelper.andValues(this, (TObj) object)).
                    q(this.q().mult(object.q())).
                    label(ObjectHelper.mergeLabels(this, object));
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
        } else if (object instanceof Pattern && (!((Pattern) object).constant()) || object instanceof Inst) {
            clone.value = null;
            clone.types = this.types.pattern((Pattern) object);
        } else {
            clone.value = object;
            clone.types = this.types.pattern(null);
        }
        assert !(clone.value instanceof Inst); // TODO: Remove when proved
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
        return (O) clone.state(TStr.of(variable), clone);
    }

    @Override
    public <O extends Obj> O access(final Inst access) {
        final TObj clone = this.clone();
        clone.types = this.types.access(access);
        return (O) clone;
    }

    @Override
    public Inst access() {
        final Inst inst = this.types.access();
        return null == inst ? IdInst.create().domainAndRange(this, this) : inst; // instances require domain/range spec on [id] access
    }

    ///////////////////////////////////////////////////////////////////////////////////

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
        return StringFactory.obj(this);
    }

    @Override
    public TObj clone() {
        try {
            final TObj clone = (TObj) super.clone();
            // clone.types = clone.types.label(null);
            return clone;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Bool a(final Obj obj) {
        return TBool.via(this).set(obj.equals(this) || obj.test(this));
    }

    @Override
    public Bool eq(final Obj object) {
        // return TBool.of(Objects.equals(this.get(), object.get()));
        return TBool.via(this).set(Objects.equals(this.get(), object.get()));
    }

    @Override
    public Bool neq(final Obj object) {
        return TBool.via(this).set(!Objects.equals(this.get(), object.get()));
    }

}
