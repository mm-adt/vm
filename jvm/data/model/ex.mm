ex:('type' -> (nat    -> (nat<=int[is>0]),
               large  -> (large<=int[is>99],
                          large<=nat[is>99]),
               user   -> (user:('id'->int,'login'->str)<=person[put,'id',.age][put,'login',.name]),
               person -> (person:('name'->str,'age'->nat)<=('name'->str,'age'->1),
                          person:('name'->str,'age'->nat)<=('name'->str,'age'->int),
                          person:('name'->str,'age'->nat)<=('name'->str)[put,'age',1],
                          person:('name'->str,'age'->nat)<=str-<('name'->_,'age'->1))))<=mm