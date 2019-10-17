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

package org.mmadt.language.mmlang;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.util.TestArgs;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class ParserTest {

    private final static TestArgs[] TEST_PARAMETERS = new TestArgs[]{
            new TestArgs("[db]"),
            //new TestParameters("[start,1,2.34,'abc',\"< \\\\\\\"\\\\/\\\" >\"]"),
            new TestArgs("[start,42][start,['name':'marko','age':[get,0]]]"),
            new TestArgs("[flatmap,[unfold]]"),
            new TestArgs("[unfold]"),
            new TestArgs("[start,[:]]"),
            new TestArgs("[start,[;]]"),
            new TestArgs("[map,[unfold][count]]"),
            new TestArgs("[map,[count]]"),
            new TestArgs("[get,'name']"),
            new TestArgs("[get,0]"),
            new TestArgs("[get,'creator']"),
            new TestArgs("[loops]"),
            new TestArgs("[loops,int~i]"),
            new TestArgs("[path]"),
            new TestArgs("[path,['b']]"),
            new TestArgs("[path,['b';'c']]"),
            new TestArgs("[path,['b';'c'],[[get,'name']]]"),
            new TestArgs("[path,[[get,'outE'][count]]]"),
            new TestArgs("[dedup]"),
            new TestArgs("[dedup,[get,'name']]"),
            new TestArgs("[dedup,[get,'name'],[get,'age']]"),
            new TestArgs("[range,0,3]"),
            new TestArgs("[range,0,int~x]"),
            new TestArgs("[range,0,[start,1,2,3][count]]"),
            new TestArgs("[define,person,['name':str] <= [db][get,'people']]"),
            new TestArgs("[define,person,['name':str]{*} <= [db]]"),
            new TestArgs("[define,person,['name':str]{*}\n -> [dedup] => ]"),
            new TestArgs("[define,person,['name':str{2,}]]"),
            new TestArgs("[define,person,['name':str{2,3}]]"),
            new TestArgs("[define,person,['name':str{,3}]]"),
            new TestArgs("[define,person,['name':str{3}]]"),
            new TestArgs("[define,person,['name':str,'age':int]]"),
            new TestArgs("[define,person,['name':str,'age':int&gt(32)]]"),
            new TestArgs("[define,person,['name':str,'age':int&gt(32)&lt(40)]]"),
            new TestArgs("[define,person,['name':str,'age':int&lt(32)|int&gt(40)]]"),
            new TestArgs("[define,person,['age':int&gt(32)]]"),
            new TestArgs("[define,person,['name':str,'age':gt(32)]]"),
            new TestArgs("[define,person,[str;int&gt(32)]]"),
            new TestArgs("[define,person,['name':str,'age':gt(32)]]"),
            new TestArgs("[define,person,['name':str,'age':gt(32)&lt(40)]]"),
            new TestArgs("[define,person,['name':str,'age':gt(32)|lt(40)]]"),
            new TestArgs("[define,person,['name':str,'age':gt(32)&lt(40)|gt(50)]]"),
            new TestArgs("[define,person,['name':str,'age':gt(32)&lt(40)|gt(50)]]"),
            new TestArgs("[define,person,['name':str,'age':int]{*}]"),
            new TestArgs("[define,person,['name':str,'age':int{+}]{*}]"),
            new TestArgs("[define,person,['name':str,'age':(int&gt(32)){+}]{*}]"),
            new TestArgs("[define,person,['loc':obj~z,'name':str{+}~x,'age':int~y]]"),
            new TestArgs("[define,person,['name':str~x,'age':int] <= [db][is,[get,'name'][eq,str~x]]]"),
            new TestArgs("[define,persons,['name':str,'age':int]{*} <= [db][get,'persons']\n" +
                    " -> [is,[get,'name'][eq,str~x]] => [ref,['name':str~x,'age':int]{?}]\n" +
                    " -> [is,[get,'age'][gt,int~x]] => [ref,['name':str,'age':int&gt(int~x)]{?}]]"),
            new TestArgs("[define,person,['name':str~x,'age':int] <= [db][is,[get,'name'][eq,str~x]]]"),
            new TestArgs("[define,row,['col':bool|int|str]]"),
            new TestArgs("[define,row,[str:bool|int|str]]"),
            //new TestParameters(true, "[define,row,[@str:@bool|@int&gt(0)|@str]]", "[define,row,[@str:@bool|@int&(gt(0)|@str)]]"),
            //new TestParameters("[define,row,[str:bool|int&(gt(0)|str)]]", "[define,row,[str:bool|int&(gt(0)|str)]]"),
            new TestArgs("[define,strList,[str{*}]]" +
                    "[define,row,[str:str|int|bool|strList]]" +
                    "[define,person,row&['name':str,'age':int&gt(0),'tags':strList]]" +
                    "[define,people,person{*}]"),
            new TestArgs(true, "[[define,row,[(@str:@bool|@int*|@str){+}]]]"), // todo: field quantifiers
            new TestArgs("[define,db,['persons':obj~people]]"),
            new TestArgs("[ref,['name':str~x,'age':int]{?} <= [is,[get,'name'][eq,str~x]]]"),
            new TestArgs("[ref,int{*}]"),
            new TestArgs("[ref,bool{*} <= [db][get,'V']]"),
            new TestArgs("[ref,real{*} <= [db][get,'V']\n -> [dedup] => ]"),
            new TestArgs("[define,project,['outE':['edges';str]{*},'name':str,'label':str]]" +
                    "[define,edge,['inV':person,'label':str]]" +
                    "[define,person,['name':str,'age':int]]" +
                    "[define,db,['V':project{*}\n" +
                    " -> [dedup] => \n" +
                    " -> [is,[get,'label'][eq,str~x]] => [ref,project{*} <= [db][get,'V']\n" +
                    " -> [dedup] => ]]]"),
            new TestArgs("[get,str <= [start,'name']]", "[get,(str <= [start,'name'])]"),
            new TestArgs("[get,str <= [start,'name']]"),
            /*new TestParameters(true, "[[define,keyvalue,[@obj~k,@obj~v] <= ([key:@obj~k,val:@obj~v] <= [[db][get,kv][is,[[get,key][eq,@obj~k]]]])]]"), // TODO: access of type TType
            new TestParameters("[define,ff,[is,@bool]|[filter,@obj?]]"),
            new TestParameters("[define,ff,[is,@bool]]"),
            new TestParameters("[define,db,@int*\n -> [filter,@ff*~a] => ^[@obj~a]]"),
            new TestParameters("[define,db,@int*\n -> [filter,@ff*~a] => ^[@ff~a]]"),
            new TestParameters("[define,db,@int*\n -> [filter,@ff*~a] => ^[@ff{3}]]"),*/
            //new TestParameters("[model,rdb=>mm]"),
            new TestArgs("[model,rdb=>mm,[define,foo,int]]"),
            new TestArgs("[model,rdb=>mm,[define,foo,int][define,bar,str]]"),
            new TestArgs("[ref,['name':'marko','age':int]{?} <= [db][get,'persons'][is,[get,'name'][eq,'marko']][get,'age'][gt,29]]"),
    };


    @TestFactory
    Stream<DynamicTest> testParse() {
        return Stream.of(TEST_PARAMETERS)
                .map(tp -> DynamicTest.dynamicTest(tp.input, () -> {
                    assumeFalse(tp.ignore);
                    assertEquals(tp.expected, Compiler.asInst(tp.input).toString());
                }));
    }

}
