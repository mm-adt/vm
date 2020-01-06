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

package org.mmadt.storage.mmstor;

import org.mmadt.machine.object.impl.composite.TRec;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.storage.Storage;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public final class Stor<A extends Obj> implements Storage<A> {

    private final String FILE_NAME = "/tmp/mmstor.obj";
    static final String MMSTOR = "mmstor";
    private Map<Object, Object> MAP;
    private A root;

    public Stor() {
        try {
            MAP = (Map<Object, Object>) new ObjectInputStream(new FileInputStream(FILE_NAME)).readObject();
        } catch (final Exception e) {
            MAP = new LinkedHashMap<>();
        }
        root = (A) TRec.of(MAP).label(MMSTOR);
    }

    @Override
    public boolean alive() {
        return true;
    }

    @Override
    public A open() {
        return this.root;
    }

    @Override
    public void close() {
        try {
            new ObjectOutputStream(new FileOutputStream(FILE_NAME)).writeObject(MAP);
        } catch (Exception e) {
            throw new RuntimeException("Could not save to file: " + e.getMessage());
        }
    }

    @Override
    public String name() {
        return MMSTOR;
    }
}
