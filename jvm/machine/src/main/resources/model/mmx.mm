mmx:(
'import' -> (mm -> ()),
'type'   -> (
  bool   -> (bool<=str),
  int    -> (int<=real,int<=str),
  real   -> (real<=int,real<=str),
  str    -> (str<=bool,str<=int,str<=real,str<=lst,str<=rec)))