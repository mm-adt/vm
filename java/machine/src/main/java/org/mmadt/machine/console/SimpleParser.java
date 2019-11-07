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

package org.mmadt.machine.console;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TQ;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Q;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithOrderedRing;
import org.mmadt.machine.object.model.util.OperatorHelper;
import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@BuildParseTree
public class SimpleParser extends BaseParser<Object> {

    final Map<Integer, Rule> ARROWS = new HashMap<>() {{
        put(0, Terminal(">"));
        put(1, Terminal("->"));
        put(2, Terminal("-->"));
        put(3, Terminal("--->"));
    }};

    final Rule COLON = Terminal(Tokens.COLON);
    final Rule COMMA = Terminal(Tokens.COMMA);
    final Rule PERIOD = Terminal(Tokens.PERIOD);
    final Rule AND = Terminal(Tokens.AMPERSAND);
    final Rule OR = Terminal(Tokens.BAR);
    final Rule STAR = Terminal(Tokens.ASTERIX);
    final Rule PLUS = Terminal(Tokens.CROSS);
    final Rule QMARK = Terminal(Tokens.QUESTION);
    final Rule SUB = Terminal(Tokens.DASH);
    final Rule DIV = Terminal(Tokens.BACKSLASH);
    final Rule MAPSFROM = Terminal(Tokens.MAPSFROM);
    final Rule MAPSTO = Terminal(Tokens.MAPSTO);
    final Rule LBRACKET = Terminal(Tokens.LBRACKET);
    final Rule RBRACKET = Terminal(Tokens.RBRACKET);
    final Rule LCURL = Terminal(Tokens.LCURL);
    final Rule RCURL = Terminal(Tokens.RCURL);
    final Rule TRIPLE_QUOTE = Terminal(Tokens.DQUOTE + Tokens.DQUOTE + Tokens.DQUOTE);
    final Rule DOUBLE_QUOTE = Terminal(Tokens.DQUOTE);
    final Rule SINGLE_QUOTE = Terminal(Tokens.SQUOTE);
    final Rule TILDE = Terminal(Tokens.TILDE);
    final Rule LPAREN = Terminal(Tokens.LPAREN);
    final Rule RPAREN = Terminal(Tokens.RPAREN);
    final Rule EQUALS = Terminal(Tokens.EQUALS);
    final Rule GT = Terminal(Tokens.GT);
    final Rule LT = Terminal(Tokens.LT);
    final Rule NEQ = Terminal(Tokens.NEQ);
    final Rule SEMICOLON = Terminal(Tokens.SEMICOLON);
    final Rule TRUE = Terminal(Tokens.TRUE);
    final Rule FALSE = Terminal(Tokens.FALSE);

    /// built-int type symbols
    final Rule INT = Terminal(Tokens.INT);
    final Rule REAL = Terminal(Tokens.REAL);
    final Rule STR = Terminal(Tokens.STR);
    final Rule BOOL = Terminal(Tokens.BOOL);
    final Rule REC = Terminal(Tokens.REC);
    final Rule LST = Terminal(Tokens.LIST);
    final Rule INST = Terminal(Tokens.INST);

    final Rule OP_ID = Terminal(Tokens.ID);
    final Rule OP_DEFINE = Terminal(Tokens.DEFINE);
    final Rule OP_MODEL = Terminal(Tokens.MODEL);

    public Rule Source() {
        final Var<Obj> left = new Var<>();
        final Var<Obj> right = new Var<>();
        final Var<String> unary = new Var<>();
        final Var<String> operator = new Var<>();
        return Sequence(
                Optional(SUB, unary.set(Tokens.DASH)), Obj(), left.set((Obj) this.pop()), ACTION(unary.isNotSet() || left.set(((WithMinus) left.getAndClear()).neg())), this.push(left.get()),
                ZeroOrMore(left.set((Obj) this.pop()),
                        BinaryOperator(operator),
                        Optional(SUB, unary.set(Tokens.DASH)),
                        Obj(), right.set((Obj) this.pop()), ACTION(unary.isNotSet() || (unary.clear() && left.set(((WithMinus) left.get()).neg()))), this.push(OperatorHelper.operation(operator.get(), left.get(), right.get()))));
    }

    ///////////////

    /*public Rule Source2() {
        return Expression();
    }*/

    /*Rule Expression() {
        return FirstOf(
                Grouping(),
                Binary(),
                Obj());
    }

    Rule Binary() {
        return Sequence(Expression(), BinaryOperator(), Expression(), swap(), this.push(OperatorHelper.operation(type(this.pop()), type(this.pop()), type(this.pop()))));
    }

    Rule Grouping() {
        return Sequence(LPAREN, Expression(), RPAREN);
    }*/

    Rule Obj() {
        return Sequence(
                FirstOf(Bool(),
                        Real(),
                        Int(),
                        Str(),
                        Rec(),
                        Lst(),
                        Inst()),                                                                               // obj
                Optional(Quantifier(), swap(), this.push((type(this.pop())).q((Q) this.pop()))),              // {quantifier}
                Optional(MAPSFROM, Inst(), swap(), this.push(type(this.pop()).access((Inst) this.pop()))));   // <= inst
    }

    Rule Lst() {
        final Var<PList<Obj>> list = new Var<>(new PList<>());
        return FirstOf(
                Sequence(LST, this.push(TLst.some())),
                Sequence(LBRACKET, SEMICOLON, RBRACKET, this.push(TLst.of())),
                Sequence(
                        LBRACKET, Obj(), ACTION(list.get().add((Obj) pop()) || true),
                        ZeroOrMore(SEMICOLON, Obj(), ACTION(list.get().add((Obj) pop()) || true)),
                        RBRACKET, this.push(TLst.of(list.get()))));
    }

    Rule Rec() {
        final Var<PMap<Obj, Obj>> rec = new Var<>(new PMap<>());
        return FirstOf(
                Sequence(REC, this.push(TRec.some())),
                Sequence(LBRACKET, COLON, RBRACKET, this.push(TRec.of())),
                Sequence(LBRACKET,
                        Obj(), COLON, Obj(), swap(), ACTION(null == rec.get().put(type(this.pop()), type(this.pop())) || true),
                        RBRACKET, this.push(TRec.of(rec.get()))));
    }

    @SuppressSubnodes
    Rule Real() {
        return FirstOf(
                Sequence(REAL, this.push(TReal.of())),
                Sequence(Sequence(OneOrMore(Digit()), PERIOD, OneOrMore(Digit())), this.push(TReal.of(Float.valueOf(match())))));
    }

    @SuppressSubnodes
    Rule Int() {
        return FirstOf(
                Sequence(INT, this.push(TInt.some())),
                Sequence(OneOrMore(Digit()), this.push(TInt.of(Integer.valueOf(match())))));
    }

    @SuppressSubnodes
    Rule Str() {
        return FirstOf(
                Sequence(STR, this.push(TStr.some())),
                Sequence(TRIPLE_QUOTE, ZeroOrMore(Sequence(TestNot(TRIPLE_QUOTE), ANY)), this.push(TStr.of(match())), TRIPLE_QUOTE),
                Sequence(SINGLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\\'")), ANY)), this.push(TStr.of(match())), SINGLE_QUOTE),
                Sequence(DOUBLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\"")), ANY)), this.push(TStr.of(match())), DOUBLE_QUOTE));
    }

    @SuppressSubnodes
    Rule Bool() {
        return FirstOf(
                Sequence(BOOL, this.push(TBool.some())),
                Sequence(TRUE, this.push(TBool.of(true))),
                Sequence(FALSE, this.push(TBool.of(false))));
    }

    Rule Inst() {
        return FirstOf(
                Sequence(INST, this.push(TInst.some())),
                Sequence(this.push(TInst.ids()), OneOrMore(Single_Inst(), this.push(this.<Inst>type(this.pop()).mult(type(this.pop()))))));
    }

    @SuppressSubnodes
    Rule Single_Inst() {
        final Var<String> opcode = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(
                LBRACKET,
                VarSym(), opcode.set(match()),                                      // opcode
                ZeroOrMore(COMMA, Obj(), args.get().add(type(this.pop()))),         // arguments
                RBRACKET, this.push(TInst.of(opcode.get(), args.get())));
    }

    @SuppressSubnodes
    Rule VarSym() {
        return Sequence(Char(), ZeroOrMore(FirstOf(Char(), Digit())));
    }

    /*@SuppressSubnodes
    Rule BinaryOperator() {
        return Sequence(FirstOf(STAR, PLUS, DIV, SUB, AND, OR), this.push(this.match().trim()));
    }*/

    @SuppressSubnodes
    Rule BinaryOperator(final Var<String> operator) {
        return Sequence(FirstOf(STAR, PLUS, DIV, SUB, AND, OR), operator.set(this.match().trim()));
    }

    @SuppressNode
    Rule Terminal(final String string) {
        return Sequence(Spacing(), string, Spacing());
    }

    @SuppressNode
    Rule Spacing() {
        return ZeroOrMore(FirstOf(
                OneOrMore(AnyOf(" \t\r\n\f")),                                                               // whitespace
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),                                        // block comment
                Sequence("//", ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI)))); // line comment
    }

    @SuppressNode
    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressNode
    Rule Char() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    @SuppressSubnodes
    Rule Quantifier() {
        return Sequence(
                LCURL,  // TODO: the *, +, ? shorthands assume Int ring. (this will need to change)
                FirstOf(Sequence(STAR, this.push(new TQ<>(0, Integer.MAX_VALUE))),                                    // {*}
                        Sequence(PLUS, this.push(new TQ<>(1, Integer.MAX_VALUE))),                                    // {+}
                        Sequence(QMARK, this.push(new TQ<>(0, 1))),                                                   // {?}
                        Sequence(COMMA, Obj(), this.push(new TQ<>((this.<WithOrderedRing>type(this.peek())).min(), type(this.pop())))),          // {,10}
                        Sequence(Obj(),
                                FirstOf(Sequence(COMMA, Obj(), swap(), this.push(new TQ<>(type(this.pop()), type(this.pop())))), // {1,10}
                                        Sequence(COMMA, this.push(new TQ<>(type(this.peek()), (this.<WithOrderedRing>type(this.peek())).max()))),             // {10,}
                                        this.push(new TQ<>(type(this.peek()), type(this.pop())))))),                                 // {1}
                RCURL);
    }

    <A extends Obj> A type(final Object object) {
        return (A) object;
    }


}
