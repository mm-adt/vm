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

package org.mmadt.language.compiler;

/**
 * The set of common tokens used by all mm-ADT languages.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Tokens {

    private Tokens() {
        // static helper class
    }

    public static final String COLON = ":";
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";
    public static final String PERIOD = ".";
    public static final String DASH = "-";
    public static final String TILDE = "~";
    public static final String AMPERSAND = "&";
    public static final String BAR = "|";
    public static final String ASTERIX = "*";
    public static final String CROSS = "+";
    public static final String EQUALS = "=";
    public static final String EMPTY = "";
    public static final String NEWLINE = "\n";
    public static final String QUESTION = "?";
    public static final String LBRACKET = "[";
    public static final String RBRACKET = "]";
    public static final String LCURL = "{";
    public static final String RCURL = "}";
    public static final String LANGLE = "<";
    public static final String RANGLE = ">";
    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String DQUOTE = "\"";
    public static final String SQUOTE = "'";
    public static final String SPACE = " ";
    public static final String MAPSFROM = "<=";
    public static final String MAPSTO = "=>";
    public static final String STEP = "->";
    public static final String TRUE = "true";
    public static final String FALSE = "false";


    public static final String OBJ = "obj";
    public static final String BOOL = "bool";
    public static final String INT = "int";
    public static final String REAL = "real";
    public static final String STR = "str";
    public static final String LIST = "list";
    public static final String REC = "rec";
    public static final String INST = "inst";
    public static final String Q = "q";

    public static final String A = "a";
    public static final String AND = "and";
    public static final String ID = "id";
    public static final String BRANCH = "branch";
    public static final String COALESCE = "coalesce";
    public static final String COUNT = "count";
    public static final String DB = "db";
    public static final String DEDUP = "dedup";
    public static final String DEFINE = "define";
    public static final String DIV = "div";
    public static final String DROP = "drop";
    public static final String EQ = "eq";
    public static final String ERROR = "error";
    public static final String EVAL = "eval";
    public static final String FILTER = "filter";
    public static final String FLATMAP = "flatMap";
    public static final String IS = "is";
    public static final String GET = "get";
    public static final String GT = "gt";
    public static final String GTE = "gte";
    public static final String GROUPCOUNT = "groupCount";
    public static final String INV = "inv";
    public static final String LT = "lt";
    public static final String LTE = "lte";
    public static final String MAP = "map";
    public static final String MINUS = "minus";
    public static final String MODEL = "model";
    public static final String MULT = "mult";
    public static final String NEG = "neg";
    public static final String NEQ = "neq";
    public static final String ONE = "one";
    public static final String OR = "or";
    public static final String ORDER = "order";
    public static final String PLUS = "plus";
    public static final String PUT = "put";
    public static final String RANGE = "range";
    public static final String REDUCE = "reduce";
    public static final String REF = "ref";
    public static final String START = "start";
    public static final String SUM = "sum";
    public static final String TYPE = "type";
    public static final String UNFOLD = "unfold";
    public static final String ZERO = "zero";
}
