[define,graph<=kvstore-<('V'->.0[is,.k.0=='vertex'],
                         'E'->.0[is,.k.0=='edge']),
        vertex<=kv:('k'->(is=='vertex',obj),'v'->(str->obj))<x>-<
                   ('id'         -> .k.1,
                    'label'      -> [.v.label|.k.0],
                    'properties' -> .v,
                    'inE'        -> g.E[is,.v.link.1==x.k.1],
                    'outE'       -> g.E[is,.v.link.0==x.k.1]),
          edge<=kv:('k'->(is=='edge',obj),'v'->('link'->(obj,obj),str->obj{*}))<x>-<
                   ('id'         -> .k.1,
                    'label'      -> [.v.label|.k.0],
                    'outV'       -> g.V[is,.k.1==x.v.link.0],
                    'inV'        -> g.V[is,.k.1==x.v.link.1])]
[define,kvstore<=-<(db)]
[define,db<=[=mmkv,file]]