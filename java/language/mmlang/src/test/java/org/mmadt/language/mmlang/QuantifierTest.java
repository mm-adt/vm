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

package org.mmadt.language.mmlang;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.jsr223.mmLangScriptEngine;
import org.mmadt.language.mmlang.util.ParserArgs;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.inst.map.PlusInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Int;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.util.IteratorUtils;

import javax.script.ScriptEngine;
import java.util.Iterator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mmadt.language.mmlang.util.ParserArgs.args;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;
import static org.mmadt.machine.object.impl.__.minus;
import static org.mmadt.machine.object.impl.__.plus;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.plus;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.qmark;
import static org.mmadt.machine.object.model.util.QuantifierHelper.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class QuantifierTest {

    private final static ParserArgs[] PARSING = new ParserArgs[]{
            args(ints().<Int>q(1),
                    "int"),
            args(objs(ints().q(star)),
                    "int{*}"),
            args(objs(ints().q(qmark)),
                    "int{?}"),
            args(objs(ints().q(plus)),
                    "int{+}"),
            args(objs(),
                    "int{0}"),
            args(objs(TInt.of().q(1, 2)),
                    "int{1,2}"),
            args(objs(TInt.of().q(2, TInt.of().max())),
                    "int{2,}"),
            args(objs(TInt.of().q(TInt.of().min(), 2)),
                    "int{,2}"),
            args(objs(PlusInst.create(11).q(2)),
                    "[plus,11]{2}"),
            args(objs(plus(11).<Inst>q(2).mult(minus(TInt.of()).q(3, 4))),
                    "[plus,11]{2}[minus,int]{3,4}"), // TODO: type <Int>q()
            // ParserArgs.of(objs(TInt.of().q(2).mapTo(plus(5).q(3))), "int{2} => [plus,5]{3}"),
    };

    @TestFactory
    Stream<DynamicTest> testQuantifiers() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(PARSING).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }

}
