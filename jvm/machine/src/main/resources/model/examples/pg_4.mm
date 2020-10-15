pg_4:('import' -> (mm -> (), num -> ()),
'type' -> (
  graph  -> (graph<=edge{*}),
  vertex -> (vertex:('id'->nat,'label'->str,'props'->props{*}),
             vertex<=nat-<('id'->nat,'label'->'vertex'),
             vertex<=(nat;str;str{?};_{?})-<('id'->.0,'label'->.1)
                       [ 1 -> [put,'props',('name'->'marko')]
                       | _   -> _]),
  edge   -> (edge:('outV'->vertex,'label'->str,'inV'->vertex),
             edge<=(vertex;vertex)-<('outV'->.0,'label'->'edge','inV'->.1)),
  props  -> (props:(str[is=='label']->[bool|int|real|str]))))
