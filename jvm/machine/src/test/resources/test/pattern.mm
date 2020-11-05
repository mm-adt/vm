pattern:('import' -> (mm -> ()),
'type' -> (
 pair  -> (pair:(_;_),
           pair<=_-<(_;_)),
 ipair -> (ipair:(int;int),
           ipair<=int-<(_;_)),
 fst   -> (fst<=(_;_).0),
 snd   -> (snd<=(_;_).1),
 ifst  -> (ifst<=ipair.0),
 isnd  -> (isnd<=ipair.1)
))