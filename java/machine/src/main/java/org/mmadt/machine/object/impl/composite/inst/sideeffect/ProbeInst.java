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
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.IteratorUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ProbeInst<S extends Obj> extends TInst<S, S> implements SideEffectInstruction<S> {

    private ProbeInst(final Object start) {
        super(PList.of(Tokens.PROBE, start));
    }

    @Override
    public void accept(final S obj) {
        final String probe = new Table(this.argument(0).mapArg(obj).access(obj.access())).toString();
        System.out.println(probe);
    }

    public static <S extends Obj> ProbeInst<S> create(final Object start) {
        return new ProbeInst<>(start);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    private class Table {
        private final String header;
        private int length;
        private List<String> function = new ArrayList<>();
        private List<String> stream = new ArrayList<>();
        private List<String> state = new ArrayList<>();

        Table(final Obj root) {
            this.header = root.access(null) + " " + Tokens.MAPSTO + " " + root.access();
            //this.function.add("instruction");
            //this.stream.add("stream");
            //this.state.add("state");
            this.build(0, root, root.access());
        }

        private void build(int indent, final Obj start, final Inst inst) {
            final List<Inst> insts = IteratorUtils.list(inst.iterable());
            this.length = insts.size();
            Obj obj = start.access(null);
            this.stream.add(obj.toString());
            this.state.add(obj.state().toString());
            for (int i = 0; i < this.length; i++)
                this.stream.add("");
            for (int i = 0; i < this.length; i++) {
                for (int j = 0; j < this.length; j++) {
                    final Inst next = insts.get(j);
                    this.function.add(next.toString());
                    if (i == j) {
                        this.stream.add((obj = FastProcessor.process(obj.mapTo(next)).next()).toString());
                        this.state.add(obj.state().toString());
                    } else
                        this.stream.add("");

                    /*for (final Obj arg : i.args()) {
                        if (arg.isReference()) // TODO: nested probes
                            build((1 + indent), arg.access());
                    }*/
                }
            }
        }

        private List<String> normalize(final List<String> column) {
            final int maxFunction = column.stream().map(String::length).max(Comparator.naturalOrder()).orElse(0);
            return column.stream().map(s -> s + Tokens.space(maxFunction - s.length())).collect(Collectors.toList());
        }

        @Override
        public String toString() {
            this.function = normalize(this.function);
            this.stream = normalize(this.stream);
            this.state = normalize(this.state);
            final StringBuilder builder = new StringBuilder(Tokens.NEWLINE)
                    .append(this.header)
                    .append(Tokens.repeater(2, Tokens.NEWLINE));

            builder.append(Tokens.repeater(this.function.toString().length() / 2, Tokens.DASH))
                    .append(Tokens.NEWLINE);
            // System.out.println(this.stream);
            int counter = 0;
            for (int i = 0; i < this.length + 1; i++) {
                builder.append(Tokens.LANGLE)
                        .append(this.stream.get(i * this.length))
                        .append(Tokens.RANGLE);
                for (int j = 0; j < this.length; j++) {
                    counter++;
                    builder.append(this.function.get(j))
                            .append(Tokens.LANGLE)
                            .append(this.stream.get(counter))
                            .append(Tokens.RANGLE);
                }


                builder.append(Tokens.space(10))
                        .append(this.state.get(i))
                        .append(Tokens.NEWLINE);
            }


            return builder.toString();
        }
    }
}