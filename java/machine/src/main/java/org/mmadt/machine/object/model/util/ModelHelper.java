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

import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.map.AsInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.composite.util.PMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ModelHelper {

    public static <O extends Obj> O fromModel(final Obj from, final Obj to) {
        final Model model = from.model();
        if (to instanceof Rec) {
            final Map<Obj, Obj> map = new PMap<>();
            for (final Map.Entry<Obj, Obj> entry : to.<Map<Obj, Obj>>get().entrySet()) {
                map.put(fromModel(model, entry.getKey()), fromModel(model, entry.getValue()));
            }
            return (O) TRec.of(map).copy(to);
        } else if (to instanceof Lst) {
            final List<Obj> list = new PList<>();
            for (final Obj entry : to.<List<Obj>>get()) {
                list.add(fromModel(model, entry));
            }
            return (O) TLst.of(list).copy(to);
        } else if (to.isLabeled()) {
            final O o = (O) model.readOrGet(to, TObj.none()).copy(to);
            return o.isNone() ? (O) to : to.test(o) ? o : (O) TObj.none();
        } else
            return (O) to;
    }


    private static <O extends Obj> O fromModel(final Model model, final Obj obj) {
        O newObj;
        if (obj.isSym()) {
            newObj = (O) model.readOrGet(obj, TObj.none());
        } else if (obj instanceof Rec) {
            final Map<Obj, Obj> map = new PMap<>();
            for (final Map.Entry<Obj, Obj> entry : obj.<Map<Obj, Obj>>get().entrySet()) {
                map.put(fromModel(model, entry.getKey()), fromModel(model, entry.getValue()));
            }
            newObj = (O) TRec.of(map);
        } else if (obj instanceof Lst) {
            final List<Obj> list = new PList<>();
            for (final Obj entry : obj.<List<Obj>>get()) {
                list.add(fromModel(model, entry));
            }
            newObj = (O) TLst.of(list);
        } else if (obj.isInst()) {
            if (InstHelper.singleInst((Inst) obj)) {
                final List<Obj> list = new PList<>();
                for (final Obj entry : obj.<List<Obj>>get()) {
                    list.add(fromModel(model, entry));
                }
                newObj = (O) new TInst(list);
            } else {
                final List<Inst> insts = new ArrayList<>();
                for (final Inst entry : obj.<List<Inst>>get()) {
                    insts.add(fromModel(model, entry));
                }
                newObj = (O) TInst.of(insts);
            }
        } else if (obj.isLabeled()) {
            final O o = (O) model.readOrGet(obj, TObj.none());
            newObj = o.isNone() ? (O) obj : obj.test(o) ? o : (O) TObj.none();
        } else
            newObj = (O) obj;
        //////////////////////////
        newObj = newObj.copy(obj);
        return newObj.isReference() ? newObj.access(fromModel(model, newObj.access())) : newObj;
    }

    public static Model fromObj(final Obj obj) {
        return ModelHelper.fromObj(obj.model(), obj);
    }

    private static Model fromObj(final Model model, final Obj obj) {
        Model update = model;
        if (obj.isLabeled())
            update = update.write(obj);
        else if (obj instanceof Rec)
            update = fromRec(update, (Rec<?, ?>) obj);
        else if (obj instanceof Lst)
            update = fromLst(update, (Lst<?>) obj);
        return update;
    }

    private static <K extends Obj, V extends Obj> Model fromRec(final Model model, final Rec<K, V> rec) {
        Model update = model;
        for (final Map.Entry<K, V> entry : rec.<Map<K, V>>get().entrySet()) {
            update = fromObj(update, entry.getKey());
            update = fromObj(update, entry.getValue());
        }
        return update;
    }

    private static <V extends Obj> Model fromLst(final Model model, final Lst<V> lst) {
        Model update = model;
        for (final V v : lst.<List<V>>get()) {
            update = fromObj(update, v);
        }
        return update;
    }

    public static <O extends Obj> O match(final O obj) {
        if (!obj.isLabeled())
            return obj;
        else if (obj.isReference()) {
            final O clone = obj.model(obj.model().write(obj));
            return AsInst.<O>create(clone.access(null)).attach(clone);
        } else {
            final O storedObj = obj.model().read(obj);
            if (null == storedObj)
                return obj.model(obj.model().write(obj)); // if the variable is unbound, bind it to the current obj
            else
                return obj.test(storedObj) ? obj : obj.kill(); // test if the current obj is subsumed by the historic obj (if not, drop the obj's quantity to [zero])
        }
    }
}
