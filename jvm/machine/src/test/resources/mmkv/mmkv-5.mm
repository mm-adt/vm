mmkv:('k'->int,'v'->[person:('name'->str,'age'->int)|edge:('outV'->int,'label'->str,'inV'->int)])
mmkv:('k'->1,'v'->person:('name'->'marko','age'->29))
mmkv:('k'->2,'v'->person:('name'->'ryan','age'->25))
mmkv:('k'->3,'v'->person:('name'->'stephen','age'->32))
mmkv:('k'->4,'v'->person:('name'->'kuppitz','age'->23))
mmkv:('k'->5,'v'->edge:('outV'->1,'label'->'knows','inV'->2))
mmkv:('k'->6,'v'->edge:('outV'->1,'label'->'knows','inV'->3))
mmkv:('k'->7,'v'->edge:('outV'->2,'label'->'likes','inV'->3))