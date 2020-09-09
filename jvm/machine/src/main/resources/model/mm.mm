mm:(
'type' ->
  (bool   -> (bool),
   int    -> (int),
   real   -> (real),
   str    -> (str),
   lst    -> (lst),
   rec    -> (rec),
   inst   -> (inst),
   (_)    -> ((_)<=^:([id])
              /* ([branch,x])<=^:([split,<x>][merge]) */
              ),
   (int)  -> ((int)<=^:(int[neg][neg]),
              (int)<=^:(int[plus,0]),
              (int)<=^:(int[mult,1])),
   ([1])  -> (([1])<=^:(int[one])),
   ([0])  -> (([0])<=^:(int[zero]),
              ([0])<=^:(int[mult,0])),
   (['']) -> (([''])<=^:(str[zero]))))