play:(
  'type' -> (
    boom -> (boom<=([id];[id])[get,0,int]+10),
    pair -> (pair:([id];[id]),
             pair<=_-<([id];[id])),
    str  -> (str<=int[plus,10])
  )
) <= mm