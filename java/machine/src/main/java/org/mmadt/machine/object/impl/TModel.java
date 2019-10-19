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

package org.mmadt.machine.object.impl;

import org.mmadt.language.compiler.Rewriting;
import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class TModel implements Model {

    private String symbol;
    private Map<String, TSym<Obj>> definitions = new HashMap<>();

    private TModel(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String symbol() {
        return this.symbol;
    }

    @Override
    public <A extends Obj> TSym<A> sym(final String symbol) {
        if (this.definitions.containsKey(symbol))
            return (TSym) this.definitions.get(symbol);
        else {
            final TSym<A> obj = TSym.of(symbol);
            this.definitions.put(symbol, (TSym) obj);
            return obj;
        }
    }

    @Override
    public void define(final String symbol, final Obj object) {
        if (this.definitions.containsKey(symbol))
            this.definitions.get(symbol).setObject(object);
        else
            this.definitions.put(symbol, object instanceof TSym ? (TSym) object : TSym.of(symbol, object));
    }

    @Override
    public boolean has(final String symbol) {
        return this.definitions.containsKey(symbol);
    }

    @Override
    public Inst query(final Inst bytecode) {
        return Rewriting.rewrite(this, bytecode);
    }

    @Override
    public TModel model(final Inst bytecode) {
        bytecode.iterable().forEach(inst -> {
            if (inst.opcode().get().equals(Tokens.DEFINE))
                this.sym(inst.get(TInt.oneInt()).get()).setObject(inst.get(TInt.twoInt()));
            else if (inst.opcode().get().equals(Tokens.MODEL)) {
                this.symbol = inst.get(TInt.oneInt()).get();
                this.model((Inst) inst.get(TInt.twoInt()));
            }
        });
        this.populate();
        return this;
    }

    private void populate() {
        for (final String define : this.definitions.keySet()) {
            final TSym sym = this.definitions.get(define);
            String symbol = define;
            Obj runningType = null;
            while (this.definitions.containsKey(symbol)) {
                final TObj currentType = this.get(symbol);
                runningType = null == runningType ? currentType : runningType.and(currentType);
                if (symbol.equals(currentType.symbol()))
                    break;
                symbol = currentType.symbol();
            }
            assert null != runningType;
            if (this.definitions.containsKey(Tokens.OBJ))
                runningType = runningType.and(this.get(Tokens.OBJ));
            sym.setObject(runningType.symbol(define));
        }
    }

    @Override
    public TModel clone() {
        try {
            final TModel model = (TModel) super.clone();
            model.definitions = new HashMap<>(this.definitions);
            return model;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public <A extends Obj> A get(final String symbol) {
        return (A) this.definitions.get(symbol).getObject();
    }


    public static TModel of(final String symbol) {
        return new TModel(symbol);
    }

    public static TModel of(final String symbol, final Map<String, TSym<Obj>> definitions) {
        final TModel model = new TModel(symbol);
        model.definitions.putAll(definitions);
        return model;
    }

    public static TModel of(final Inst modelBytecode) {
        return new TModel("ex").model(modelBytecode);
    }

    public Map<String, TSym<Obj>> definitions() {
        return this.definitions;
    }

    @Override
    public String toString() {
        return this.definitions.toString();
    }

    //@Override
    public void load(final TModel model) {
        for (final Map.Entry<String, TSym<Obj>> entry : model.definitions.entrySet()) {
            if (!this.definitions.containsKey(entry.getKey()))
                this.definitions.put(entry.getKey(), entry.getValue());
            else if (null == this.definitions.get(entry.getKey()).getObject())
                this.definitions.get(entry.getKey()).setObject(TSym.fetch(entry.getValue()));
        }
        this.populate();
    }
}
