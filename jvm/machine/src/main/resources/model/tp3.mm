tp:('type'-> (graph    -> (graph:('V'->vertex{*},'E'->edge{*})),
              vertex   -> (vertex:('id'->obj,'label'->str,'properties'->property{*},'outE'->edge{*},'inE'->edge{*})),
              edge     -> (edge:('id'->obj,'label'->str,'properties'->property{*},'outV'->vertex,'inV'->vertex)),
              property -> (property:([is,![=='id'|=='label']]->obj)),
              store    -> (store<=kv{*}),
              kv       -> (kv:('k'->obj,'v'->obj))))