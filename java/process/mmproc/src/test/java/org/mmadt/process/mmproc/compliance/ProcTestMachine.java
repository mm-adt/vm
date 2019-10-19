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

package org.mmadt.process.mmproc.compliance;

import org.mmadt.machine.Machine;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Inst;
import org.mmadt.process.compliance.TestMachine;
import org.mmadt.process.mmproc.ProcProcessor;
import org.mmadt.processor.compiler.IR;
import org.mmadt.util.EmptyIterator;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface ProcTestMachine extends TestMachine {
    @Override
    public default Machine machine() {
        return new Machine() {
            @Override
            public <E extends Obj> Iterator<E> submit(final Inst bytecode) {
                return new ProcProcessor(Map.of()).<Obj, E>mint(new IR<>(bytecode)).iterator(EmptyIterator.instance());
            }

            @Override
            public void close() {

            }
        };
    }
}
