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
[model,ex=>mm,  // this is a model that embeds 'ex' (example) into the mm model-ADT (i.e. base mm-ADT)
 [define,v,(bool~x|int{3}~y|str{?}~z){4}~w
  -> [mult,y]                      => [mult,[1;[2;[x;[4;true;1.2;['a':'b'];[;]]]]]]
  -> [count]                       => [is,bool{1}   <= [eq,w]
                                          -> [eq,x] => [eq,false]]] // bytecode can have a specified range() which is the output object's type cast (thus, "bytecode" can also just be types)
 [define,animal,['alive':[is,[count][eq,4]]]] // variable framing makes it so the compiler thinks w is a type (which is a bad compilation, but correct state behavior)
 [define,person,animal['name':str~x,'age':int&gt(0)]           <= [db]*[get,'people']{16}*[is,[get,'name'][eq,x]]]*([db][count]&[sum]&[get,'name'][sum]) // {16} is the coefficient of the preceding instruction (see [define,q])
 [define,people,person{*}                                      <= [db][get,'people']*(([get,'organizations']{2}[count]{4})-[sum]{45})*([count][sum])
  -> [is,[get,'name'][eq,str~y]]                               => [<=rdb,person['name':y]{?} <= [db][get,'name']{0}
  --> [get,'name'] => [map,y]
  --> [count]      => [map,1]]]
  [define,location,['phone':int,
                    'age':[is,[gt,0]&[lt,150]],   // the difference between a type and an instance are whether there are free variables/unbound structures (i.e. predicates).
                    'exists':[is,[a,bool]&[id]],
                    'address':['street':[int;str],'city':str,42:[:]]]]

/* ALGEBRAIC DATATYPES */
// we will most likely have a standard library
// where the standard algebraic structures are defined (in mm-ADT)
 [define,complex,ring[real~x;real~y]
   > 0                             => complex[0.0;0.0] // instances should be replaced throughout the bytecode via the symbol table
   > 1                             => complex[1.0;0.0]
  -> [mult,complex[1.0;0.0]]       =>                      // no-op as multiplying by 1 does nothing. this rewrite isn't needed as its inherited from ring (perhaps compilation warning?)
  -> [plus,complex[real~a;real~b]] => [map,complex[[map,x][plus,b];[map,y][plus,b]]]
  -> [mult,complex[real~a;real~b]] => [map,complex&[y;b]]
  -> [mult,22]                     => ([count]|[sum])&[get,'name']*[is,[map,true]|[map,false]]] // just playing with + * & and | which do branching, composition, and'ing, and or'ing in stream theory
 [define,ring,int~r
   > 0          => [error]
   > 1          => [error]
  -> [mult,0]   => [map,0]  // 0 and 1 refer to the static member of the type
  -> [mult,1]   =>
  -> [mult,-1]  => [map,r]  // try with -r
  -> [plus,0]   => ]
 [define,group,obj~m
   > 0          => [error]  // group has no internal structure and thus, serves as an interface -- all [error] mappings must be implemented by the extending type
   > 1          => [error]
   > 'test'     => [map,0|1 <= [map,'test'][coalesce,[is,[eq,'mult']][map,1],[map,0]]]  // solve the infinite recursion so types can be on the rhs of =>
   > 'marko'    => 'mult'|'plus' // the instruction to use for the group operation is specified by the extending type
  -> [get,m]  =>
  -> [get,'t4'] => [map,"""test_a_very_long_adsf_long"""]]
 /*[define,tree,(obj~a|[tree~b;tree~c])~T                           // capital variables (T and U) allow access to the instance variables within the larger structure via dot notation (idea -- T.b.b.b.b.b ? :))
  -> [eq,tree~U]    => [repeat,'until':[eq,T.a]                   // each thread breaks out when its reached a leaf
                               'do'   :[branch,[map,T.a][eq,U.a], // check equality on each branch
                                               [map,T.b][eq,U.b],
                                               [map,T.c][eq,U.c]], // [map,T.a][eq,U.a]+[map,T.b][eq,U.b]+[map,T.c][eq,U.c] is equivalent to [branch,...] (+ is branch and * is compose)
                               'emit' :[_]][fold,true,[and]]]*/      // emit the bool checks and reduce on true




/* db and q are required as they form the structural component of stream ring theory (the data and coefficient product) */
 [define,db,['people':people]]
 [define,q,int]]