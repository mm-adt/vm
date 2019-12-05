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

package org.mmadt.machine.object.model.util;

import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;

import java.util.Map;

import static org.mmadt.language.compiler.Tokens.ASTERIX;
import static org.mmadt.language.compiler.Tokens.COLON;
import static org.mmadt.language.compiler.Tokens.COMMA;
import static org.mmadt.language.compiler.Tokens.CROSS;
import static org.mmadt.language.compiler.Tokens.EMPTY;
import static org.mmadt.language.compiler.Tokens.LBRACKET;
import static org.mmadt.language.compiler.Tokens.LCURL;
import static org.mmadt.language.compiler.Tokens.LPAREN;
import static org.mmadt.language.compiler.Tokens.MAPSFROM;
import static org.mmadt.language.compiler.Tokens.QUESTION;
import static org.mmadt.language.compiler.Tokens.RBRACKET;
import static org.mmadt.language.compiler.Tokens.RCURL;
import static org.mmadt.language.compiler.Tokens.RPAREN;
import static org.mmadt.language.compiler.Tokens.SEMICOLON;
import static org.mmadt.language.compiler.Tokens.SPACE;
import static org.mmadt.language.compiler.Tokens.TILDE;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StringFactory {

    private StringFactory() {
        // for static method use
    }

    private static void objMetadata(final Obj obj, final StringBuilder builder) {
        if (!obj.q().isOne())
            builder.append(obj.q());
        if (null != obj.label())
            builder.append(TILDE)
                    .append(obj.label());
        if (!obj.access().isOne()) {
            builder.append(SPACE)
                    .append(MAPSFROM)
                    .append(SPACE)
                    .append(obj.access());
        }
    }

    private static String nestedObj(final Obj obj) {
        final StringBuilder builder = new StringBuilder();
        if (obj.constant() || !obj.named())
            builder.append(obj);
        else {
            builder.append(obj.symbol());
            StringFactory.objMetadata(obj, builder);
        }
        return builder.toString();
    }

    public static String stream(final Stream<? extends Obj> stream) {
        final StringBuilder builder = new StringBuilder();
        for (Obj object : stream) {
            builder.append(nestedObj(object)).append(COMMA);
        }
        if (builder.length() > 0)
            builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String record(final Rec<? extends Obj, ? extends Obj> record) {
        final StringBuilder builder = new StringBuilder();
        if (record.isInstance() || record.get() != null)
            builder.append(record.<PMap>get());
        else
            builder.append(record.symbol());
        StringFactory.objMetadata(record, builder);
        return builder.toString();
    }

    public static String map(final Map<? extends Obj, ? extends Obj> map) {
        final StringBuilder builder = new StringBuilder();
        builder.append(LBRACKET);
        if (map.isEmpty())
            builder.append(COLON);
        else {
            for (final Map.Entry<? extends Obj, ? extends Obj> entry : map.entrySet()) {
                builder.append(nestedObj(entry.getKey())).append(COLON).append(nestedObj(entry.getValue())).append(COMMA);
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append(RBRACKET);
        return builder.toString();
    }

    public static String list(final Lst<? extends Obj> list) {
        final StringBuilder builder = new StringBuilder();
        if (list.get() instanceof PList) {
            builder.append(LBRACKET);
            if (list.<PList>get().isEmpty())
                builder.append(SEMICOLON);
            else {
                for (Obj object : list.<PList<Obj>>get()) {
                    builder.append(nestedObj(object)).append(SEMICOLON);
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(RBRACKET);
        } else if (null != list.get())
            builder.append(list.get().toString());
        else
            builder.append(list.symbol());
        StringFactory.objMetadata(list, builder);
        return builder.toString();
    }

    public static String obj(final Obj obj) {
        final StringBuilder builder = new StringBuilder();
        final Object o = obj.get();
        if (null == o)
            builder.append(obj.symbol());
        else {
            final boolean parentheses = o instanceof Inst && (null != obj.label() || !obj.q().isOne());
            if (parentheses) builder.append(LPAREN);
            if (o instanceof Inst)
                builder.append(obj.symbol());
            final String oString = o instanceof String ?
                    String.format("'%s'", ((String) o).replaceAll("[\"\\\\]", "\\\\$0")) :
                    o.toString();
            builder.append(oString);
            if (parentheses) builder.append(RPAREN);
        }
        StringFactory.objMetadata(obj, builder);
        return builder.toString();
    }

    public static String inst(final Inst inst) {
        final StringBuilder builder = new StringBuilder();
        if (!TInst.some().get().equals(inst.get())) {
            // boolean head = true;
            for (Inst single : inst.iterable()) {
                /*if (head) {
                    head = false;
                    if (!single.domain().q().isZero()) {
                        builder.append(LPAREN).append((Obj)single.domain().access(null)).append(RPAREN); // TODO: do we show domain in toString()?
                    }
                }*/
                builder.append(LBRACKET);
                boolean opcode = true;
                for (Obj object : single.<Iterable<Obj>>get()) {
                    if (opcode) {
                        builder.append(object.get().toString()).append(COMMA);
                        opcode = false;
                    } else
                        builder.append(object).append(COMMA);
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append(RBRACKET);
                builder.append(quantifier(single.q()));
                if (null != single.label())
                    builder.append(TILDE).append(single.label());
            }
            if (null != inst.label()) // TODO: this shouldn't happen over the entire stream
                builder.append(TILDE).append(inst.label());
        } else
            builder.append(inst.symbol());
        return builder.toString();
    }

    public static String quantifier(final Q quantifier) {
        if (quantifier.isOne())
            return EMPTY;
        else if (quantifier.isStar())
            return LCURL + ASTERIX + RCURL;
        else if (quantifier.isQMark())
            return LCURL + QUESTION + RCURL;
        else if (quantifier.isPlus())
            return LCURL + CROSS + RCURL;
        else if (quantifier.constant())
            return LCURL + quantifier.peek() + RCURL;
        else
            return LCURL +
                    (quantifier.peek().isMin() ? EMPTY : quantifier.object().peek()) + COMMA +
                    (quantifier.last().isMax() ? EMPTY : quantifier.object().last()) + RCURL;
    }
}
