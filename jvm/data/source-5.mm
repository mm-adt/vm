[model,mm]
[define,
  edge:('outV'->int,'label'->str,'inV'->int),
  vertex:('id'->int,'name'->str,'outE'->edge{*}),
  vertex<=mmkv:('k'->int,'v'->person:('name'->str,'age'->int))-<
    ('id'      -> .k,
     'name'    -> .v.name,
     'outE'    -> <x>[=mmkv,''].v[is,[and,[a,edge],.outV==<.x>.k]]),
  vertex<=int<x>[=mmkv,''][is,.k==x]]
