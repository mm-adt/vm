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

package org.mmadt.object.impl;

import org.mmadt.language.Query;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.object.impl.atomic.TBool;
import org.mmadt.object.impl.atomic.TInt;
import org.mmadt.object.impl.atomic.TReal;
import org.mmadt.object.impl.atomic.TStr;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.impl.composite.TLst;
import org.mmadt.object.impl.composite.TQuantifier;
import org.mmadt.object.impl.composite.TRec;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.Stream;
import org.mmadt.object.model.atomic.Bool;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.composite.Quantifier;
import org.mmadt.object.model.type.PList;
import org.mmadt.object.model.type.PMap;
import org.mmadt.object.model.type.POr;
import org.mmadt.object.model.type.Pattern;
import org.mmadt.object.model.type.feature.WithAnd;
import org.mmadt.object.model.type.feature.WithOr;
import org.mmadt.object.model.type.feature.WithRing;
import org.mmadt.object.model.util.ObjectHelper;
import org.mmadt.object.model.util.StringFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TObj implements Obj, WithAnd<Obj>, WithOr<Obj> {

    private static final TObj SOME = new TObj(null);
    private static final TObj ALL = new TObj(null).q(0, Integer.MAX_VALUE);
    private static final TObj NONE = new TObj(null).q(0);

    public static Obj some() {
        return SOME;
    }

    public static Obj all() {
        return ALL;
    }

    public static Obj none() {
        return NONE;
    }

    ////////

    protected String symbol = Tokens.OBJ;               // the name of the object's form
    protected Object value;                             // mutually exclusive with pattern (instance data)
    protected Pattern pattern;                          // mutually exclusive with value   (constraint data)
    protected Obj type;                                 // an object that abstractly defines this object's forms
    protected String variable;                          // the ~bind string (if retrieved via a bind)
    protected Quantifier<?> quantifier = TQuantifier.one;   // the 'amount' of this object bundle
    protected Inst access;                              // access to its physical representation
    protected PMap<Inst, Inst> instructions;            // rewrite rules for the vm instruction set (typically types)
    protected PMap<Obj, Obj> members;                   // the static members of the form (typically types)

    public TObj(final Object value) {
        if (null != value) {
            if (!(value instanceof Pattern) || ((Pattern) value).constant())
                this.value = value;
            else
                this.pattern = (Pattern) value;
        } else {
            this.value = null;
            this.pattern = null;
        }
        assert !(this.value instanceof Pattern) || ((Pattern) this.value).constant();
    }

    @Override
    public String symbol() {
        return this.symbol;
    }

    @Override
    public boolean constant() {
        return null != this.value;
    }

    @Override
    public <B> B get() {
        return null == this.value ? (B) this.pattern : (B) this.value;
    }

    @Override
    public <Q extends WithRing<Q>> Quantifier<Q> q() {
        return (Quantifier<Q>) this.quantifier;
    }

    @Override
    public String variable() {
        return this.variable;
    }

    @Override
    public Inst access() {
        return null == this.access ? TInst.none() : this.access;
    }

    @Override
    public PMap<Inst, Inst> instructions() {
        return null == this.type ? this.instructions : this.type.instructions();
    }

    @Override
    public PMap<Obj, Obj> members() {
        return null == this.type ? this.members : this.type.members();
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
        if (type.isType() && null != type.get() && !(this.get() instanceof Stream) ? !type.<Pattern>get().test(this) : !type.test(this)) {
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
                    access(ObjectHelper.access(this, object)).
                    q((Quantifier) this.q().and(object.q())).
                    as(ObjectHelper.mergeVariables(this, object));
    }

    @Override
    public Obj or(final Obj object) {
        if (Objects.equals(this, object))
            return this;
        else if (null != this.get() &&
                this.get().equals(object.get()) &&
                null == this.access &&
                null == this.instructions &&
                null == this.variable)
            return this.q((Quantifier)this.q().or(object.q()));
        else
            return ObjectHelper.root(this, object).set(POr.or(this.get() instanceof POr ? this.get() : this, object));
    }

    public static Obj from(final Object object) {
        if (object instanceof Obj)
            return (Obj) object;
        else if (object instanceof Boolean)
            return TBool.of(((Boolean) object));
        else if (object instanceof Integer)
            return TInt.of(((Integer) object));
        else if (object instanceof Float)
            return TReal.of(((Float) object));
        else if (object instanceof Double)
            return TReal.of(((Double) object).floatValue());
        else if (object instanceof String)
            return TStr.of((String) object);
        else if (object instanceof List)
            return TLst.of(new PList<>((List<Obj>) object));
        else if (object instanceof Map)
            return TRec.of(new PMap<>((Map<Obj, Obj>) object));
        else if (object instanceof Query) // TODO: see about getting rid of this as its not a supported built-in type
            return ((Query) object).bytecode();
        else
            throw new IllegalStateException("Unknown type: " + object.getClass());
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getClass(),
                this.value,
                this.pattern,
                this.variable,
                this.q(),
                this.access,
                this.instructions);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TObj &&
                ((this.q().isZero() && ((TObj) object).q().isZero()) ||
                        (this.getClass().equals(object.getClass()) &&
                                Objects.equals(this.value, ((TObj) object).value) &&
                                Objects.equals(this.pattern, ((TObj) object).pattern) &&
                                Objects.equals(this.variable, ((TObj) object).variable) &&
                                Objects.equals(this.q(), ((TObj) object).q()) &&
                                Objects.equals(this.access, ((TObj) object).access) &&
                                Objects.equals(this.instructions, ((TObj) object).instructions)));
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

    public <O extends Obj> O pop() {
        return this.get() instanceof Stream ? this.<Stream<O>>get().pop() : (O) this;
    }

    public <O extends Obj> O push(final O obj) {
        assert obj.getClass().equals(this.getClass());
        if (this.get() instanceof Stream)
            ((Stream<O>) this.get()).push(obj);
        else
            this.value = TStream.of(obj, this.clone());
        this.quantifier = (Quantifier) this.q().or(obj.q());
        return (O) this;
    }

    public <O extends TObj> O strip() { // TODO: gut this at some point
        final O clone = (O) this.clone();
        clone.access = null;
        clone.variable = null;
        clone.quantifier = TQuantifier.one;
        return clone;
    }

    @Override
    public <O extends Obj> O symbol(final String symbol) {
        final TObj clone = this.clone();
        clone.symbol = symbol;
        return (O) clone;
    }

    @Override
    public <O extends Obj> O set(final Object object) {
        final TObj clone = this.clone();
        if (null == object) {
            clone.value = null;
            clone.pattern = null;
        } else if (object instanceof Pattern && !((Pattern) object).constant()) {
            clone.value = null;
            clone.pattern = (Pattern) object;
        } else {
            clone.value = object;
            clone.pattern = null;
        }
        return (O) clone;
    }

    @Override
    public <O extends Obj> O q(final Quantifier quantifier) {
        final TObj clone = this.clone();
        clone.quantifier = quantifier;
        return (O) clone;
    }

    @Override
    public <O extends Obj> O as(final String variable) {
        final TObj clone = this.clone();
        clone.variable = variable;
        return (O) clone;
    }

    @Override
    public <O extends Obj> O access(final Inst access) {
        final TObj clone = this.clone();
        clone.access = TInst.none().equals(access) ? null : access;
        return (O) clone;
    }

    @Override
    public <O extends Obj> O inst(final Inst instA, final Inst instB) {
        final TObj clone = this.clone();
        clone.instructions = null == this.instructions ?
                new PMap<>() :
                new PMap<>(this.instructions);
        clone.instructions.put(instA, instB);
        return (O) clone;
    }

    public <O extends TObj> O member(final Obj name, final Obj value) {
        final O clone = (O) this.clone();
        clone.members = null == this.members ?
                new PMap<>() :
                new PMap<>(this.members);
        clone.members.put(name, value);
        return clone;
    }

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts) {
        final TObj clone = this.clone();
        clone.instructions = insts;
        return (O) clone;
    }
}
