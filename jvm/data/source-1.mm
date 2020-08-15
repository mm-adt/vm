[define,nat<=int[is>0]]
[define,person<=('name'->str,'age'->nat)]
[define,vertex<=person:('name'->str,'age'->nat)-<(
                        'id'    -> .age,
                        'label' -> <x>.name[plus,<.x>.age[as,str]],
                        'outE'  -> {0})]
[define,vertex<=int-<('id'->_)]
[define,vertex:('id'->nat,'label'->str)]
[define,vertex:('id'->nat)]