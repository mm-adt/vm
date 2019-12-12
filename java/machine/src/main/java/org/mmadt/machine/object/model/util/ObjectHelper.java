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

import org.mmadt.language.compiler.Tokens;
import org.mmadt.machine.object.impl.TObj;
import org.mmadt.machine.object.impl.atomic.TBool;
import org.mmadt.machine.object.impl.atomic.TInt;
import org.mmadt.machine.object.impl.atomic.TReal;
import org.mmadt.machine.object.impl.atomic.TStr;
import org.mmadt.machine.object.impl.composite.TInst;
import org.mmadt.machine.object.impl.composite.TLst;
import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.impl.composite.inst.initial.StartInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.Sym;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.machine.object.model.composite.Lst;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.composite.util.PList;
import org.mmadt.machine.object.model.composite.util.PMap;
import org.mmadt.machine.object.model.ext.algebra.WithAnd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class ObjectHelper {

    private static final Map<Class, Obj> SYMBOL_MAP = new HashMap<>() {{
        put(TObj.class, TObj.single());
        put(TBool.class, TBool.of());
        put(TInt.class, TInt.of());
        put(TReal.class, TReal.of());
        put(TStr.class, TStr.of());
        put(TLst.class, TLst.some());
        put(TRec.class, TRec.some());
        put(TInst.class, TInst.some());
    }};

    private ObjectHelper() {
        // static helper class
    }

    public static <O extends Obj> O create(final Obj obj, final Object object) {
        return object instanceof Inst ?
                obj instanceof Inst ?
                        (O) object :
                        obj.type().access((Inst) object) :
                object instanceof Sym ?
                        (O) Optional.<O>ofNullable(obj.state().read(((Sym) object))).orElse((O)obj).label(((Sym) object).label()) :
                        (O) ObjectHelper.from(object);
    }

    public static <O extends Obj> O make(final Function<Object, O> constructor, final Object... objects) {
        if (0 == objects.length)
            return constructor.apply(null);
        else if (1 == objects.length)
            return objects[0] instanceof Inst ? constructor.apply(objects[0]) : objects[0] instanceof Obj ? (O) objects[0] : constructor.apply(objects[0]);
        else
            return constructor.apply(null).q(objects.length).access(StartInst.create(objects));
    }

    public static Object andValues(final TObj object1, final TObj object2) {
        if (object1 instanceof Rec && object2 instanceof Rec)
            return ObjectHelper.mergeMaps(object1.get(), object2.get());
        else if (object1 instanceof Lst && object2 instanceof Lst)
            return ObjectHelper.mergeLists(object1.get(), object2.get());
        else if (object1.constant() && object2.constant()) {
            if (!object1.get().equals(object2.get()))
                throw new RuntimeException("AND'ing non-equal values: " + object1.get() + "::" + object2.get());
            return object1.get();
        } else {
            if (null == object1.get())
                return object2.get();
            else
                return object1.get();
        }
    }

    public static String mergeLabels(final Obj object1, final Obj object2) {
        if (null != object1.label() && null != object2.label() && !object1.label().equals(object2.label()))
            throw new RuntimeException("The two objects have different variables: " + object1 + ":::" + object2);
        if (null != object1.label())
            return object1.label();
        else
            return object2.label();
    }

    private static <K extends Obj, V extends Obj> Map<K, V> mergeMaps(final Map<K, V> map1, final Map<K, V> map2) {
        if (null == map1)
            return map2;
        else if (null == map2)
            return map1;
        else {
            final Map<K, V> map = new PMap<>(map1);
            for (final Map.Entry<K, V> entry : map2.entrySet()) {
                final V value = map.get(entry.getKey());
                map.put(entry.getKey(), null == value ? entry.getValue() : ((WithAnd<V>) value).and(entry.getValue()));
            }
            return map;
        }
    }

    private static <V extends Obj> List<V> mergeLists(final List<V> list1, final List<V> list2) {
        if (null == list1)
            return list2;
        else if (null == list2)
            return list1;
        else {
            final List<V> list = new PList<>(list1);
            for (int i = 0; i < list2.size(); i++) {
                final V value = list2.get(i);
                if (list.size() < i)
                    list.add(value);
                else
                    list.set(i, ((WithAnd<V>) list.get(i)).and(value));
            }
            return list;
        }
    }

    public static Obj root(final Obj object1, final Obj object2) {
        if (object1 instanceof Rec)
            return object1.symbol(Tokens.REC);   // necessary for mutability
        else if (object1 instanceof Lst)
            return object1.symbol(Tokens.LST); // necessary for mutability
        else if (object1.getClass().equals(object2.getClass()))
            return SYMBOL_MAP.get(object1.getClass());
        else if (object1.named() && null == object1.get())
            return SYMBOL_MAP.get(object2.getClass());
        else if (object2.named() && null == object2.get())
            return SYMBOL_MAP.get(object1.getClass());
        else
            return TObj.single();
    }

    public static Obj from(final Object object) {
        if (object instanceof Obj)
            return (Obj) object;
        else if (object instanceof Boolean)
            return TBool.of(((Boolean) object));
        else if (object instanceof Integer)
            return TInt.of(((Integer) object));
        else if (object instanceof Float)
            return TReal.of(((Float) object));
        else if (object instanceof Double)
            return TReal.of(((Double) object).floatValue());
        else if (object instanceof String)
            return TStr.of((String) object);
        else if (object instanceof List)
            return TLst.of(new PList<>((List<Obj>) object));
        else if (object instanceof Map)
            return TRec.of(new PMap<>((Map<Obj, Obj>) object));
        else
            throw new IllegalStateException("Unknown type: " + object.getClass());
    }
}
