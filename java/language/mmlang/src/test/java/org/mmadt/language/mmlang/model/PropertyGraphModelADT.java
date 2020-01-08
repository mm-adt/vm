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

package org.mmadt.language.mmlang.model;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.mmadt.language.mmlang.jsr223.mmLangScriptEngine;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.testing.LanguageArgs;

import javax.script.ScriptEngine;
import java.util.Map;
import java.util.stream.Stream;

import static org.mmadt.testing.LanguageArgs.args;
import static org.mmadt.testing.LanguageArgs.objs;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
class PropertyGraphModelADT {

    /*
[graph   -> [[is,[get,id][eq,int~x]] ->[eval,'vertex_id_index','id',x] |
 element -> [[drop,'label'|'id']     ->[error]                         |
			 [put,'label'|'id',obj]  ->[error]]                        |
 vertex  -> [[dedup,[get,'id']]      ->[id]                            |
    	     [order]                 ->[id]]                           |
 edge    -> [[dedup,[get,'id']]      ->[id]]]
     */

    private static String pg = "[" +
            "        ['id':int,'label':str,str:obj{*}]~element; " +
            "element~['inE':edge~rec{*},'outE':edge~rec{*}]~vertex;" +
            "element~['outV':vertex~rec,'inV':vertex~rec]~edge;" +
            "vertex~rec{*}~graph]";

    private final static LanguageArgs[] PROPERTY_GRAPH = new LanguageArgs[]{
            args(objs(TRec.of(Map.of("id", 1, "label", "person")).symbol("element")), "element <=[=" + pg + "][map,['id':1,'label':'person']][as,element~rec][explain]"),
            args(objs(TRec.of(Map.of("id", 1, "label", "person")).symbol("vertex")), "vertex <=[=" + pg + "][map,['id':1,'label':'person']][as,vertex~rec]"),

    };

    @TestFactory
    Stream<DynamicTest> testBindings() {
        final ScriptEngine engine = new mmLangScriptEngine();
        return Stream.of(PROPERTY_GRAPH).map(query -> query.execute(engine));
    }
}
