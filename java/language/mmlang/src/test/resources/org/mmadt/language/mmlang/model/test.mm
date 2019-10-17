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
[model,ex=>mm,
 // [load,alg=>mm]
 [define,ring,obj
  -> [plus,int~zero]  =>
  -> [mult,int~one]   => ]
 [define,int,ring&int
  > int~zero         => 0
  > int~one          => 1
  -> [plus,2] => [plus,1]~a[plus,1][plus,3]~b[plus,-3]
  -> [plus,3] => [plus,5]~c[plus,-2]]
 [define,db,int{*} <= [start,1,2,3,4,5,6{3}]]
 [define,play,real{*}
   -> [mult,2] => [db,[[plus,1];[plus,1]]]]
   // TODO: -> [mult,2] => real{2,3} <=[=rdb,[plus,1][plus,1]]]
 ]