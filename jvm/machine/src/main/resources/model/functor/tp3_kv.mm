// A Functor from Key/Value model-ADT to TP3 model-ADT
[define,vertex<=kv:('k'->(is=='vertex',obj),'v'->(str->obj))-<
                   ('id'    -> .k.1,
                    'label' -> [.v.label|.k.0]),
          edge<=kv:('k'->(is=='edge',obj),'v'->('link'->(obj,obj),str->obj))-<
                   ('id'    -> .k.1,
                    'label' -> [.v.label|.k.0],
                    'outV'  -> .v.link.0-<('vertex',_),
                    'inV'   -> .v.link.1-<('vertex',_))]