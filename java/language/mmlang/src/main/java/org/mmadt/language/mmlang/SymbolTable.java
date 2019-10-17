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

import org.mmadt.object.impl.TModel;
import org.mmadt.object.impl.TSym;
import org.mmadt.object.impl.composite.TInst;
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;
import org.mmadt.object.model.util.OperatorHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A symbol table for managing variables and named objects during parsing.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class SymbolTable {

    private final TModel model;
    private final Map<String, Obj> variables;
    private Inst bytecode = TInst.none();

    public SymbolTable(final TModel model) {
        this.model = model.clone();
        this.variables = new HashMap<>();
    }

    public SymbolTable(final String modelSymbol) {
        this.model = TModel.of(modelSymbol);
        this.variables = new HashMap<>();
    }

    private SymbolTable(final TModel model, final Map<String, Obj> variables, final Inst bytecode) {
        this.model = model.clone();
        this.variables = new HashMap<>(variables);
        this.bytecode = bytecode; // TODO: may need to clone()
    }

    public SymbolTable addSymbol(final String symbol) {
        if (this.model.has(symbol))
            return this;
        final TModel extendedModel = this.model.clone();
        extendedModel.define(symbol, TSym.of(symbol));
        return new SymbolTable(extendedModel, this.variables, this.bytecode);
    }

    public SymbolTable addVariable(final String symbol, final Obj object) {
        if (this.variables.containsKey(symbol)) {
            if (!this.variables.get(symbol).equals(object) && !(object.test(this.variables.get(symbol)) || this.variables.get(symbol).test(object)))
                throw new RuntimeException("The variable types is not the same as the type of its current use: " + this.variables.get(symbol) + "::" + object);
            return this;
        }
        final Map<String, Obj> temp = new HashMap<>(this.variables);
        temp.put(symbol, object);
        return new SymbolTable(this.model, temp, this.bytecode);
    }

    public SymbolTable addInst(final String operator, final Inst inst) {
        return new SymbolTable(this.model, this.variables,
                TInst.none().equals(this.bytecode) ?
                        TInst.of(List.of(inst)) :
                        OperatorHelper.operation(operator, this.bytecode, inst));
    }

    public boolean hasVariable(final String symbol) {
        return this.variables.containsKey(symbol);
    }

    public Obj getVariable(final String symbol) {
        return this.variables.get(symbol);
    }

    public boolean hasSymbol(final String symbol) {
        return this.model.has(symbol);
    }

    public <A extends Obj> TSym<A> getSymbol(final String symbol) {
        return this.model.sym(symbol);
    }

    public SymbolTable create(final SymbolTable symTable) {
        return new SymbolTable(symTable.model, symTable.variables, this.bytecode);
    }

    public SymbolTable merge(final SymbolTable symTable, final boolean dropVariables) {
        return new SymbolTable(symTable.model, dropVariables ? this.variables : symTable.variables, this.bytecode);
    }

    public String toString() {
        return this.model.toString();
    }

    public Inst bytecode() {
        return this.bytecode;
    }

    public TModel model() {
        return this.model;
    }

}
