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

package org.mmadt.machine.object.impl.composite.inst.sideeffect;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.inst.SideEffectInstruction;
import org.mmadt.machine.object.model.type.PList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ExplainInst<S extends Obj> extends TInst implements SideEffectInstruction<S> {

    private ExplainInst(final Object obj) {
        super(PList.of(Tokens.EXPLAIN, obj));
    }

    @Override
    public void accept(final S obj) {
        final Table table = new Table(this.args().isEmpty() ? obj : this.args().get(0));
        // TODO: provide as a side-effect
        System.out.println(table.toString());
    }

    public static <S extends Obj> ExplainInst<S> create(final Object arg) {
        return new ExplainInst<>(arg);
    }

    public static <S extends Obj> ExplainInst<S> create() {
        return new ExplainInst<>(null);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private class Table {
        private final String header;
        private List<String> function = new ArrayList<>();
        private List<String> domain = new ArrayList<>();
        private List<String> range = new ArrayList<>();

        Table(final Obj root) {
            this.header = root.toString();
            this.function.add("instruction");
            this.domain.add("domain");
            this.range.add("range");
            this.build(0, root.access());
        }

        private List<String> normalize(final List<String> column) {
            final int maxFunction = column.stream().map(String::length).max(Comparator.naturalOrder()).orElse(0);
            return column.stream().map(s -> s + Tokens.space(maxFunction - s.length())).collect(Collectors.toList());
        }

        private void build(int indent, final Inst inst) {
            for (final Inst i : inst.iterable()) {
                final String space = Tokens.space(indent);
                this.function.add(space + i.toString());
                this.domain.add(space + i.domain().access(null).toString());
                this.range.add(space + i.range().access(null).toString());
                for (final Obj x : i.args()) {
                    if (!x.isInstance())
                        build((1 + indent) * 2, x.access());
                }
            }
        }

        @Override
        public String toString() {
            this.function = normalize(this.function);
            this.domain = normalize(this.domain);
            this.range = normalize(this.range);
            final StringBuilder builder = new StringBuilder(Tokens.NEWLINE)
                    .append(this.header)
                    .append(Tokens.repeater(2, Tokens.NEWLINE));
            for (int i = 0; i < this.function.size(); i++) {
                builder.append(this.function.get(i))
                        .append(Tokens.space(2))
                        .append(this.domain.get(i))
                        .append(0 == i ? Tokens.space(6) : "  ->  ")
                        .append(this.range.get(i))
                        .append(Tokens.NEWLINE);
                if (0 == i)
                    builder.append(Tokens.repeater((builder.length() - builder.lastIndexOf("\n", builder.length() - 2)) - 3, Tokens.DASH))
                            .append(Tokens.NEWLINE);
            }
            return builder.toString();
        }
    }

}