social:(
'import'  -> (mm -> ()),
'type'    ->
  (nat    -> (nat<=int[is,bool<=int[gt,0]]),
   date   -> (date:(nat<=nat[is=<12];nat<=nat[is=<31];nat),
              date<=(nat<=nat[is=<12];nat<=nat[is=<31])[put,2,2009]),
   moday  -> (moday<=int-<(int;int)),
   person -> (person:('id'->int,'name'->str,'age'->nat),
              person<=int-<('id'->_,'name'->'anon','age'->1)),
              /* person<=badge-<('id'->.code,'name'->'anon','age'->.date.2)), */
   badge  -> (badge:('code'->int,'date'->date),
              badge<=int-<('code'->_,'date'->date))))
