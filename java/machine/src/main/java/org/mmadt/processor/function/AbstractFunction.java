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

package org.mmadt.processor.function;

import org.mmadt.object.model.Obj;
import org.mmadt.object.model.type.Quantifier;
import org.mmadt.object.model.util.StringFactory;
import org.mmadt.processor.compiler.Argument;

import java.util.Objects;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class AbstractFunction implements QFunction {

    private Argument<?, ?>[] arguments;
    private Quantifier quantifier;
    private String label;

    public AbstractFunction(final Quantifier quantifier, final String label, final Argument<?, ?>... arguments) {
        this.quantifier = quantifier;
        this.label = label;
        this.arguments = arguments;
    }

    @Override
    public Quantifier quantifier() {
        return this.quantifier;
    }

    @Override
    public String label() {
        return this.label;
    }

    public <S extends Obj, E extends Obj> Argument<S, E> argument(final int index) {
        return (Argument<S, E>) this.arguments[index];
    }

    @Override
    public String toString() {
        return StringFactory.function(this, this.arguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getClass(), this.quantifier, this.label, this.arguments);
    }

    @Override
    public boolean equals(final Object other) {
        return other != null && other.getClass().equals(this.getClass()) && this.hashCode() == other.hashCode();
    }

    @Override
    public AbstractFunction clone() {
        try {
            // TODO: individually clone arguments?
            return (AbstractFunction) super.clone();
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}