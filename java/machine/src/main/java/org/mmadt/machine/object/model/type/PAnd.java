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

import org.mmadt.machine.object.model.Obj;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class PAnd extends PConjunction {

    PAnd(final List<Pattern> patterns) {
        super(patterns);
    }

    @Override
    public boolean constant() {
        for (final Pattern pattern : this.predicates()) {
            if (!pattern.constant())
                return false;
        }
        return true;
    }

    @Override
    public boolean test(final Obj object) {
        for (final Pattern predicate : this.patterns) {
            if (!predicate.test(object))
                return false;
        }
        return true;
    }

    @Override
    public boolean match(final Bindings bindings, final Obj object) {
        for (final Pattern predicate : this.patterns) {
            if (!predicate.match(bindings, object))
                return false;
        }
        return true;
    }

    public static Pattern and(final Pattern a, final Pattern b) {
        if (null == a)
            return b;
        else if (null == b)
            return a;
        else if (a.equals(b))
            return a;
        else {
            final List<Pattern> predicates = new ArrayList<>();
            if (a instanceof PAnd)
                predicates.addAll(((PAnd) a).patterns);
            else
                predicates.add(a);
            if (b instanceof PAnd) {
                for (final Pattern predicate : predicates) {
                    if (!predicates.contains(predicate))
                        predicates.add(predicate);
                }
            } else {
                if (!predicates.contains(b))
                    predicates.add(b);
            }
            return predicates.size() == 1 ? predicates.get(0) : new PAnd(predicates);
        }
    }
}
