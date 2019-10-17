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
[model,pg=>mm,
 [define,obj,obj
   -> [id]                                             =>                                                                                                                  // x().identity().y() is x().y()     [IDENTITY REMOVAL STRATEGY]
   -> [filter,[is,obj{+}~a]]                           => [is,obj{+}~a]                                                                                                    // filter(a_filter) is a_filter      [INLINE FILTER STRATEGY]
   -> [order,[lt,obj~a]]                               => [ref,obj{*}
     -> [dedup,[get,obj~b]]                            => [dedup,[get,obj~b]][order,[lt,obj~a]]]                                                                           // filter then order                 [FILTER RANK STRATEGY]
   -> [dedup,[get,obj~a]]                              => [ref,obj{*}
     -> [is,obj{+}~b]                                  => [is,obj{+}~b][dedup,[get,obj~a]]]                                                                                // filter then dedup                 [FILTER RANK STRATEGY]
   -> [count]                                          => [ref,int
     -> [is,[gt,int~b]]                                => [range,0,int~b][count][is,[gt,int~b]]]]                                                                          // limit(3).count().is(gt(3))        [COUNT STRATEGY]
 [define,key,str&neq('label')&neq('id')]
 [define,value,str|int|real|bool]
 [define,element,['id':int,'label':str]
   -> [eq,element&['id':int~a]]                        => [get,'id'][eq,int~a]
   -> [drop,'id'|'label']                              => [error]                                                                                                          // N/A                               [SCHEMA]
   -> [put,'id'|'label',str|int]                       => [error]]                                                                                                         // N/A                               [SCHEMA]
 [define,vertex,element&['outE':edge{*},'inE':edge{*}]
   -> [eq,vertex&['id':int~x]]                         => [get,'id'][eq,int~x]
   -> [get,'outE']                                     => [ref,edge{*}
     -> [count]                                        => [ref,int                                 <= [get,'outE'][count]][sum]                                            // v.outE().count()                 [AGGREGATE]
     -> [is,[get,'inV'][get,'id'][eq,int~a]]           => [ref,edge&['inV':vertex&['id':int~a]]{*} <= [get,'outE'][is,[get,'inV'][get,'id'][eq,int~a]]]                    // v.outE().where(inV().hasId()))   [DENORM]
     -> [get,'inV']                                    => [ref,vertex{*}
       -> [count]                                      => [ref,int                                 <= [get,'outE'][count]][sum]]                                           // v.outE().count()                 [AGGREGATE]
     -> [is,[get,'label'][eq,str~a]]                   => [ref,edge&['label':str~a]{*}             <= [get,'outE'][is,[get,'label'][eq,str~a]]                             // v.outE(knows)                    [INDEX]
       -> [count]                                      => [ref,int                                 <= [get,'outE'][is,[get,'label'][eq,str~a]][count]][sum]]               // v.outE(knows).count()            [INDEX+AGGREGATE]
     -> [get,'inV']                                    => [ref,vertex{*}                           <= [get,'outE'][is,[get,'label'][eq,str~a]][get,'inV']                  // v.out(knows)                     [INDEX]
       -> [count]                                      => [ref,int                                 <= [get,'outE'][is,[get,'label'][eq,str~a]][count]][sum]                // v.out(knows).count()             [AGGREGATE]
       -> [get,'id']                                   => [ref,int{*}                              <= [get,'outE'][is,[get,'label'][eq,str~a]][get,'inV'][get,'id']]       // v.out(knows).id()                [DENORM]
       -> [get,'label']                                => [ref,str{*}                              <= [get,'outE'][is,[get,'label'][eq,str~a]][get,'inV'][get,'label']]]]] // v.out(knows).label()             [DENORM]
 [define,edge,element&['outV':vertex,'inV':vertex]
   -> [drop,'outV'|'inV']                              => [error]                                                                                                          // N/A                              [SCHEMA]
   -> [put,'outV'|'inV',vertex]                        => [error]                                                                                                          // N/A                              [SCHEMA]
   -> [get,'inV']                                      => [ref,vertex
     -> [get,'id']                                     => [ref,int                                 <= [get,'inV'][get,'id']]                                               // e.inV().id()                     [DENORM]
     -> [get,'label']                                  => [ref,str                                 <= [get,'inV'][get,'label']]]]                                          // e.inV().label()                  [DENORM]
 [define,db,vertex{*}
   -> [count]                                          => [ref,int                                 <= [db][count]]                                                         // g.V().count()                    [AGGREGATE]
   -> [dedup,[get,'id']]                               =>                                                                                                                  // g.V().dedup()                    [SCHEMA]
   -> [order,[gt,[get,'label']]]                       =>                                                                                                                  // g.V().order().by(label,ASC)      [SCHEMA]
   -> [is,[get,'id'][eq,int~a]]                        => [ref,vertex&['id':int~a]{?}              <= [db][is,[get,'id'][eq,int~a]]]                                       // g.V(1)                           [INDEX]
   -> [is,[get,'label'][eq,str~b]]                     => [ref,vertex&['label':str~b]{*}           <= [db][is,[get,'label'][eq,str~b]]                                     // g.V().hasLabel(person)           [INDEX]
     -> [dedup,[get,'id']]                             =>                                                                                                                  // g.V().hasLabel(person).dedup()   [INDEX+SCHEMA]
     -> [count]                                        => [ref,int                                 <= [db][is,[get,'label'][eq,str~b]][count]]]                            // g.V().hasLabel(person).count()   [INDEX+AGGREGATE]
   -> [get,('inE'|'outE')~c]                           => [ref,edge{*}
     -> [dedup,[get,'id']]                             =>                                                                                                                  // g.V().outE().dedup()             [SCHEMA]
     -> [count]                                        => [ref,int                                 <= [db][get,str~c][count]]                                              // g.V().outE().count()             [AGGREGATE]
     -> [get,('outV'|'inV')~d]                         => [ref,vertex{*}
       -> [dedup,[get,'id']]                           =>                                                                                                                  // g.V().out().dedup()              [SCHEMA]
       -> [count]                                      => [ref,int                                 <= [db][get,str~c][get,str~d][count]]]]]]                               // g.V().out().count()              [AGGREGATE]