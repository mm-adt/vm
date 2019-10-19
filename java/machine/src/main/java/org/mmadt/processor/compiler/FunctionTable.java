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

package org.mmadt.processor.compiler;

import org.mmadt.object.model.Model;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.processor.function.QFunction;
import org.mmadt.processor.function.branch.BranchBranch;
import org.mmadt.processor.function.filter.FilterFilter;
import org.mmadt.processor.function.filter.IdentityFilter;
import org.mmadt.processor.function.filter.IsFilter;
import org.mmadt.processor.function.flatmap.UnfoldFlatMap;
import org.mmadt.processor.function.initial.StartInitial;
import org.mmadt.processor.function.map.AMap;
import org.mmadt.processor.function.map.DropMap;
import org.mmadt.processor.function.map.EqMap;
import org.mmadt.processor.function.map.GetMap;
import org.mmadt.processor.function.map.GtMap;
import org.mmadt.processor.function.map.MapMap;
import org.mmadt.processor.function.map.MinusMap;
import org.mmadt.processor.function.map.MultMap;
import org.mmadt.processor.function.map.OneMap;
import org.mmadt.processor.function.map.PlusMap;
import org.mmadt.processor.function.map.PutMap;
import org.mmadt.processor.function.map.QMap;
import org.mmadt.processor.function.map.TypeMap;
import org.mmadt.processor.function.map.ZeroMap;
import org.mmadt.processor.function.reduce.CountReduce;
import org.mmadt.processor.function.reduce.GroupCountReduce;
import org.mmadt.processor.function.reduce.SumReduce;

import static org.mmadt.language.compiler.Tokens.A;
import static org.mmadt.language.compiler.Tokens.BRANCH;
import static org.mmadt.language.compiler.Tokens.COUNT;
import static org.mmadt.language.compiler.Tokens.DB;
import static org.mmadt.language.compiler.Tokens.DROP;
import static org.mmadt.language.compiler.Tokens.EQ;
import static org.mmadt.language.compiler.Tokens.FILTER;
import static org.mmadt.language.compiler.Tokens.GET;
import static org.mmadt.language.compiler.Tokens.GROUPCOUNT;
import static org.mmadt.language.compiler.Tokens.GT;
import static org.mmadt.language.compiler.Tokens.ID;
import static org.mmadt.language.compiler.Tokens.IS;
import static org.mmadt.language.compiler.Tokens.MAP;
import static org.mmadt.language.compiler.Tokens.MINUS;
import static org.mmadt.language.compiler.Tokens.MULT;
import static org.mmadt.language.compiler.Tokens.ONE;
import static org.mmadt.language.compiler.Tokens.PLUS;
import static org.mmadt.language.compiler.Tokens.PUT;
import static org.mmadt.language.compiler.Tokens.Q;
import static org.mmadt.language.compiler.Tokens.START;
import static org.mmadt.language.compiler.Tokens.SUM;
import static org.mmadt.language.compiler.Tokens.TYPE;
import static org.mmadt.language.compiler.Tokens.UNFOLD;
import static org.mmadt.language.compiler.Tokens.ZERO;

/**
 * A {@link FunctionTable} is a lookup-table from bytecode instruction to a Java function representation.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class FunctionTable {

    private FunctionTable() {
        // static helper class
    }

    public static QFunction function(final Model model, final Inst inst) {
        final String opcode = inst.opcode().get();
        switch (opcode) {
            case A:
                return AMap.compile(inst);
            case BRANCH:
                return BranchBranch.compile(inst);
            case COUNT:
                return CountReduce.compile(inst);
            case DB:
                return FunctionTable.function(model, model.get(DB).access().iterable().iterator().next()); // TODO...
            case DROP:
                return DropMap.compile(inst);
            case EQ:
                return EqMap.compile(inst);
            case FILTER:
                return FilterFilter.compile(inst);
            case GET:
                return GetMap.compile(inst);
            case GROUPCOUNT:
                return GroupCountReduce.compile(inst);
            case GT:
                return GtMap.compile(inst);
            case ID:
                return IdentityFilter.compile(inst);
            case IS:
                return IsFilter.compile(inst);
            case MAP:
                return MapMap.compile(inst);
            case MINUS:
                return MinusMap.compile(inst);
            case MULT:
                return MultMap.compile(inst);
            case ONE:
                return OneMap.compile(inst);
            case PLUS:
                return PlusMap.compile(inst);
            case PUT:
                return PutMap.compile(inst);
            case Q:
                return QMap.compile(inst);
            case START:
                return StartInitial.compile(inst);
            case SUM:
                return SumReduce.compile(inst);
            case TYPE:
                return TypeMap.compile(inst);
            case UNFOLD:
                return UnfoldFlatMap.compile(inst);
            case ZERO:
                return ZeroMap.compile(inst);
            default:
                throw new RuntimeException("Unknown instruction: " + inst);
        }
    }

}
