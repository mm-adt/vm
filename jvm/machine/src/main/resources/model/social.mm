('type' -> (nat   -> (nat<=int[is,bool<=int[gt,0]]),
            date  -> (date:(nat<=nat[is=<12];nat<=nat[is=<31];nat),
                      date<=(nat<=nat[is=<12];nat<=nat[is=<31])[put,2,2009]),
            moday -> (moday<=int-<(int;int))))
