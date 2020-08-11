[define,vertex:('name'->str,'friends'->person{*}),
        vertex<=person:('name'->str,'age'->int,'knows'->int{*})-<('name'->.name, 'friends'->.knows[=mmkv,'','getByKeyEq',_].v)]