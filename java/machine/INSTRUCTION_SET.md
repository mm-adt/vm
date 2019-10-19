# reduce instruction takes a monoidic instruction
   [reduce,[mean]]
   [reduce,[min]]
   [reduce,[max]]
   [reduce,[plus]] // [sum]
   [reduce,[mult]]
[q][reduce,[plus]] // [count]


A -> [plus,A] => A
