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
[model,social=>ex,
  [define,person,['address':address{?},
                   'alive':bool,
                   'name':str,
                   'age':[is,[and,[a,int],[lt,150]]],
                   'spouse':person{?}]]
  [define,address,['city':str,'street':str,'state':str]]
  [define,singles,person&['address':address,'spouse':[is,[a,person{0}]]]{50}
    -> [dedup,[get,'name']]                                => [id]
    -> [get,'address']                                     => [ref,address{50}
     -> [order,[get,'state']]                              => [id]
     -> [dedup]                                            => [id]
    -> [dedup,[get,'street']]                              => [id]
    -> [get,'street']                                      => [ref,str{50}
     -> [dedup] => ]]]
  [define,db,['persons':person{,200}
    -> [is,[get,'name'][eq,str~a]]                         => [ref,person{,200}                            <= [db][get,'persons'][is,[get,'name'][eq,str~a]]
      -> [is,[get,'age'][eq,int~c]]                        => [ref,person{?}                               <= [db][get,'persons'][is,[get,'name'][eq,str~a]][is,[get,'age'][eq,int~c]]]]
    -> [is,[get,'age'][eq,int~a]]                          => [ref,person&['age':int~a]{,200}              <= [db][get,'persons'][is,[get,'age'][eq,int~a]]
      -> [is,[get,'name'][eq,str~c]]                       => [ref,person&['age':int~a,'name':str~c]{?}    <= [db][get,'persons'][is,[get,'name'][eq,str~c]][is,[get,'age'][eq,int~a]]]]
    -> [is,[get,'spouse'][count][eq,0]]                    => [ref,singles                                 <= [db][get,'persons'][is,[get,'spouse'][count][eq,0]]]
    -> [is,[get,'alive']]                                  => [ref,person&['address':address{0},
                                                                           'age'    :int <= [is,[and,[a,int],[gt,75]]]]{*}]
    -> [order,[get,'age']]                                 => [id]
    -> [get,'address']                                     => [ref,address{,200}
          -> [order,[gt,[get,'state'|'city']]]             => [id]]]]]