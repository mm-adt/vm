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

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TModel;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.type.PList;
import org.mmadt.machine.object.model.type.PMap;
import org.mmadt.machine.object.model.type.algebra.WithMinus;
import org.mmadt.machine.object.model.type.algebra.WithMult;
import org.mmadt.machine.object.model.type.algebra.WithPlus;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.DontLabel;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.support.Var;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple parser for mm-lang.
 * TODO: This should be rewritten using a faster parser framework.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
@BuildParseTree
public class Parser extends BaseParser<Object> {

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

    final Rule OP_ID = Terminal(Tokens.ID);
    final Rule OP_DEFINE = Terminal(Tokens.DEFINE);
    final Rule OP_MODEL = Terminal(Tokens.MODEL);

    public Rule Source() {
        return Source(TModel.of("ex"));
    }

    public Rule Source(final TModel model) {
        final Var<Obj> bytecode = new Var<>();
        final Var<SymbolTable> symtable = new Var<>(new SymbolTable(model));
        return Sequence(this.push(symtable.get()), Bytecode(bytecode), this.push(new SymbolTable(Compiler.validateTypes(this.popSymTable().model())).addInst(Tokens.ASTERIX, (TInst) bytecode.getAndClear())), EOI);
    }

    Rule Bytecode(final Var<Obj> bytecode) {
        return Sequence(this.newFrame(), OneOrMore(BCode()), bytecode.set(this.peekSymTable().bytecode()), this.mergeFrame(false));
    }

    Rule BCode() {
        final Var<Inst> inst = new Var<>();
        final Var<String> operator = new Var<>(Tokens.ASTERIX);
        final Var<Obj> bytecode = new Var<>();
        return Sequence(Optional(BinaryOperator(), operator.set(match())),
                FirstOf(Sequence(LPAREN, Bytecode(bytecode), RPAREN, this.push(this.popSymTable().addInst(operator.getAndSet(Tokens.ASTERIX), (TInst) bytecode.getAndClear()))),
                        Sequence(Inst(inst), this.push(this.popSymTable().addInst(operator.getAndSet(Tokens.ASTERIX), inst.getAndClear())))));
    }

    Rule Inst(final Var<Inst> inst) {
        return Sequence(LBRACKET, FirstOf(
                Inst_Functor(inst),
                Inst_Id(inst),
                Inst_Define(inst),
                Inst_Model(inst),
                Inst_Default(inst)
        ), RBRACKET, Optional(ExactQuantifier(inst)), Optional(As(inst)));
    }

    Rule Inst_Functor(final Var<Inst> inst) {
        final Var<String> opcode = new Var<>();
        final Var<Obj> value = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(Sequence(MAPSFROM, VarSym()), opcode.set(match()), ZeroOrMore(COMMA, TypeExpress(value), args.get().add(value.getAndClear())), inst.set(TInst.of(opcode.get(), args.get())));
    }

    Rule Inst_Default(final Var<Inst> inst) {
        final Var<String> opcode = new Var<>();
        final Var<Obj> value = new Var<>();
        final Var<PList<Obj>> args = new Var<>(new PList<>());
        return Sequence(VarSym(), opcode.set(match()), ZeroOrMore(COMMA, TypeExpress(value), args.get().add(value.getAndClear())), inst.set(TInst.of(opcode.get(), args.get())));
    }

    Rule Inst_Define(final Var<Inst> inst) {
        final Var<Obj> symbol = new Var<>();
        final Var<Obj> type = new Var<>();
        return Sequence(OP_DEFINE, COMMA, this.newFrame(), Symbol(symbol), this.push(this.popSymTable().addSymbol(symbol.get().symbol())), COMMA, Type(type), new Action() {
            @Override
            public boolean run(Context context) {
                peekSymTable().getSymbol(symbol.get().symbol()).setObject(type.get());
                return true;
            }
        }, inst.set(TInst.of(Tokens.DEFINE, symbol.get().symbol(), type.get())), this.mergeFrame(true));
    }

    Rule Inst_Id(final Var<Inst> inst) {
        return Sequence(OP_ID, inst.set(TInst.of(Tokens.ID)));
    }

    Rule Inst_Model(final Var<Inst> inst) {
        final Var<String> from = new Var<>();
        final Var<String> to = new Var<>();
        final Var<Obj> bytecode = new Var<>();
        return Sequence(OP_MODEL, COMMA, VarSym(), from.set(match()), MAPSTO, VarSym(), to.set(match()), COMMA, Bytecode(bytecode), inst.set(TInst.of("model", from.get() + "=>" + to.get(), bytecode.getAndClear())));
    }

    ///////////////

    Rule TypeExpress(final Var<Obj> object) {
        return FirstOf(Type(object), Expression(object));
    }

    Rule Type(final Var<Obj> object) {
        return Sequence(
                Optional(LPAREN),
                Expression(object),
                Optional(Access(object)),
                ZeroOrMore(Rewrite(object)),
                Optional(RPAREN));
    }

    Rule Quantifier(final Var<Obj> object) {
        final Var<Obj> min = new Var<>(TInt.of(Integer.MIN_VALUE));
        final Var<Obj> max = new Var<>(TInt.of(Integer.MAX_VALUE));
        return Sequence(LCURL,
                FirstOf(Sequence(Int(min), COMMA, Int(max)),
                        Sequence(Int(min), COMMA),
                        Sequence(COMMA, Int(max)),
                        Sequence(Int(min), max.set(min.get())),
                        Sequence(STAR, min.set(TInt.of(0))), // min and max preset
                        Sequence(QMARK, min.set(TInt.of(0)), max.set(TInt.of(1))),
                        Sequence(PLUS, min.set(TInt.of(1)))),
                RCURL, object.set(object.getAndClear().q(min.get().get(), max.get().get())));
    }

    Rule ExactQuantifier(final Var<? extends Obj> object) {
        final Var<Obj> quantifier = new Var<>();
        return Sequence(LCURL, Int(quantifier), RCURL, object.set(object.getAndClear().q((Integer) quantifier.get().get())));
    }

    Rule Expression(final Var<Obj> object) {
        final Var<Obj> right = new Var<>();
        final Var<String> operator = new Var<>(Tokens.AMPERSAND);
        return Sequence(Optional(LPAREN), Atom(object), ZeroOrMore(Optional(BinaryOperator(), operator.set(match())), Atom(right), new Action<>() {
            @Override
            public boolean run(final Context<Object> context) {
                final String op = operator.get();
                switch (op) {
                    case (Tokens.AMPERSAND):
                        return object.set(object.get().and(right.getAndClear()));
                    case (Tokens.BAR):
                        return object.set(object.get().or(right.getAndClear()));
                    case (Tokens.CROSS):
                        return object.set(((WithPlus) object.get()).plus((WithPlus) right.getAndClear()));
                    case (Tokens.ASTERIX):
                        return object.set(((WithMult) object.get()).mult((WithMult) right.getAndClear()));
                    case (Tokens.DASH):
                        return object.set(((WithMinus) object.get()).minus((WithMinus) right.getAndClear()));
                    default:
                        throw new RuntimeException("Unknown operator: " + op);
                }
            }
        }), Optional(RPAREN), Optional(Quantifier(object)), Optional(Binding(object)));
    }

    Rule Atom(final Var<Obj> object) {
        final Var<Boolean> negate = new Var<>(Boolean.FALSE);
        return Sequence(
                Optional(SUB, negate.set(Boolean.TRUE)),
                FirstOf(Str(object),
                        Real(object),
                        Int(object),
                        Rec(object),
                        Bool(object),
                        Variable(object),
                        Symbol(object),
                        Bytecode(object),
                        Lst(object)),
                !negate.get() || object.set(((WithMinus) object.getAndClear()).neg()),
                Optional(Quantifier(object)),
                Optional(Binding(object)));
    }

    Rule Binding(final Var<Obj> object) {
        return Sequence(TILDE, VarSym(), object.set(object.get().label(match())), this.push(this.popSymTable().addVariable(match(), object.get())));
    }

    Rule BinaryOperator() {
        return FirstOf(STAR, PLUS, SUB, AND, OR);
    }

    Rule Access(final Var<Obj> type) {
        final Var<Obj> access = new Var<>();
        return Sequence(MAPSFROM, Bytecode(access), type.set(type.get().accessFrom(((TInst) access.get()).domain(((TInst) access.get()).domain()))));
    }

    Rule Arrows() {
        return FirstOf(ARROWS.get(1), ARROWS.get(2), ARROWS.get(3));
    }

    Rule Rewrite(final Var<Obj> type) {
        final Var<Obj> lhs = new Var<>();
        final Var<Obj> rhs = new Var<>(TInst.none());
        return FirstOf(Sequence(Arrows(), this.newFrame(), Bytecode(lhs), MAPSTO, Optional(Bytecode(rhs)), type.set(type.get().inst((Inst) lhs.getAndClear(), (Inst) rhs.getAndClear())), this.mergeFrame(true)),
                Sequence(ARROWS.get(0), this.newFrame(), Expression(lhs), MAPSTO, Expression(rhs), type.set(((TObj) type.get()).member(lhs.getAndClear(), rhs.getAndClear())), this.mergeFrame(true)));
    }

    ///////

    @SuppressNode
    @DontLabel
    Rule Terminal(final String string) {
        return Sequence(Spacing(), string, Spacing());
    }

    @SuppressNode
    Rule Spacing() {
        return ZeroOrMore(FirstOf(
                // whitespace
                OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),
                // traditional comment
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),
                // end of line comment
                Sequence("//", ZeroOrMore(TestNot(AnyOf("\r\n")), ANY), FirstOf("\r\n", '\r', '\n', EOI))
        ));
    }

    Rule Lst(final Var<Obj> object) {
        final Var<PList<Obj>> list = new Var<>(new PList<>());
        return Sequence(LBRACKET,
                FirstOf(SEMICOLON, // empty list
                        Sequence(Entry(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                return list.get().add((Obj) pop());
                            }
                        }, ZeroOrMore(SEMICOLON, Entry(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                return list.get().add((Obj) pop());
                            }
                        }))), RBRACKET, object.set(TLst.of(list.get())));
    }

    Rule Rec(final Var<Obj> object) {
        final Var<PMap<Obj, Obj>> rec = new Var<>(new PMap<>());
        return Sequence(LBRACKET,
                FirstOf(COLON, // empty record
                        Sequence(Field(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                rec.get().put((Obj) pop(), (Obj) pop());
                                return true;
                            }
                        }, ZeroOrMore(COMMA, Field(), new Action<>() {
                            @Override
                            public boolean run(final Context<Object> context) {
                                rec.get().put((Obj) pop(), (Obj) pop());
                                return true;
                            }
                        }))), RBRACKET, object.set(TRec.of(rec.get())));
    }

    @SuppressSubnodes
    Rule Real(final Var<Obj> object) {
        return Sequence(Sequence(OneOrMore(Digit()), PERIOD, OneOrMore(Digit())), object.set(TReal.of(Float.valueOf(match()))));
    }

    @SuppressSubnodes
    Rule Int(final Var<Obj> object) {
        return Sequence(OneOrMore(Digit()), object.set(TInt.of(Integer.valueOf(match()))));
    }

    @SuppressSubnodes
    Rule As(final Var<? extends Obj> object) {
        return Sequence(TILDE, VarSym(), object.set(object.get().label(match())));
    }


    @SuppressSubnodes
    Rule Variable(final Var<Obj> object) {
        return Sequence(VarSym(), this.peekSymTable().hasVariable(match()), object.set(this.peekSymTable().getVariable(match())));
    }

    Rule VarSym() {
        return Sequence(Char(), ZeroOrMore(FirstOf(Char(), Digit())));
    }

    @SuppressSubnodes
    Rule Symbol(final Var<Obj> object) {
        return Sequence(Sequence(Char(), ZeroOrMore(FirstOf(Char(), Digit()))), new Action() {
            @Override
            public boolean run(final Context context) {
                final String symbol = match();
                switch (symbol) {
                    case (Tokens.OBJ):
                        return object.set(TObj.some());
                    case (Tokens.BOOL):
                        return object.set(TBool.some());
                    case (Tokens.INT):
                        return object.set(TInt.some());
                    case (Tokens.REAL):
                        return object.set(TReal.some());
                    case (Tokens.STR):
                        return object.set(TStr.some());
                    case (Tokens.REC):
                        return object.set(TRec.some());
                    case (Tokens.LIST):
                        return object.set(TLst.some());
                }
                if (!peekSymTable().hasSymbol(symbol))
                    push(popSymTable().addSymbol(symbol));
                return object.set(peekSymTable().getSymbol(symbol));
            }
        });
    }

    @SuppressSubnodes
    Rule Str(final Var<Obj> object) {
        return FirstOf(
                Sequence(TRIPLE_QUOTE, ZeroOrMore(Sequence(TestNot(TRIPLE_QUOTE), ANY)), object.set(TStr.of(match())), TRIPLE_QUOTE),
                Sequence(SINGLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\\'")), ANY)), object.set(TStr.of(match())), SINGLE_QUOTE),
                Sequence(DOUBLE_QUOTE, ZeroOrMore(Sequence(TestNot(AnyOf("\r\n\"")), ANY)), object.set(TStr.of(match())), DOUBLE_QUOTE));
    }

    @SuppressSubnodes
    Rule Bool(final Var<Obj> object) {
        return Sequence(FirstOf(TRUE, FALSE), object.set(TBool.of(Boolean.valueOf(match()))));
    }

    @SuppressSubnodes
    Rule Field() {
        final Var<Obj> key = new Var<>();
        final Var<Obj> value = new Var<>();
        return Sequence(TypeExpress(key), COLON, TypeExpress(value), this.push(key.get()), this.push(value.get()), swap());
    }

    @SuppressSubnodes
    Rule Entry() {
        final Var<Obj> value = new Var<>();
        return Sequence(TypeExpress(value), this.push(value.get()));
    }

    @SuppressSubnodes
    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressSubnodes
    Rule Char() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    SymbolTable popSymTable() {
        return (SymbolTable) this.pop();
    }

    SymbolTable peekSymTable() {
        return (SymbolTable) this.peek();
    }

    boolean newFrame() {
        return this.push(new SymbolTable(this.peekSymTable().model().symbol()).create(this.peekSymTable()));
    }

    boolean mergeFrame(final boolean dropVariables) {
        return swap() && this.push(this.popSymTable().merge(this.popSymTable(), dropVariables));
    }

}
