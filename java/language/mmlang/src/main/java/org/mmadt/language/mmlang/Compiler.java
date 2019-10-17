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
import org.mmadt.object.model.Obj;
import org.mmadt.object.model.composite.Inst;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * A collection of static methods for transforming {@link String} bytecode into {@link Inst}.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Compiler {

    private final static Parser PARSER = Parboiled.createParser(Parser.class);

    private Compiler() {
        // static helper class
    }

    public static Inst asInst(final String bytecode) {
        return ((SymbolTable) new ReportingParseRunner<>(PARSER.Source()).run(bytecode).valueStack.pop()).bytecode();
    }

    public static Inst asInst(final TModel model, final String bytecode) {
        return ((SymbolTable) new ReportingParseRunner<>(PARSER.Source(model)).run(bytecode).valueStack.pop()).bytecode();
    }

    public static String asString(final InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream)).lines().reduce("", (a, b) -> a + "\n" + b);
    }

    public static Inst asInst(final InputStream inputStream) {
        return Compiler.asInst(asString(inputStream));
    }

    static TModel validateTypes(final TModel model) {
        final Optional<TSym<Obj>> symbol = model.definitions().values().stream().filter(sym -> null == sym.getObject()).findAny();
        if (symbol.isPresent()) {
            throw new RuntimeException("The following symbol was never defined: " + symbol.get());
        }
        return model;
    }


}