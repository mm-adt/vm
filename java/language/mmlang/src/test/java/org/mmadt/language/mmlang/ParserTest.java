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
import org.mmadt.machine.object.impl.composite.TLst;
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
import static org.mmadt.language.mmlang.util.ParserArgs.bools;
import static org.mmadt.language.mmlang.util.ParserArgs.ints;
import static org.mmadt.language.mmlang.util.ParserArgs.objs;
import static org.mmadt.language.mmlang.util.ParserArgs.strs;
import static org.mmadt.machine.object.impl.___.gt;
import static org.mmadt.machine.object.impl.___.is;
import static org.mmadt.machine.object.impl.___.minus;
import static org.mmadt.machine.object.impl.___.plus;
import static org.mmadt.machine.object.model.composite.Q.Tag.plus;
import static org.mmadt.machine.object.model.composite.Q.Tag.qmark;
import static org.mmadt.machine.object.model.composite.Q.Tag.star;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ParserTest {

    private final static ParserArgs[] PARSING = new ParserArgs[]{
            /////////////////// MAP TO => ///////////////////
            args(ints(11), "11"),
            args(ints(0), "11 => [zero]"),
            args(objs(), "11 => [zero] => 11"),
            args(ints(11), "11 => [zero] => [plus,11]"),
            args(ints(11), "11 => int"),
            args(objs(ints(11).label("a")), "11 => int~a"),
            args(ints(11), "11 => [plus,[zero]]"),
            args(ints(11), "11 + 0"),
            args(ints(30), "11 + 4 * 2"),
            args(ints(30), "11 => [plus,4] => [mult,2]"),
            args(ints(30), "11 => [plus,4][mult,2] => int => [id]"),
            args(ints(30), "11 => ([plus,4] * [mult,2]) => int => [id]"),   // TODO: should we have binary operator precedence with => and <= being lowest?
            args(plus(11), "[plus,11]"),
            args(objs(1, 2, 3), "1 => ([id] + [plus,1] + [plus,2])"),
            args(objs(-1, -2, -3), "1 => (([id] + [plus,1] + [plus,2]) * [neg]) => [plus,[zero]] => int"),
            args(objs(ints(1).label("a"), TInt.of(2).label("a"), TInt.of(3).label("a")), "1 => ([id] + [plus,1] + [plus,2]) => int~a"),
            args(objs(ints(10).label("a")), "10 => (int~a | str~b | real~c)"),
            // ParserArgs.of(objs(TInt.of(20).label("a")), "10 => (int~a | str~b | real~c) => [plus,[id]]"),
            args(objs(strs("marko rodriguez").label("b")), "'marko' => (int~a | str~b | real~c) => [is,[a,str]][plus,' '][plus,'rodriguez']"),
            args(ints(40), "40 => int[is[gt,20]]"),
            args(objs(), "40 => [mult,2] => int[is[gt,100]]"),
            args(ints(is(gt(100))), "int => int[is[gt,100]]"),

            //////////////////////////////////////////////////////////
            // instance <=> instance | type | reference
            args(ints(1), "1 => 1"),
            args(ints(1), "1 <= 1"),
            args(ints(1).<Int>label("a"), "1 => 1~a"),
            args(ints(1).<Int>label("a"), "1 <= 1~a"),
            args(objs(), "1 => 2"),
            args(objs(), "1 <= 2"),
            args(ints(1), "1 => int"),
            args(ints(1).<Int>label("a"), "1 => int~a"),
            args(objs(), "1 => str"),
            args(objs(), "1 => str~a"),
            args(objs(), "1 <= str"),
            args(objs(), "1 <= str~a"),
            args(objs(ints(1).access(plus(2))), "1 <= [plus,2]"),
            args(ints(3), "1 => int => [plus,2]"),
            args(ints(3), "1 => (int => [plus,2])"),
            args(ints(3), "1 => int => [plus,2]"),
            args(ints(3), "(1 => int) => [plus,2]"),
            args(ints(3), "1 => (int <= [plus,2])"),
            args(ints(1).<Int>access(plus(2)), "1 <= (int <= [plus,2])"),
            args(ints(1), "1 <= [id]"),

            // type <=> instance | type | reference
            args(ints(1), "int => 1"),
            args(ints(1), "int => (1 => [id])"),
            args(ints(1), "int => (1 <= [id])"),
            args(ints(), "int => int"),
            args(ints().<Int>label("a"), "int => int~a"),
            args(ints().plus(2), "int => (int <= [plus,2])"),
            args(ints().plus(2), "int => (int => [plus,2])"),
            args(ints().<Int>label("a").plus(2), "int => (int~a => [plus,2])"),
            // args(ints().<Int>label("a").plus(2), "(int => int~a) <= [plus,2]"),
            args(ints().<Int>label("a").plus(2), "int => (int~a <= [plus,2])"),
            // args(ints().<Int>label("a").plus(2), "int => int~a <= [plus,2]"),
            args(ints().<Int>label("a").plus(2), "int <= int~a <= [plus,2]"),   // TODO: the meaning of this needs to be thought through
            args(ints().<Int>label("a").plus(2), "int <= (int~a <= [plus,2])"), // TODO: the meaning of this needs to be thought through
            // args(ints().<Int>label("a").plus(2), "int => (int => [plus,2]~a)"), // TODO: step labels (like quantifiers) transfer from inst to obj
            args(objs(), "str => 1"),
            args(strs("a"), "str => 'a'"),
            args(objs(), "str => int"),
            args((Obj) bools().access(gt(10)), "bool <= [gt,10]"),


            // references <=> instances | type | reference
            args(ints().plus(2).mult(3), "(int <= [plus,2]) => [mult,3]"),

            //args(ints(1), "(int <= [plus,0]) => 1"),  // TODO: this requires a new operator ^
            //args(ints(0), "(int <= [plus,1]) => 1"),
            //args(ints(-1), "(int <= [plus,2]) => 1"),


            /////////////////// QUANTIFIER TESTING ///////////////////
            args(ints().<Int>q(1), "int"),
            args(objs(ints().q(star)), "int{*}"),
            args(objs(ints().q(qmark)), "int{?}"),
            args(objs(ints().q(plus)), "int{+}"),
            args(objs(), "int{0}"),
            args(objs(TInt.of().q(1, 2)), "int{1,2}"),
            args(objs(TInt.of().q(2, TInt.of().max())), "int{2,}"),
            args(objs(TInt.of().q(TInt.of().min(), 2)), "int{,2}"),
            args(objs(PlusInst.create(11).q(2)), "[plus,11]{2}"),
            args(objs(plus(11).<Inst>q(2).mult(minus(TInt.of()).q(3, 4))), "[plus,11]{2}[minus,int]{3,4}"), // TODO: type <Int>q()
            // ParserArgs.of(objs(TInt.of().q(2).mapTo(plus(5).q(3))), "int{2} => [plus,5]{3}"),

            /////////////////// MAP FROM <= ///////////////////
            args(objs(11), "9 => (11 <= [plus,2])"),
            args(objs(ints(11).access(plus(2))), "11 <= [plus,2]"),                               // TODO: what is the meaning of this? right now, its 11 (the access doesn't matter)
            args(strs().plus("a"), "str <= [plus,'a']"),
            args(strs().plus("a").plus("bc"), "str <= [plus,'a'] => [plus,'bc']"),

            /////////////////////////////////////////////////////
            /////////////////////////////////////////////////////
            /////////////////////////////////////////////////////
            /////////////////////////////////////////////////////

            args(ints().plus(3), "(int -> (x <= 3)) => [plus,x]"),
            args(ints().plus(3).plus(10), "int -> (x <= 3) -> (y <= 10) => [plus,x][plus,y]"),
            args(ints().mult(10).mult(10), "(int -> (z <= 10) -> ([plus,int] <= [mult,z])) => [plus,2][plus,3]"),
            args(TLst.of("x", "y", "a"), "[;] -> (x <= ['x';'y']) -> (y <= ['a']) => [plus,x][plus,y]"),

            args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,['name':'a','age':1]][as,person][get,'age']"),
            args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,[map,[map,['name':'a','age':1]]][as,person]][get,'age']"),
            args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,['name':'a','age':1]][as,person][get,'age'][is,[eq,x]]"),
            args(objs(), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,[map,[map,['name':'a','age':1]]][as,person]][get,'age'][is,[gt,x]]"),
            //args(ints(1).<Int>label("x"), "(['name':'marko','age':29] -> (person <= ['name':str,'age':int~x])) => [as,person][get,'age'][map,person.['name':'a','age':1]][get,'age']"),
    };


    @TestFactory
    Stream<DynamicTest> testParsing() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(PARSING).map(query -> DynamicTest.dynamicTest(query.input, () -> assertEquals(query.expected, IteratorUtils.list((Iterator<Obj>) engine.eval(query.input)))));
    }

}
