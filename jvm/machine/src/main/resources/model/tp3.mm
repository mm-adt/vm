[define,graph:('V'->vertex{*},'E'->edge{*}),
  vertex:('id'->obj,'label'->str,'properties'->property{*},'outE'->edge{*},'inE'->edge{*}),
  edge:('id'->obj,'label'->str,'properties'->property{*},'outV'->vertex,'inV'->vertex),
  property:([is,![=='id'|=='label']]->obj)]