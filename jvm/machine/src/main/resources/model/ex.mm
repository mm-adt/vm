ex:(
'import' -> (mm -> ()),
'type'   -> (nat    -> (nat<=int[is>0]),
               large  -> (large<=int[is>99],
                          large<=nat[is>99]),
               names  -> (names:(str,str)),
               user   -> (user:('id'->int,'login'->str),
                          user<=person[put,'id',.age][put,'login',.name]),
               person -> (person:('name'->str,'age'->nat),
                          person<=('name'->str,'age'->int),
                          person<=('name'->str)[put,'age',1],
                          person<=names-<('name'-><y>.0+<.y>.1,'age'->1),
                          person<=str-<('name'->_,'age'->1))))