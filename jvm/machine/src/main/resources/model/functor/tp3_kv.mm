// A Functor from Key/Value model-ADT to TP3 model-ADT
[define,vertex<=kv:('k' -> (['vertex'|'edge'],obj),'v'     -> (str->obj))-<
                   ('id'-> .k.1,                   'label' -> [.v.label|.k.0])]