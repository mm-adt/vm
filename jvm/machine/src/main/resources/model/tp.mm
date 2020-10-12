tp:(
'import' -> (mm -> ()),
'type'   -> (graph    -> (graph:('V'->vertex{*},'E'->edge{*})),
             vertex   -> (vertex:('id'->_,'label'->str,'properties'->property{*},'outE'->edge{*},'inE'->edge{*})),
             edge     -> (edge:('id'->_,'label'->str,'properties'->property{*},'outV'->vertex,'inV'->vertex)),
             property -> (property:([is,![=='id'|=='label']]->_)),
             store    -> (store<=kv{*}),
             kv       -> (kv:('k'->_,'v'->_))))