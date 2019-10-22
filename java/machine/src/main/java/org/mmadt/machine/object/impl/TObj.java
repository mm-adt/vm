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
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
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

    ////////


    protected Object value;                             // mutually exclusive with pattern (instance data)
    protected Pattern pattern;                          // mutually exclusive with value   (constraint data)
    protected Obj type;                                 // an object that abstractly defines this object's forms
    protected String variable;                          // the ~bind string (if retrieved via a bind)
    protected Q<?> quantifier;                          // the 'amount' of this object bundle
    // TODO: all fields below are type structures and should be bundled into a single field
    protected Type types;
    protected String symbol = Tokens.OBJ;

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

    private Type getType() {
        if (null == this.types)
            this.types = TType.of();
        return this.types;
    }

    @Override
    public String symbol() {
        return this.symbol;
    }

    @Override
    public boolean constant() {
        return null != this.value && !(this.value instanceof Inst);
    }

    @Override
    public <B> B get() {
        return null == this.value ? (B) this.pattern : (B) this.value;
    }

    @Override
    public <B extends WithRing<B>> Q<B> q() {
        return null == this.quantifier ? TQ.ONE : (Q<B>) this.quantifier;
    }

    @Override
    public String variable() {
        return this.variable;
    }

    @Override
    public Inst access() {
        return this.getType().access();
    }

    @Override
    public PMap<Inst, Inst> instructions() {
        return this.getType().instructions();
    }

    @Override
    public PMap<Obj, Obj> members() {
        return this.getType().members();
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
                    q(this.q().and(object.q())).
                    as(ObjectHelper.mergeVariables(this, object));
    }

    @Override
    public Obj or(final Obj object) {
        if (Objects.equals(this, object))
            return this;
        else if (null != this.get() &&
                this.get().equals(object.get()) &&
                TInst.none().equals(this.getType().access()) &&
                null == this.getType().instructions() &&
                null == this.variable)
            return this.q(this.q().or(object.q()));
        else
            return ObjectHelper.root(this, object).set(POr.or(this.get() instanceof POr ? this.get() : this, object));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.getClass(),
                this.value,
                this.pattern,
                this.variable,
                this.q(),
                this.getType());
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
                                Objects.equals(this.getType(), ((TObj) object).getType())));
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
        this.quantifier = this.q().or(obj.q());
        return (O) this;
    }

    public <O extends TObj> O strip() { // TODO: gut this at some point
        final O clone = (O) this.clone();
        clone.types = this.getType().access(null);
        clone.variable = null;
        clone.quantifier = this.q().one();
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
    public <O extends Obj> O q(final Q quantifier) {
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
        clone.types = this.getType().access(access);
        return (O) clone;
    }

    @Override
    public <O extends Obj> O inst(final Inst instA, final Inst instB) {
        final TObj clone = this.clone();
        clone.types = this.getType().inst(instA, instB);
        return (O) clone;
    }

    public <O extends TObj> O member(final Obj name, final Obj value) {
        final O clone = (O) this.clone();
        clone.types = this.getType().member(name,value);
        return clone;
    }

    public <O extends Obj> O insts(final PMap<Inst, Inst> insts) {
        final TObj clone = this.clone();
        clone.types = this.getType().insts(insts);
        return (O) clone;
    }
}
