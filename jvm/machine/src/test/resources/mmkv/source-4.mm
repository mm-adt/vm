[define,vertex<=person:('name'->str,'age'->int,'knows'->int{*})-<
  ('name'    ->.name,
   'friends' ->.knows<y>[=mmkv,''][map,[is,.k==<.y>].v]
  )]