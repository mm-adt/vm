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

package org.mmadt.object.model.type;

import org.mmadt.object.impl.TObj;
import org.mmadt.object.model.Obj;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Bindings {

    private final Map<String, Obj> variables;
    private boolean recording = false;
    private Set<String> recordings = new HashSet<>();

    public Bindings(final Bindings bindings) {
        this.variables = new HashMap<>(bindings.variables);
    }

    public Bindings() {
        this.variables = new HashMap<>();
    }

    public boolean has(final String variable) {
        return this.variables.containsKey(variable);
    }

    public <A extends Obj> A get(final String variable) {
        return (A) this.variables.get(variable);
    }

    public void put(final String variable, final Obj object) {
        if (null == variable || TObj.none().equals(object))
            return;
        if (this.recording) this.recordings.add(variable);
        this.variables.put(variable, object);
    }

    public int size() {
        return this.variables.size();
    }

    public void clear() {
        this.recording = false;
        this.variables.clear();
    }

    public void start() {
        this.recording = true;
    }

    public void commit() {
        this.recording = false;
        this.recordings.clear();
    }

    public void rollback() {
        this.recording = false;
        for (final String undo : this.recordings) {
            this.variables.remove(undo);
        }
        this.recordings.clear();
    }

    @Override
    public String toString() {
        return this.variables.toString();
    }
}
