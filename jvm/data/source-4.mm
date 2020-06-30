[define,vertex<=person:('name'->str,'age'->int,'knows'->int{*})-<
  ('name'    ->.name,
   'friends' ->.knows[=mmkv,'','getByKeyEq',_].v
  )]