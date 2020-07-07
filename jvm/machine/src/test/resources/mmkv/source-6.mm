[define,graph<=obj-<('V'->[=mmkv,'/Users/marko/software/mmadt/vm/jvm/machine/target/test-classes/mmkv/mmkv-6.mm'][is,.k.0=='vertex'],
                     'E'->[=mmkv,'/Users/marko/software/mmadt/vm/jvm/machine/target/test-classes/mmkv/mmkv-6.mm'][is,.k.0=='edge']),
        vertex<=kv:('k'->(is=='vertex',obj),'v'->(str->obj))<x>-<
                   ('id'    -> .k.1,
                    'label' -> [.v.label|.k.0],
                    'inE'  -> graph.E[is,.v.link.1==x.k.1],
                    'outE'  -> graph.E[is,.v.link.0==x.k.1]),
          edge<=kv:('k'->(is=='edge',obj),'v'->('link'->(obj,obj),str->obj{*}))<x>-<
                   ('id'    -> .k.1,
                    'label' -> [.v.label|.k.0],
                    'outV'  -> graph.V[is,.k.1==x.v.link.0],
                    'inV'   -> graph.V[is,.k.1==x.v.link.1])]