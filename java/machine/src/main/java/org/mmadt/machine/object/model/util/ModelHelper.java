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
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Model;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.composite.util.PMap;

import java.util.List;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ModelHelper {

    public static <O extends Obj> O fromModel(final Model model, final Obj obj) {
        if (obj.isSym()) {
            return (O) model.readOrGet(obj, TObj.none()).copy(obj);
        } else if (obj instanceof Rec) {
            final Map<Obj, Obj> map = new PMap<>();
            for (final Map.Entry<Obj, Obj> entry : obj.<Map<Obj, Obj>>get().entrySet()) {
                map.put(fromModel(model, entry.getKey()), fromModel(model, entry.getValue()));
            }
            return (O) TRec.of(map).copy(obj);
        } else if (obj instanceof Lst) {
            final List<Obj> list = new PList<>();
            for (final Obj entry : obj.<List<Obj>>get()) {
                list.add(fromModel(model, entry));
            }
            return (O) TLst.of(list).copy(obj);
        } else
            return (O) obj.copy(obj);
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
}
