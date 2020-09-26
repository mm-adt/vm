mmx:('type' -> (
  bool -> (bool<=str),
  int  -> (int<=[real|str]),
  real -> (real<=[int|str]),
  str  -> (str<=[bool|int|real|poly]))) <= mm