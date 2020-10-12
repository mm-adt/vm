num:(
'import' -> (mm -> ()),
'type'   -> (
  nat    -> (nat<=int[is>0]),
  int    -> (int<=nat),
  cmplx  -> (cmplx:(real;real),
             cmplx:([x.0+y.0];[x.1+y.1])<=cmplx:(real;real)<x>[plus,cmplx<y>],
             cmplx:([x.0*y.0]+[x.1*y.1];[x.0*y.1]+[x.1*y.0])<=cmplx:(real;real)<x>[plus,cmplx<y>])
))