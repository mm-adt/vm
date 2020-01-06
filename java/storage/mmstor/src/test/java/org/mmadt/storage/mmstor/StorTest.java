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

import org.junit.jupiter.api.Test;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.storage.Storage;
import org.mmadt.util.IteratorUtils;

import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StorTest {

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

    @Test
    void testModelInstruction() {
        System.out.println(FastProcessor.process(TInt.of().model(TRec.sym("mmstor")).plus(TRec.of(Map.of("a", "b"))).explain()).next());
        System.out.println(FastProcessor.process(TInt.of().model(TRec.sym("mmstor")).put("a","b").put("c","d").map(TRec.sym("mmstor")).explain()).next());
        System.out.println(FastProcessor.process(TInt.of().model(TRec.sym("mmstor")).put("a","b").put("c","d").explain()).next());
    }

    @Test
    void testRoot() {
        final Storage<Lst<Str>> storage = new Stor<>(TLst.of());
        storage.root().put(TStr.of("a"));
        storage.root().put(TStr.of("b"));
        storage.root().put(TStr.of("c"));
        storage.root().put(TStr.of("d"));
        assertEquals(TLst.of("a", "b", "c", "d"), storage.root());
        assertEquals(List.of(TLst.of("b", "d", "f")), IteratorUtils.list(FastProcessor.process(storage.root().plus(TLst.of("e", "f")).minus(TLst.of("a", "c", "e")))));
    }
}
