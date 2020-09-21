pg_3:('type' -> (
  graph  -> (graph<=edge{*}),
  vertex -> (vertex<=('id'->int,'label'->str),
             vertex<=int-<('id'->_,'label'->'vertex')),
  edge   -> (edge<=('outV'->vertex,'label'->str,'inV'->vertex),
             edge<=(vertex;vertex)-<('outV'->.0,'label'->'edge','inV'->.1)))) <= mm
