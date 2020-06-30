[define,edge:('outV'->int,'label'->str,'inV'->int)]
[define,vertex<=mmkv:('k'->int,'v'->person:('name'->str,'age'->int))<x>-<
  ('id'      -> .k,
   'name'    -> .v.name,
   'outE'    -> [=mmkv,''].v[is,[and,[a,edge],.outV==<.x>.k]]
  )]
[define,vertex<=int<x>[=mmkv,''][is,.k==x]]