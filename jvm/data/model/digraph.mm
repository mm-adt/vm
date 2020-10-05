digraph:(
'type' -> (
  //int    -> (int<=vertex.id),
  vertex -> (vertex:('id'->nat,'attrs'->attr{*}),
             vertex<=nat-<('id'->nat),
             vertex<=(str;attr)=(int<=str;-<('key'->.key+'x','value'->.value+'xx')),
             vertex<=(nat;attr)-<('id'->.0,'attrs'->.1),
             vertex<=int[is<0]-<([neg];('no';'data'))),
  attr   -> (attr:('key'->_,'value'->_),
             attr<=(str;[id])-<('key'->.0,'value'->.1)),
  edge   -> (edge:('outV'->vertex,'inV'->vertex),
             edge<=(vertex;vertex)-<('outV'->.0,'inV'->.1))
)) <= num