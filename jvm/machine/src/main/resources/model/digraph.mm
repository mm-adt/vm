digraph:(
'type' -> (
  vertex -> (vertex:('id'->nat,'attrs'->attr{*}),
             vertex<=nat-<('id'->nat),
             vertex<=(nat;attr)=(_)-<('id'->.0,'attrs'->.1),
             vertex<=int[is<0]-<([neg];('no';'data'))),
  attr   -> (attr:('key'->_,'value'->_),
             attr<=(str;_)-<('key'->.0,'value'->.1)),
  edge   -> (edge:(outV->vertex,inV->vertex))
)) <= numbers