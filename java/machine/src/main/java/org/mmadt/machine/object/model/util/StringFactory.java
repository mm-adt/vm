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

package org.mmadt.machine.object.model.util;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Stream;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.type.PAnd;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.Pattern;
import org.mmadt.processor.function.QFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mmadt.language.compiler.Tokens.AMPERSAND;
import static org.mmadt.language.compiler.Tokens.ASTERIX;
import static org.mmadt.language.compiler.Tokens.BAR;
import static org.mmadt.language.compiler.Tokens.COLON;
import static org.mmadt.language.compiler.Tokens.COMMA;
import static org.mmadt.language.compiler.Tokens.CROSS;
import static org.mmadt.language.compiler.Tokens.DEFINE;
import static org.mmadt.language.compiler.Tokens.EMPTY;
import static org.mmadt.language.compiler.Tokens.LBRACKET;
import static org.mmadt.language.compiler.Tokens.LCURL;
import static org.mmadt.language.compiler.Tokens.LPAREN;
import static org.mmadt.language.compiler.Tokens.MAPSFROM;
import static org.mmadt.language.compiler.Tokens.MAPSTO;
import static org.mmadt.language.compiler.Tokens.MODEL;
import static org.mmadt.language.compiler.Tokens.NEWLINE;
import static org.mmadt.language.compiler.Tokens.QUESTION;
import static org.mmadt.language.compiler.Tokens.RANGLE;
import static org.mmadt.language.compiler.Tokens.RBRACKET;
import static org.mmadt.language.compiler.Tokens.RCURL;
import static org.mmadt.language.compiler.Tokens.RPAREN;
import static org.mmadt.language.compiler.Tokens.SEMICOLON;
import static org.mmadt.language.compiler.Tokens.SPACE;
import static org.mmadt.language.compiler.Tokens.STEP;
import static org.mmadt.language.compiler.Tokens.TILDE;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class StringFactory {

    private StringFactory() {
        // for static method use
    }

    public static String model(final Model model) {
        StringBuilder builder = new StringBuilder();
        builder.append(MODEL).append(model.get("db").get().toString()).append(NEWLINE);
        return builder.toString();
    }

    private static void objectMetadata(final Obj object, final StringBuilder builder) {
        if (!object.q().isOne())
            builder.append(object.q());
        if (null != object.label())
            builder.append(TILDE).append(object.label());
        if (!object.access().isZero())
            builder.append(SPACE).append(MAPSFROM).append(SPACE).append(object.access());
        if (null != object.members()) {
            builder.append(NEWLINE);
            for (final Map.Entry<Obj, Obj> member : object.members().entrySet()) {
                builder.append(RANGLE).append(SPACE).append(member.getKey()).append(MAPSTO).append(SPACE).append(member.getValue()).append(NEWLINE);
            }
            builder.deleteCharAt(builder.length() - 1);
        }
        if (null != object.instructions()) {
            builder.append(NEWLINE);
            for (final Map.Entry<Inst, Inst> inst : object.instructions().entrySet()) {
                builder.append(SPACE).append(STEP).append(SPACE).append(inst.getKey()).append(SPACE).append(MAPSTO).append(SPACE).append(inst.getValue()).append(NEWLINE);
            }
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private static String nestedObject(final Obj object) {
        final StringBuilder builder = new StringBuilder();
        if (object.constant() || !object.named())
            builder.append(object);
        else {
            builder.append(object.symbol());
            StringFactory.objectMetadata(object, builder);
        }
        return builder.toString();
    }

    public static String stream(final Stream<? extends Obj> stream) {
        final StringBuilder builder = new StringBuilder();
        for (Obj object : stream) {
            builder.append(nestedObject(object)).append(COMMA);
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
        StringFactory.objectMetadata(record, builder);
        return builder.toString();
    }

    public static String map(final Map<? extends Obj, ? extends Obj> map) {
        final StringBuilder builder = new StringBuilder();
        builder.append(LBRACKET);
        if (map.isEmpty())
            builder.append(COLON);
        else {
            for (final Map.Entry<? extends Obj, ? extends Obj> entry : map.entrySet()) {
                builder.append(nestedObject(entry.getKey())).append(COLON).append(nestedObject(entry.getValue())).append(COMMA);
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
                    builder.append(nestedObject(object)).append(SEMICOLON);
                }
                builder.deleteCharAt(builder.length() - 1);
            }
            builder.append(RBRACKET);
        } else if (null != list.get())
            builder.append(list.get().toString());
        else
            builder.append(list.symbol());
        StringFactory.objectMetadata(list, builder);
        return builder.toString();
    }

    public static String conjunction(final PAnd conjunction) {
        final StringBuilder builder = new StringBuilder();

        for (final Pattern pred : conjunction.predicates()) {
            builder.append(pred instanceof Obj ? nestedObject((Obj) pred) : pred);
            builder.append(AMPERSAND);
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    public static String object(final Obj object) {
        final StringBuilder builder = new StringBuilder();
        final Object o = object.get();
        if (null == o)
            builder.append(object.symbol());
        else if (o instanceof Inst) {
            if (null != object.label() || !object.q().isOne()) builder.append(LPAREN);
            builder.append(typeInstructions((Inst) o));
            if (null != object.label() || !object.q().isOne()) builder.append(RPAREN);
        } else {
            final boolean parens =
                    (o instanceof Obj || o instanceof Stream || o instanceof PAnd) &&
                            (null != object.label() || !object.q().isOne());
            if (parens)
                builder.append(LPAREN);
            builder.append(o);
            if (parens)
                builder.append(RPAREN);
        }

        StringFactory.objectMetadata(object, builder);
        return builder.toString();
    }

    public static String inst(final Inst inst) {
        final StringBuilder builder = new StringBuilder();
        if (inst.get() instanceof PAnd)
            builder.append((PAnd) inst.get());
        else if (!TInst.some().get().equals(inst.get())) {
            for (Inst single : inst.iterable()) {
                boolean first = true;
                builder.append(LBRACKET);
                if (single.opcode().get().equals(DEFINE))
                    builder.append(DEFINE).append(COMMA).append(single.get(TInt.oneInt()).get().toString()).append(COMMA).append(single.get(TInt.twoInt()));
                else if (single.opcode().get().equals(MODEL))
                    builder.append(MODEL).append(COMMA).append(single.get(TInt.oneInt()).get().toString()).append(COMMA).append(single.get(TInt.twoInt()));
                else {
                    for (Obj object : single.<Iterable<Obj>>get()) {
                        if (first) {
                            builder.append(object.get().toString()).append(COMMA);
                            first = false;
                        } else
                            builder.append(object).append(COMMA);
                    }
                    builder.deleteCharAt(builder.length() - 1);
                }
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
                    (quantifier.peek().get().equals(Integer.MIN_VALUE) ? EMPTY : quantifier.peek()) + COMMA +
                    (quantifier.last().get().equals(Integer.MAX_VALUE) ? EMPTY : quantifier.last()) + RCURL;
    }

    public static String string(final Str string) {
        final StringBuilder builder = new StringBuilder();
        if (string.isInstance()) {
            if (string.get() instanceof Stream)
                builder.append(string.symbol()).append("..");
            else if (java.util.regex.Pattern.compile("^[a-zA-Z]*$").matcher(string.<String>get()).matches())
                builder.append("'").append(string.<String>get()).append("'");
            else
                builder.append(String.format("\"%s\"", string.<String>get().replaceAll("[\"\\\\]", "\\\\$0")));
        } else if (string.get() instanceof Inst) {
            builder.append(typeInstructions(string.get()));
        } else if (string.isType() && null != string.get())
            builder.append(string.get().toString());
        else
            builder.append(string.symbol());
        StringFactory.objectMetadata(string, builder);
        return builder.toString();
    }

    ////////////// PROCESSOR
    public static String function(final QFunction function, final Object... args) {
        final List<Object> arguments = new ArrayList<>(args.length);
        Collections.addAll(arguments, args);
        arguments.remove(null);

        String name = function.getClass().getSimpleName();
        if (arguments.size() > 0)
            name = name + "(";
        for (final Object object : arguments) {
            name = name + object + ",";
        }
        if (arguments.size() > 0) {
            name = name.substring(0, name.length() - 1);
            name = name + ")";
        }
        if (!function.quantifier().isOne())
            name = name + function.quantifier();
        if (null != function.label())
            name = name + "~" + function.label();
        return name;
    }

    private static String typeInstructions(final Inst inst) {
        final StringBuilder builder = new StringBuilder();
        if (inst.<Inst>peek().opcode().java().equals(Tokens.IS)) {
            if (inst.<Inst>peek().args().get(0) instanceof Inst && ((Inst) inst.<Inst>peek().args().get(0)).opcode().java().equals(Tokens.OR)) {
                for (final Obj i : ((Inst) inst.<Inst>peek().args().get(0)).args()) {
                    builder.append(((Inst) i).args().get(0)).append(BAR);
                }
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            }
        }
        return inst.toString();
    }
}
