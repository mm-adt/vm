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

package org.mmadt.language;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.util.TestArgs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mmadt.language.__.get;
import static org.mmadt.language.__.start;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class QueryTest {

    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            new TestArgs<>("[start][dedup]", start().dedup()),
            new TestArgs<>("[start][dedup,[get,'name']]", start().dedup(get("name"))),
            new TestArgs<>("[start][dedup,[get,'name'],[get,'age']]", start().dedup(get("name"), get("age"))),
            new TestArgs<>("[start][get,'name'][is,true][count][sum]", start().get("name").is(true).count().sum()),
            new TestArgs<>("[start][get,2][is,[get,3][gt,77.6]][count]", start().get(2).is(get(3).gt(77.6)).count()),
    };

    @TestFactory
    Stream<DynamicTest> testParse() {
        return Stream.of(TEST_PARAMETERS)
                .map(tp -> DynamicTest.dynamicTest(tp.input.toString(), () -> {
                    assumeFalse(tp.ignore);
                    assertEquals(tp.expected.toString(), tp.input.toString());
                }));
    }
}
