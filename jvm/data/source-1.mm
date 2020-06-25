[define,nat<=int[is>0]]
[define,person<=('name'->str,'age'->nat)]
[define,vertex:('name'->int)<=person[put,'name',.age]]

