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
obj
 -> (graph   -> (vertex{*}
  -> ([is,[get,'id'][eq,int~x]] -> [map,vertex['id':x]]))
  -> ([count]                   -> [eval,'vertex-count']))
 -> (element -> (['id':int~y,'label':str]
  -> ([drop,'id'|'label'] -> [error])))
 -> (vertex  -> (element['outE':edge{*},'inE':edge{*}]
  -> ([get,'outE'] -> (edge{*}
   -> [is,[get,'label'][eq,str~x]] -> [eval,'vertex-idx',y,x]
   -> [count]                      -> [eval,'vertex-idx-count',y,x]]))))
 -> (edge    -> element['outV':vertex,'inV':vertex])