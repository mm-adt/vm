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

package org.mmadt.storage.mmstor;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.__;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.storage.Storage;
import org.mmadt.testing.ObjArgs;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mmadt.testing.ObjArgs.args;
import static org.mmadt.testing.ObjArgs.ints;
import static org.mmadt.testing.ObjArgs.recs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StorTest {

    private final static Rec<Str, Obj> marko = recs(Map.of("name", "marko", "age", 29));
    private final static Rec<Str, Obj> kuppitz = recs(Map.of("name", "kuppitz", "age", 21));

    private final static ObjArgs[] STORAGE = new ObjArgs[]{
            args(ints(0), TInt.of(1).model(TRec.sym("mmstor")).put(TStr.of("users"), TRec.of(TRec.of(Map.of("name", "marko", "age", 29)), TRec.of(Map.of("name", "kuppitz", "age", 21)))).map(0)),
            args(marko, TRec.some().q(0, 2).access(TObj.single().model(TRec.sym("mmstor")).get("users").is(__.get("name").mult(__.eq("marko"))).access())),
            args(kuppitz, TInt.of(1).model(TRec.sym("mmstor")).get("users").is(__.get("name").mult(__.eq("kuppitz")))),
    };

    @TestFactory
    Stream<DynamicTest> testStorage() {
        return Stream.of(STORAGE).map(query -> query.execute(query.input));
    }

    @Test
    void testServiceProvider() {
        final ServiceLoader<Storage> serviceLoader = ServiceLoader.load(Storage.class);
        assertTrue(serviceLoader.stream().count() > 0);
        boolean found = false;
        for (Storage storage : serviceLoader) {
            if (storage.getClass().equals(Stor.class)) {
                assertEquals(Stor.MMSTOR, storage.name());
                found = true;
            }
        }
        assertTrue(found);
    }
}
