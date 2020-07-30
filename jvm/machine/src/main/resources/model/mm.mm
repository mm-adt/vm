[model,mm:(
   bool -> (bool),
   int  -> (int,
           (int)<=(int[neg][neg]),
           (int)<=(int[plus,0]),
           (int)<=(int[mult,1]),
           ([0])<=(int[mult,0])),
   real -> (real),
   str  -> (str),
   lst  -> (lst),
   rec  -> (rec))]
