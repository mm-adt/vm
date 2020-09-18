pg_2:('type' -> (                                                       //<1>
  graph  -> (graph<=edge{*}),                                           //<2>
  vertex -> (vertex<=('id'->int),                                       //<3>
             vertex<=int-<('id'->_)),                                   //<4>
  edge   -> (edge<=('outV'->vertex,'inV'->vertex),                      //<5>
             edge<=(vertex;vertex)-<('outV'->.0,'inV'->.1)))) <= mm     //<6>
