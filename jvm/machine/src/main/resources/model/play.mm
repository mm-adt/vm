play:(
'import' -> (mm -> ()),
'type'   -> (
  A      -> (A<=int),
  B      -> (B<=A[is>0]),
  C      -> (C<=B[is>10],C<=D[is>15]),
  D      -> (D<=B[is>20])
))