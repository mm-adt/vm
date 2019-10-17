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
import org.mmadt.object.impl.TModel;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mmadt.language.mmlang.model.TypingTest.verifyTyping;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class SocialModelTest {

    private static final TModel model = TModel.of(Compiler.asInst(TypingTest.class.getResourceAsStream("social.mm")));

    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            new TestArgs("[db][get,'persons']", "[db][get,'persons'][order,[get,'age']]"),
            new TestArgs("[db][get,'persons'][get,'address']"),
            new TestArgs("[db][get,'persons'][get,'address']", "[db][get,'persons'][get,'address'][order,[gt,[get,'state']]]"),
            new TestArgs("[db][get,'persons'][get,'address']", "[db][get,'persons'][get,'address'][order,[gt,[get,'state']]][order,[gt,[get,'city']]]"),
            new TestArgs("[db][get,'persons'][get,'spouse'][get,'address'][get,'street']", "[db][get,'persons'][get,'spouse'][get,'address'][get,'street']"),
            new TestArgs("[ref,singles <= [db][get,'persons'][is,[get,'spouse'][count][eq,0]]]", "[db][get,'persons'][is,[get,'spouse'][count][eq,0]][dedup,[get,'name']]"),
            new TestArgs("[ref,singles <= [db][get,'persons'][is,[get,'spouse'][count][eq,0]]][get,'address'][get,'street']", "[db][get,'persons'][is,[get,'spouse'][count][eq,0]][get,'address'][get,'street']"),
            new TestArgs("[ref,singles <= [db][get,'persons'][is,[get,'spouse'][count][eq,0]]][get,'address'][get,'street']", "[db][get,'persons'][is,[get,'spouse'][count][eq,0]][get,'address'][get,'street'][dedup]"),
            new TestArgs("[ref,person&['age':29]{,200} <= [db][get,'persons'][is,[get,'age'][eq,29]]\n" +
                    " -> [is,[get,'name'][eq,str~c]] => [ref,person&['age':29,'name':str~c]{?} <= [db][get,'persons'][is,[get,'name'][eq,str~c]][is,[get,'age'][eq,29]]]]", "[db][get,'persons'][is,[get,'age'][eq,29]]"),
            new TestArgs("[ref,person&['age':29,'name':'marko']{?} <= [db][get,'persons'][is,[get,'name'][eq,'marko']][is,[get,'age'][eq,29]]]", "[db][get,'persons'][is,[get,'age'][eq,29]][is,[get,'name'][eq,'marko']]"),
            new TestArgs(true, "[ref,person&['age':29,'name':'marko']{?} <= [db][get,'persons'][is,[get,'name'][eq,'marko']][is,[get,'age'][eq,29]]][get,'name']", "[db][get,'persons'][is,[get,'age'][eq,29]][is,[get,'name'][eq,'marko']][get,'name']"), // TODO: should yield marko{?}
            new TestArgs("[db][get,'persons'][ref,person&['address':address{0},'age':int <= [is,[and,[a,int],[gt,75]]]]{*} <= [id]]", "[db][get,'persons'][is,[get,'alive']]"),
            new TestArgs("[db][get,'persons'][ref,person&['address':address{0},'age':int <= [is,[and,[a,int],[gt,75]]]]{*} <= [id]][get,'address']", "[db][get,'persons'][is,[get,'alive']][get,'address']"),
    };

    @TestFactory
    Stream<DynamicTest> testParse() {
        return Stream.of(TEST_PARAMETERS)
                .map(tp -> DynamicTest.dynamicTest(tp.input, () -> {
                    assumeFalse(tp.ignore);
                    assertEquals(Compiler.asInst(model, tp.expected), verifyTyping(model.query(Compiler.asInst(tp.input))));
                }));
    }
}
