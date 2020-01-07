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
import org.mmadt.language.mmlang.jsr223.mmLangScriptEngine;
import org.mmadt.testing.LanguageArgs;

import javax.script.ScriptEngine;
import java.util.stream.Stream;

import static org.mmadt.testing.LanguageArgs.args;
import static org.mmadt.testing.LanguageArgs.ints;
import static org.mmadt.testing.LanguageArgs.objs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class GraphLangTest {


    private final static LanguageArgs[] BINDINGS = new LanguageArgs[]{
            args(objs(), "obj{0} <=[=mmstor][put,'users',rec{6}<=[start," +
                    "['id':1,'name':'marko','age':29,'outE':rec{3}<=[start,['id':7,'outV':1,'inV':2,'label':'knows'],['id':8,'outV':1,'inV':4,'label':'knows'],['id':9,'outV':1,'inV':3,'label':'created']]]," +
                    "['id':2,'name':'vadas','age':27]," +
                    "['id':3,'name':'lop','lang':'java']," +
                    "['id':4,'name':'josh','age':32,'outE':rec{2}<=[start,['id':11,'outV':4,'inV':3,'label':'created'],['id':10,'outV':4,'inV':5,'label':'created']]]," +
                    "['id':5,'name':'ripple','lang':'java']," +
                    "['id':6,'name':'peter','age':35,'outE':['id':12,'outV':6,'inV':3,'label':'created']]]][is,false]"), // just want the side-effect of put() -- TODO: use [sideeffect,[put]]
            args(ints(1), "int <=[=mmstor][get,'users'][is,[get,'name'][eq,'marko']][get,'id']"),
            args(ints(1), "int <=[=mmstor][get,'users'][is,[get,'id'][eq,1]][get,'id']"),
            args(objs(4, 6), "int{0,4} <=[=mmstor][get,'users'][is,[get,'age',int][gt,30]][get,'id']"),
            args(objs(1, 4, 6), "int{0,4} <=[=mmstor][get,'users'][is,[get,'outE'][count][gt,0]][get,'id']"),
            args(objs(2,4), "int{0,4} <=[=mmstor][get,'users'][is,[get,'name'][eq,'marko']][get,'outE'][is,[get,'label'][eq,'knows']][get,'inV'][as,x][map,mmstor][get,'users'][is,[get,'id'][eq,x]][get,'id'][explain]"),
    };


    @TestFactory
    Stream<DynamicTest> testBindings() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(BINDINGS).map(query -> query.execute(engine));
    }
}
