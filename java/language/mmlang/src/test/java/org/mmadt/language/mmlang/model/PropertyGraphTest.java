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

package org.mmadt.language.mmlang.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.Compiler;
import org.mmadt.language.mmlang.util.TestArgs;
import org.mmadt.machine.object.impl.TModel;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mmadt.language.mmlang.model.TypingTest.verifyTyping;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PropertyGraphTest {

    private static final TModel model = TModel.of(Compiler.asInst(TypingTest.class.getResourceAsStream("pg.mm")));

    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            // FilterRankStrategy
            new TestArgs("[db][dedup,[get,'label']][order,[lt,[get,'label']]]", "[db][order,[lt,[get,'label']]][dedup,[get,'label']]"),
            new TestArgs("[db][is,[get,'name'][eq,'marko']][dedup,[get,'label']]", "[db][dedup,[get,'label']][is,[get,'name'][eq,'marko']]"),
            // InlineFilterStrategy
            new TestArgs("[db][is,[get,'label'][eq,'person']]", "[db][filter,[is,[get,'label'][eq,'person']]]"),
            // CountStrategy
            new TestArgs("[db][get,'outE'][get,'inV'][get,'inE'][range,0,3][count][is,[gt,3]]", "[db][get,'outE'][get,'inV'][get,'inE'][count][is,[gt,3]]"),
            // PrimaryStructureOnly
            new TestArgs("[db][get,'outE']"),
            new TestArgs("[db][get,'inE'][get,'label']"),
            new TestArgs("[db][get,'outE'][get,'label']"),
            new TestArgs("[db][get,'outE'][get,'id']"),
            new TestArgs("[db][get,'outE'][get,'inV'][get,'id']"),
            new TestArgs("[db][get,'inE']"),
            new TestArgs("[db][get,'outE'][get,'inV']"),
            new TestArgs("[db][order,[gt,[get,'id']]]"),
            // NoOp Schema Deductions
            new TestArgs("[db]", "[db][dedup,[get,'id']]"),
            new TestArgs("[db][get,'outE']", "[db][get,'outE'][dedup,[get,'id']]"),
            new TestArgs("[db][get,'outE'][get,'inV']"),
            new TestArgs("[ref,int <= [db][get,'outE'][get,'inV'][count]]", "[db][get,'outE'][dedup,[get,'id']][get,'inV'][dedup,[get,'id']][count]"),
            new TestArgs("[db]", "[db][order,[gt,[get,'label']]]"),
            new TestArgs("[db][get,'outE']", "[db][id][get,'outE'][id][id][id]"),
            // Aggregate Counts
            new TestArgs("[ref,int <= [db][count]]"),
            new TestArgs("[ref,int <= [db][get,'outE'][count]]", "[db][get,'outE'][count]"),
            new TestArgs("[ref,int <= [db][get,'inE'][count]]", "[db][get,'inE'][count]"),
            new TestArgs("[ref,int <= [db][get,'outE'][get,'inV'][count]]", "[db][get,'outE'][get,'inV'][count]"),
            new TestArgs("[ref,int <= [db][get,'inE'][get,'outV'][count]]", "[db][get,'inE'][get,'outV'][count]"),
            new TestArgs("[ref,int <= [db][get,'outE'][get,'inV'][count]]", "[db][get,'outE'][dedup,[get,'id']][get,'inV'][dedup,[get,'id']][count]"),
            new TestArgs("[ref,vertex&['id':1]{?} <= [db][is,[get,'id'][eq,1]]][count]", "[db][is,[get,'id'][eq,1]][count]"),
            // Denormalizations
            new TestArgs("[db][get,'outE'][get,'inV'][get,'inE'][ref,int <= [get,'inV'][get,'id']]", "[db][get,'outE'][get,'inV'][get,'inE'][get,'inV'][get,'id']"),
            new TestArgs(true, "[db][get,'outE'][is,[get,'inV'][get,'id'][eq,1]]", "[ref,edge&['inV':vertex&['id':1]]{*} <= [get,'outE'][is,[get,'inV'][get,'id'][eq,1]]]"), // TODO: deref dead end reference graph path to search another
            new TestArgs(true, "[db][get,'outE'][get,'inV'][get,'outE'][is,[get,inV][eq,['id':1]]]", "[db][get,'outE'][get,'inV'][ref,edge{?} <= [get,'outE'][is,[get,'inV'][eq,['id':1]]]]"),
            // Element Equality
            new TestArgs("[db][get,'id'][eq,2]", "[db][eq,vertex&['id':2]]"),
            // Indices
            new TestArgs("[ref,vertex&['id':1]{?} <= [db][is,[get,'id'][eq,1]]]", "[db][is,[get,'id'][eq,1]]"),
    };

    // @TestFactory
    Stream<DynamicTest> testParse() {
        return Stream.of(TEST_PARAMETERS)
                .map(tp -> DynamicTest.dynamicTest(tp.input, () -> {
                    assumeFalse(tp.ignore);
                    assertTrue(model.has("edge") && model.has("vertex") && model.has("obj") &&
                            model.has("value") && model.has("key") && model.has("db") && model.has("element"));
                    if (null != tp.ex)
                        assertThrows(tp.ex, () -> verifyTyping(model.query(Compiler.asInst(model, tp.input))));
                    else
                        assertEquals(Compiler.asInst(model, tp.expected), verifyTyping(model.query(Compiler.asInst(model, tp.input))));
                }));
    }

}
