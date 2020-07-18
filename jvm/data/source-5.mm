[define,
  edge:('outV'->int,'label'->str,'inV'->int),
  vertex<=mmkv:('k'->int,'v'->person:('name'->str,'age'->int))-<
    ('id'      -> .k,
     'name'    -> .v.name,
     'outE'    -> <x>[=mmkv,''].v[is,[and,[a,edge],.outV==<.x>.k]]),
  vertex<=int<x>[=mmkv,''][is,.k==x]]
[rewrite,(.outE.inV[as,vertex])<=(.out)]
