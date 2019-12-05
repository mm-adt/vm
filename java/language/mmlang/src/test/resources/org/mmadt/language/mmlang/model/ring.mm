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

/*
 * Rewrite rules for common abstract algebraic structures.
 *   @see:    https://en.wikipedia.org/wiki/Magma_%28algebra%29
 *   @author: Marko A. Rodriguez (http://markorodriguez.com)
 * m_x: an x-algebra via [mult] (*)
 * p_x: an x-algebra via [plus] (+)
 * mp_x: an x-algebra via [mult] and [plus] (*,+)
 *
 */
[=m_monoid,
 | one          -> [one]
 | [mult,one]   -> [id]]

[=p_group,
 | zero            -> [zero]
 | [plus,zero]     -> [id]
 | [minus,zero]    -> [id]
 | x + y           -> y + x
 | --x             -> x
 | [plus,[neg]]    -> zero]

[=mp_ring,
 | m_monoid
 | p_group
 | -x * -y           ->  x *  y
 | -(x + y)          -> -x + -y
 | x * (y + z)       -> (x * y) + (x * z)
 | (x + y) * (y + z) -> (x * y) + (x * z) +
                        (y * y) + (y * z)]

/*
 * Provide a specific carrier set and get back a custom [=model] instruction.
 * @param model the name of the algebra (e.g. complex, polar, etc.)
 * @param zero the zero element of the algebra (no specification required)
 * @param one the one element of the algebra (no specification required)
 * @return a [=model] with the specified abstract algebra axioms.
 * *
 */
[=generator,str~model, obj~zero, obj~one]
  [start, m_monoid, p_group, mp_ring]
  [branch,
    one  -> ~one,
    zero -> ~zero]
  [reduce,[;],[plus,lst]]
  [put,0,~model]
  [as,inst]