// Apache TinkerPop3 Graph model-ADT
// Directed, binary, attributed multi-graph commonly known as a property graph
// TODO: property:(str[is,!=='id'&&!=='label']->obj)
[define,graph<=vertex{*},
  vertex:('id'->obj,'label'->str,'properties'->property{*},'outE'->edge{*},'inE'->edge{*}),
  edge:('id'->obj,'label'->str,'properties'->property{*},'outV'->vertex,'inV'->vertex),
  property:(str[is,[and,[eq,'id'][not],[eq,'label'][not]]]->obj)]