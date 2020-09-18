mm:('type' ->
  (bool ->  ( ), int ->  ( ), real -> ( ),
   str  ->  ( ), lst  -> ( ), rec ->  ( ),
   poly ->  (poly<=[lst|rec]),
   inst ->  ( ),
   // consider rewrites in a mm_poly1.mm model.
   // a model that processes models with poly lifted reification
   (_)    ->  ((_)<=^:([id])
               /* ([branch,x])<=^:([split,<x>][merge]) */
               ),
   (int)  ->  ((int)<=^:(int[neg][neg]),
               (int)<=^:(int[plus,0]),
               (int)<=^:(int[mult,1])),
   ([1])  ->  (([1])<=^:(int[one])),
   ([0])  ->  (([0])<=^:(int[zero]),
               ([0])<=^:(int[mult,0])),
   (['']) -> (([''])<=^:(str[zero]),
               (str)<=^:(str[plus,'']))))