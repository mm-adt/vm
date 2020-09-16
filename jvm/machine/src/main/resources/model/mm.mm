mm:('type' ->
  (bool   ->  ( ),
   int    ->  ( ),
   real   ->  ( ),
   str    ->  ( ),
   lst    ->  ( ),
   rec    ->  ( ),
   inst   ->  ( ),

   // considering extended type names and rewrites in a mm_ext model
   poly   ->  (poly<=[lst{?}|rec]),
   (_)    ->  ((_)<=^:([id])
               /* ([branch,x])<=^:([split,<x>][merge]) */
               ),
   // rewrites in mm_ext as well. it's a model that processes a model via poly lifted reification */
   (int)  ->  ((int)<=^:(int[neg][neg]),
               (int)<=^:(int[plus,0]),
               (int)<=^:(int[mult,1])),
   ([1])  ->  (([1])<=^:(int[one])),
   ([0])  ->  (([0])<=^:(int[zero]),
               ([0])<=^:(int[mult,0])),
   (['']) -> (([''])<=^:(str[zero]),
               (str)<=^:(str[plus,'']))))