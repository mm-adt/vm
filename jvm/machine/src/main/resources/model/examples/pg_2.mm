pg_2:('import' -> (mm -> ()),
'type'   -> (
  vertex -> (vertex:('id'->int),                                       //<1>
             vertex<=int-<('id'->_)),                                  //<2>
  edge   -> (edge:('outV'->vertex,'inV'->vertex),                      //<3>
             edge<=(vertex;vertex)-<('outV'->.0,'inV'->.1))))          //<4>
