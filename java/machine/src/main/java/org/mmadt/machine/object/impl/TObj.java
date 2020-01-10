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
import org.mmadt.machine.object.impl.composite.inst.filter.IdInst;
import org.mmadt.machine.object.impl.ext.composite.TPair;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Pattern;
import org.mmadt.machine.object.model.Type;
import org.mmadt.machine.object.model.atomic.Bool;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.ext.algebra.WithAnd;
import org.mmadt.machine.object.model.ext.algebra.WithOr;
import org.mmadt.machine.object.model.ext.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.ext.composite.Pair;
import org.mmadt.machine.object.model.util.ModelHelper;
import org.mmadt.machine.object.model.util.ObjectHelper;
import org.mmadt.machine.object.model.util.QuantifierHelper;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class TObj implements Obj, WithAnd<Obj>, WithOr<Obj> {

    public static Obj single(final Object... objects) {
        return new TObj(null);
    }

    public static Obj sym(final String label) {
        final TObj obj = new TObj(null);
        obj.binding = label;
        return obj;
    }

    public static Obj all() {
        return new TObj(null).q(0, Integer.MAX_VALUE);
    }

    public static Obj none() {
        return new TObj(null).q(TInt.zeroInt());
    }

    ////////
    protected Type type;                               // an object that abstractly defines this object's forms
    protected Object value;                            // the value of the obj
    private  Inst predicate;                           // the [is,[pred]] further constraining the obj
    private WithOrderedRing quantifier = null;         // the 'amount' of this object bundle
    public String binding = null;                      // the variable binding associated with the current obj
    private Inst ref = null;                           // the instructions necessary to generate the obj ("the reference").
    private Model model = new TModel();                // the model that is used to interpret this obj ("the traverser")

    public TObj(final Object value) {
        this.type = TType.of(Tokens.getBaseSymbol(this));
        if (null != value) {
            if (!(value instanceof Pattern) || (((Pattern) value).constant() && !(value instanceof Inst)))
                this.value = value;
            else
                this.type = this.type.pattern((Pattern) value);
        }
        assert !(this.value instanceof Obj) && (!(this.value instanceof Pattern) || ((Pattern) this.value).constant()); // TODO: Remove when proved
    }

    @Override
    public Model model() {
        return this.model;
    }

    @Override
    public <O extends Obj> O model(final Model model) {
        final TObj clone = this.clone();
        clone.model = model;
        return (O) clone; // : (O) ModelInst.create("xxx", TRec.of(), IdInst.create()).attach(clone);
    }

    @Override
    public <O extends Obj> O type() {
        if (null != this.type.pattern() && this.type.pattern() instanceof Obj)
            return (O) this.type.pattern();
        else {
            final TObj clone = this.clone();
            clone.value = null;
            return (O) clone;
        }
    }

    @Override
    public String symbol() {
        return this.type.bind();
    }

    @Override
    public boolean constant() {
        return null != this.value;
    }

    @Override
    public <B> B get() {
        return this.constant() ? (B) this.value : (B) this.type.pattern();
    }

    @Override
    public WithOrderedRing q() {
        return null == this.quantifier ? QuantifierHelper.ONE : this.quantifier;
    }

    @Override
    public String binding() {
        return this.binding;
    }

    @Override
    public Obj and(final Obj object) {
        if (Objects.equals(this, object))
            return this;
        else
            return ObjectHelper.root(this, object).
                    set(ObjectHelper.andValues(this, (TObj) object)).
                    q(this.q().mult(object.q())).
                    bind(ObjectHelper.mergeLabels(this, object));
    }

    @Override
    public <O extends Obj> O symbol(final String symbol) {
        final TObj clone = this.clone();
        clone.type = this.type.bind(symbol);
        if (!this.model.apply(TObj.sym(symbol)).isNone()) {
            clone.type = clone.type.pattern(this.model.apply(TObj.sym(symbol)));
            clone.typeCheck();
        }
        return (O) clone;
    }

    @Override
    public <O extends Obj> O set(final Object object) {
        final TObj clone = this.clone();
        clone.binding = null;
        if (null == object) {
            clone.value = null;
            clone.type = clone.type.pattern(null);
        } else if (object instanceof Pattern && (!((Pattern) object).constant()) || object instanceof Inst) {
            clone.value = null;
            clone.type = clone.type.pattern((Pattern) object);
        } else {
            clone.value = object;
            clone.type = clone.type.pattern(null);
        }
        assert !(clone.value instanceof Inst); // TODO: Remove when proved
        return (O) clone;
    }

    @Override
    public <O extends Obj> O q(final WithOrderedRing quantifier) {
        final TObj clone = this.clone();
        clone.quantifier = quantifier instanceof Pair ? quantifier : TPair.of(quantifier, quantifier);
        return (O) clone;

    }

    @Override
    public <O extends Obj> O bind(final String binding) {
        final TObj clone = this.clone();
        clone.binding = binding;
        // return null == binding ? (O) clone : (O)clone.model(clone.model.write(clone));
        return null == binding ? (O) clone : ModelHelper.match((O) clone);
    }

    @Override
    public <O extends Obj> O ref(final Inst reference) {
        final TObj clone = this.clone();
        clone.type = this.type.ref(reference);
        return (O) clone;
    }

    @Override
    public Inst ref() {
        final Inst inst = this.type.ref();
        return null == inst ? IdInst.create().domainAndRange(this, this) : inst; // instances require domain/range spec on [id] access
    }

    @Override
    public void typeCheck() {
        if (null != this.type.pattern() && !this.type.pattern().test(this))
            throw new RuntimeException("The left obj does not subsume the right obj: " + this.type.pattern() + "~" + this);
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.q().toString(), this.type);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof TObj &&
                ((this.q().isZero() && ((TObj) object).q().isZero()) ||
                        (Objects.equals(this.value, ((TObj) object).value) &&
                                Objects.equals(this.binding, ((TObj) object).binding) &&
                                Objects.equals(this.q(), ((TObj) object).q()) &&
                                Objects.equals(this.type, ((TObj) object).type))); // TODO: model equality
    }

    @Override
    public String toString() {
        return StringFactory.obj(this);
    }

    @Override
    public TObj clone() {
        try {
            return (TObj) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Bool a(final Obj obj) {
        return TBool.via(this).set(obj.equals(this) || obj.test(this));
    }

}
