mm:(
  'type' -> (bool -> (bool),
             int  -> (int),
             real -> (real),
             str  -> (str),
             lst  -> (lst),
             rec  -> (rec)),
  'path' -> (int  -> ((int)<=(int[neg][neg]),
                     (int)<=(int[plus,0]),
                     (int)<=(int[mult,1]),
                     ([0])<=(int[mult,0]))))