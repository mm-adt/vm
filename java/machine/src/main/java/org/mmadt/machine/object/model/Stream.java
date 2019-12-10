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
package org.mmadt.machine.object.model;

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.model.type.Bindings;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.machine.object.model.util.QuantifierHelper;

import java.util.Iterator;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface Stream<A extends Obj> extends Iterable<A>, Pattern {

    @Override
    public default boolean test(final Obj object) {
        throw new UnsupportedOperationException("This is not supported because stream is going away");
    }

    @Override
    public default boolean match(final Bindings bindings, final Obj object) {
        // throw new UnsupportedOperationException("This is not supported because stream is going away");
        bindings.start();
        for (final A a : this) {
            if (!a.match(bindings, object)) {
                bindings.rollback();
                return false;
            }
        }
        bindings.commit();
        return true;
    }

    @Override
    public default boolean constant() {
        for (final A a : this) {
            if (!a.constant())
                return false;
        }
        return true;
    }

    ///////////////////

    public static boolean testStream(final Obj tester, final Obj testee) {
        return matchStream(null, tester, testee);
    }

    public static boolean matchStream(final Bindings bindings, final Obj tester, final Obj testee) {
        final boolean match = null != bindings;
        final Iterator<? extends Obj> ittyA = tester.iterable().iterator();
        final Iterator<? extends Obj> ittyB = testee.iterable().iterator();
        while (ittyA.hasNext() || ittyB.hasNext()) {
            final Obj a = ittyA.hasNext() ? ittyA.next() : null;
            final Obj b = ittyB.hasNext() ? ittyB.next() : TObj.none();
            //System.out.println(a + "--" + b);
            if (null != a) {
                if (!a.q().test(b.q()) ||
                        (!match && !a.test(b)) ||
                        (match && !a.match(bindings, b)))
                    return false;
            }

        }
        return QuantifierHelper.within(tester.q(), testee.q());

    }
}
