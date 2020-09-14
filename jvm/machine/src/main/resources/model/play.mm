play:(
  'type' -> (
    bow  -> (bow<=int+100),
    boom -> (boom<=pair[get,0,int]+10),
    px   -> (px:(bow;bow)),
    pair -> (pair:([id];[id]),
             pair<=_-<([id];[id])),
    str  -> (str<=int[plus,10])
  )
) <= mm