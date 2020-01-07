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
import org.junit.jupiter.api.TestFactory;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.storage.compliance.util.TestArgs;

import java.util.Map;
import java.util.stream.Stream;

import static org.mmadt.machine.object.impl.__.eq;
import static org.mmadt.machine.object.impl.__.get;
import static org.mmadt.storage.compliance.util.TestArgs.args;
import static org.mmadt.storage.compliance.util.TestArgs.ints;
import static org.mmadt.storage.compliance.util.TestArgs.objs;
import static org.mmadt.storage.compliance.util.TestArgs.recs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GraphTest {

    private final static Rec<Str, Obj> markoKnowsVadas = recs(Map.of("id", 7, "outV", 1, "inV", 2, "label", "knows"));
    private final static Rec<Str, Obj> markoKnowsJosh = recs(Map.of("id", 8, "outV", 1, "inV", 4, "label", "knows"));
    private final static Rec<Str, Obj> markoCreatedLop = recs(Map.of("id", 9, "outV", 1, "inV", 3, "label", "created"));
    private final static Rec<Str, Obj> peterCreatedLop = recs(Map.of("id", 12, "outV", 6, "inV", 3, "label", "created"));
    private final static Rec<Str, Obj> joshCreatedLop = recs(Map.of("id", 11, "outV", 4, "inV", 3, "label", "created"));
    private final static Rec<Str, Obj> joshCreatedRipple = recs(Map.of("id", 10, "outV", 4, "inV", 5, "label", "created"));


    private final static Rec<Str, Obj> marko = recs(Map.of("id", 1, "name", "marko", "age", 29, "outE", recs(markoKnowsVadas, markoKnowsJosh, markoCreatedLop)));
    private final static Rec<Str, Obj> vadas = recs(Map.of("id", 2, "name", "vadas", "age", 27));
    private final static Rec<Str, Obj> lop = recs(Map.of("id", 3, "name", "lop", "lang", "software"));
    private final static Rec<Str, Obj> josh = recs(Map.of("id", 4, "name", "josh", "age", 32, "outE", recs(joshCreatedLop, joshCreatedRipple)));
    private final static Rec<Str, Obj> ripple = recs(Map.of("id", 5, "name", "ripple", "lang", "software"));
    private final static Rec<Str, Obj> peter = recs(Map.of("id", 6, "name", "peter", "age", 35, "outE", recs(peterCreatedLop)));

    private final static TestArgs[] TINKERPOP = new TestArgs[]{
            args(ints(0), TInt.of().access(TObj.single().model(TRec.sym("mmstor")).put(TStr.of("V"), TRec.of(marko, vadas, lop, josh, ripple, peter)).explain().map(0).access())),
            args(marko, TRec.some().access(TObj.single().model(TRec.sym("mmstor")).get("V").<Rec>is(get("id").mult(eq(1))).access())),
            args(marko, TRec.some().access(TObj.single().model(TRec.sym("mmstor")).get("V").<Rec>is(get("name").mult(eq("marko"))).access())),
            args(objs(markoKnowsVadas, markoKnowsJosh, markoCreatedLop), TRec.some().q(0, 2).access(TObj.single().model(TRec.sym("mmstor")).get("V").<Rec>is(get("id").mult(eq(1))).get("outE").access())),
            args(objs(2, 4, 3), TObj.single().model(TRec.sym("mmstor")).get("V").<Rec<Obj, Rec>>is(get("id").mult(eq(1))).get("outE").get("inV")),
            args(objs(2, 4), TObj.single().model(TRec.sym("mmstor")).get("V").<Rec<Obj, Rec>>is(TRec.some().get("id").eq(TInt.of(1))).get("outE").<Rec<Obj, Rec>>is(get("label").mult(eq("knows"))).get("inV")),
    };

    @TestFactory
    Stream<DynamicTest> testStorage() {
        return Stream.of(TINKERPOP).map(query -> query.execute(query.input));
    }
}
