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

package org.mmadt.machine.object.impl.composite.inst.map;

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.inst.MapInstruction;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.util.QuantifierHelper;
import org.mmadt.processor.util.FastProcessor;
import org.mmadt.util.IteratorUtils;

import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class AsInst<S extends Obj> extends TInst<S, S> implements MapInstruction<S, S> {

    private AsInst(final Object arg) {
        super(PList.of(Tokens.AS, arg));
    }

    public S apply(final S obj) {
        return this.quantifyRange(obj.as(this.<S>argument(0).mapArg(obj)));
    }

    public static <S extends Obj> AsInst<S> create(final Object arg) {
        return new AsInst<>(arg);
    }

    public static <S extends Obj> S compute(final S from, S to) {
        if (to.isSym()) {
            return from.bind(to.binding());
        } else if (!to.ref().isOne()) {
            return from.mapTo(to.ref()).bind(to.binding()).symbol(to.symbol()).model(from.model());
        } else if (from.isReference()) {
            return AsInst.<S>create(to).attach(from, to.binding() == null ?
                    from :
                    fakeLabel(to.binding(), from.model(from.model().write(to.ref(from.ref())))));
        } else if (from instanceof Lst && to instanceof Lst && null != from.get() && null != to.get()) {  // TODO: test()/match()/as() need to all become the same method!
            final Lst<Obj> fromList = (Lst<Obj>) from;
            final Lst<Obj> toList = (Lst<Obj>) to;
            if (toList.java().size() < fromList.java().size())
                return toList.halt();
            final PList<Obj> temp = new PList<>();
            Model model = from.model();
            for (int i = 0; i < toList.java().size(); i++) {
                final Obj obj = from.model().readOrGet(fromList.get(i), fromList.get(i).as(toList.get(i)));
                if (obj.q().isZero())
                    return toList.halt();
                model = model.write(obj);
                temp.add(obj);
            }
            return (S) TLst.of(temp).bind(to.binding()).model(model).symbol(to.symbol());
        } else if (from.isRec() && to.isRec() && null != from.get() && null != to.get()) {  // TODO: test()/match()/as() need to all become the same method!
            final Rec<Obj, Obj> fromRec = (Rec<Obj, Obj>) from;
            final Rec<Obj, Obj> toRec = (Rec<Obj, Obj>) to;
            final Rec<Obj, Obj> asRec = TRec.of();
            for (final Map.Entry<Obj, Obj> toEntry : toRec.java().entrySet()) {
                final List<Obj> asEntry = AsInst.recKV(fromRec, toEntry.getKey());
                if (toEntry.getValue().test(asEntry.get(1))) {
                    if (!asEntry.get(1).q().isNone())
                        asRec.put(asEntry.get(0).as(toEntry.getKey()), asEntry.get(1).as(toEntry.getValue()));
                } else if (!QuantifierHelper.withinZero(toEntry.getValue().q())) {
                    return fromRec.halt();
                }
            }
            return (S) asRec.bind(to.binding()).symbol(to.symbol());
        } else if (to.isPredicate()) {
            return IteratorUtils.orElse(FastProcessor.process(from.mapTo(to.get())), (S) TObj.none());
        } else if (!to.test(from))
            return to.halt();
        else
            return (from.isType() ? from.set(to.get()) : from).model(from.model()).bind(to.binding()).symbol(to.symbol());
    }

    private static <S extends Obj> S fakeLabel(final String label, final S obj) { // TODO: this is lame
        final TObj temp = (TObj) obj.clone();
        temp.binding = label;
        return (S) temp;
    }

    private static <K extends Obj, V extends Obj> List<Obj> recKV(final Rec<K, V> rec, final K key) {
        for (final Map.Entry<K, V> entry : rec.<Map<K, V>>get().entrySet()) {
            if (key.test(entry.getKey()))
                return List.of(entry.getKey().copy(rec), entry.getValue().copy(rec));
        }
        return List.of(key, TObj.none());
    }
}