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

package org.mmadt.object.model.type;

import org.mmadt.object.model.Obj;
import org.mmadt.object.model.type.algebra.WithOrder;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PRel implements Pattern {

    public enum Rel {GT, GTE, EQ, NEQ, LT, LTE}

    private final Rel rel;
    private final Obj object;

    public PRel(final Rel rel, final Obj object) {
        this.rel = rel;
        this.object = object;
    }

    @Override
    public boolean test(final Obj object) {
        if (this.rel.equals(Rel.EQ))
            return this.object.test(object);
        else if (this.rel.equals(Rel.NEQ))
            return !this.object.test(object);
        else if (!(object instanceof WithOrder) || !(this.object instanceof WithOrder))
            return false;
        else {
            switch (this.rel) {
                case GT:
                    return ((WithOrder) object).gt(this.object).get();
                case GTE:
                    return ((WithOrder) object).gte(this.object).get();
                case LT:
                    return ((WithOrder) object).lt(this.object).get();
                case LTE:
                    return ((WithOrder) object).lte(this.object).get();
                default:
                    throw new IllegalStateException("Unknown relation: " + this.rel);
            }
        }
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        return this.test(object);
    }

    @Override
    public boolean constant() {
        return Rel.EQ.equals(this.rel);
    }

    @Override
    public PRel bind(final Bindings bindings) {
        return new PRel(this.rel, this.object.bind(bindings));
    }

    @Override
    public String toString() {
        return this.rel.name().toLowerCase() + "(" + this.object + ")";
    }

    @Override
    public int hashCode() {
        return this.rel.hashCode() ^ this.object.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof PRel &&
                this.rel.equals(((PRel) object).rel) &&
                this.object.equals(((PRel) object).object);

    }

    public Obj get() {
        return this.object;
    }
}
