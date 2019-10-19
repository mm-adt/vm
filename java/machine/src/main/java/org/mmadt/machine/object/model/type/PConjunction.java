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

package org.mmadt.machine.object.model.type;

import org.mmadt.machine.object.impl.TSym;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.util.StringFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public abstract class PConjunction implements Pattern {

    final List<Pattern> patterns;

    PConjunction(final List<Pattern> patterns) {
        this.patterns = patterns;
    }

    public List<Pattern> predicates() {
        return Collections.unmodifiableList(this.patterns);
    }

    @Override
    public PConjunction bind(final Bindings bindings) {
        final List<Pattern> patterns = new ArrayList<>();
        for (final Pattern p : this.predicates()) {
            patterns.add(p.bind(bindings));
        }
        return this instanceof PAnd ? new PAnd(patterns) : new POr(patterns);
    }

    public <A> A get(final int index) {
        return (A) this.predicates().get(index);
    }

    @Override
    public String toString() {
        return StringFactory.conjunction(this);
    }

    @Override
    public int hashCode() {
        return this.patterns.hashCode() ^ this.getClass().hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof PConjunction
                && object.getClass().equals(this.getClass())
                && new HashSet<>(this.patterns).equals(new HashSet<>(((PConjunction) object).patterns));
    }

    public Optional<Inst> inst(final Obj object, final Bindings bindings, final Inst inst) {
        for (final Pattern p : this.predicates()) {
            if (!object.constant() || p.test(object)) {
                if (p.isObj()) {
                    Optional<Inst> match = p.asObj().inst(bindings, inst);
                    if (match.isPresent())
                        return match;
                    if (p instanceof TSym && null != ((TSym) p).getObject()) { // TODO: perhaps symbols shouldn't have instructions on them?!
                        match = TSym.fetch(p.asObj()).inst(bindings, inst);
                        if (match.isPresent())
                            return match;
                    }
                } else if (p instanceof PConjunction) {
                    final Optional<Inst> match = ((PConjunction) p).inst(object, bindings, inst);
                    if (match.isPresent())
                        return match;
                }
            }
        }
        return Optional.empty();
    }
}
