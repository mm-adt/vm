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

import org.mmadt.language.compiler.Instructions;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.map.AsInst;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.atomic.Str;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.composite.util.PMap;
import org.mmadt.processor.util.FastProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ModelHelper {

    public static <O extends Obj> O via(final Obj from, final Obj to) {
        O obj;
        if (to.isAtomic() || null == to.get())
            obj = (O) to;
        else {
            if (to.isRec()) {
                final Map<Obj, Obj> map = new PMap<>();
                for (final Map.Entry<Obj, Obj> entry : to.<Map<Obj, Obj>>get().entrySet()) {
                    map.put(via(from, entry.getKey()), via(from, entry.getValue()));
                }
                obj = (O) TRec.of(map).label(to.label());
            } else if (to.isLst()) {
                final List<Obj> list = new PList<>();
                for (final Obj entry : to.<List<Obj>>get()) {
                    list.add(via(from, entry));
                }
                obj = (O) TLst.of(list).label(to.label());
            } else if (to.isInst()) {
                if (InstHelper.singleInst((Inst) to)) {
                    final List<Obj> list = new PList<>();
                    for (final Obj entry : to.<List<Obj>>get()) {
                        list.add(via(from, entry));
                    }
                    obj = (O) Instructions.compile(TInst.of((Str) list.get(0), list.subList(1, list.size()).toArray(new Object[list.size() - 1])));
                } else {
                    final List<Inst> insts = new ArrayList<>();
                    for (final Inst entry : to.<List<Inst>>get()) {
                        insts.add(via(from, entry));
                    }
                    obj = (O) TInst.of(insts);
                }
            } else
                throw new RuntimeException("This state should not have been reached: " + from + "=>" + to);
        }
        if (to.isLabeled()) {
            final O o = (O) from.model().apply(to);
            obj = TObj.none().equals(o) ? obj : obj.test(o) ? o : (O) TObj.none();
        }
        return to.isReference() ?
                FastProcessor.<O>process(from.mapTo(obj.access(via(from, to.access())))).next() :
                obj;
    }


    public static Obj model(final Obj obj) {
        return obj.model(ModelHelper.fromObj(obj.model(), obj));
    }

    private static Model fromObj(final Model model, final Obj obj) {
        Model update = model;
        if (obj.isRec()) {
            for (final Map.Entry<Obj, Obj> entry : obj.<Map<Obj, Obj>>get().entrySet()) {
                update = fromObj(update, entry.getKey());
                update = fromObj(update, entry.getValue());
            }
        } else if (obj.isLst()) {
            for (final Obj v : obj.<List<Obj>>get()) {
                update = fromObj(update, v);
            }
        }
        if (obj.isLabeled())
            update = update.write(obj);
        return update;
    }

    public static <O extends Obj> O match(final O obj) {
        if (!obj.isLabeled())
            return obj;
        else if (obj.isReference()) {
            final O clone = obj.model(obj.model().write(obj));
            return AsInst.<O>create(clone.access(null)).attach(clone);
        } else {
            final O storedObj = (O) obj.model().apply(obj);
            if (TObj.none().equals(storedObj))
                return obj.model(obj.model().write(obj)); // if the variable is unbound, bind it to the current obj
            else
                return obj.test(storedObj) ? obj : obj.kill(); // test if the current obj is subsumed by the historic obj (if not, drop the obj's quantity to [zero])
        }
    }
}
