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

package org.mmadt.machine.object.model.type.algebra;

import org.mmadt.machine.object.impl.___;
import org.mmadt.machine.object.impl.composite.inst.filter.IsInst;
import org.mmadt.machine.object.impl.composite.inst.map.AInst;
import org.mmadt.machine.object.impl.composite.inst.map.OrInst;
import org.mmadt.machine.object.model.Obj;
import org.mmadt.machine.object.model.composite.Rec;
import org.mmadt.machine.object.model.util.ObjectHelper;

/**
 * An {@link org.mmadt.machine.object.model.Obj} that supports |.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface WithOr<A extends Obj> {

    public default A or(final A obj) {
        return this instanceof Rec ? (A) ((Obj) this).set(IsInst.create(OrInst.create(AInst.create(this), AInst.create(obj)))) : ObjectHelper.root((Obj) this, obj).set(___.is(___.or(___.a(this), ___.a(obj))));
        // (A)((Obj)this).label(null).set(IsInst.create(OrInst.create(this, obj))).q(Q.Tag.one);
    }
}
