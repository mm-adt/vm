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

package org.mmadt.storage.mmstor;

import org.junit.jupiter.api.Test;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.storage.Storage;
import org.mmadt.util.IteratorUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.__.start;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class StorTest {

    @Test
    void testRoot() {
        final Storage<Lst<Str>> storage = new Stor<>(TLst.of());
        storage.root().put(TStr.of("a"));
        storage.root().put(TStr.of("b"));
        storage.root().put(TStr.of("c"));
        storage.root().put(TStr.of("d"));
        assertEquals(TLst.of("a", "b", "c", "d"), storage.root());
        assertEquals(List.of(TLst.of("b", "d", "f")), IteratorUtils.list(FastProcessor.process(TLst.some().mapFrom(start(storage.root()).plus(TLst.of("e", "f")).minus(TLst.of("a", "c", "e")).bytecode()))));
    }

    @Test
    void testModel() {
        Storage storage = new Stor<>(TLst.of());
        assertEquals(TLst.some(), storage.model().get(Tokens.DB));
        //
        final Rec<Str, Obj> person = TRec.of("name", TStr.some(), "age", TInt.some());
        final Rec<Str, Obj> people = person.q(star);
        final Rec<Str, Obj> instances = people.accessFrom(start()).type(people);
        storage = new Stor<>(instances);
        assertEquals(people, storage.model().get(Tokens.DB));
        assertEquals(instances, storage.root());
        assertEquals(people, storage.root().type());
    }
}
