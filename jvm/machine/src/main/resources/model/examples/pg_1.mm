pg_1:('type' -> (                                             //<1>
  vertex -> (vertex<=('id'->int)),                            //<2>
  edge   -> ( edge<=('outV'->vertex,'inV'->vertex)))) <= mm   //<3>
