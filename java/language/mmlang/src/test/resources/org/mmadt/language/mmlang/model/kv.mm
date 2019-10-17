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
[model,kv=>mm,
 [define,k,str]
 [define,v,bool|int|str|real|list]
 [define,kv,[k;v]
  -> [put,0,k]                     => [error]                                                     // [SCHEMA]
  -> [drop,0|1]                    => [error]]                                                    // [SCHEMA]
 [define,db,kv{*}
  -> [put,k~a,v~b]                 => [coalesce,[is,[get,0][eq,k~a]][put,1,v~b],[put,k~a,v~b]]
  -> [order,[gt,[get,0]]]          =>                                                             // [SORT ORDER]
  -> [dedup,[get,0]]               =>                                                             // [UNIQUE]
  -> [count]                       => [ref,int  <= [db][count]                                    // [AGGREGATE]
    -> [sum]                       => ]
  -> [is,[get,0][eq,(str|real)~a]] => [ref,v{?} <= [db][is,[get,0][eq,(str|real)~a]]]]]           // [INDEX]