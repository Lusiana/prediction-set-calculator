This project aims to provide a program which can evaluate an input grammar and computes the first and follow set of it. Then, it constructs a table of this result along with the prediction set for each specific production rule.

A sample input grammar format:

```
E       : Prefix "(" E ")"
        | v Tail        <-- E also derives 'v Tail' -->
        ;               <-- end of production rule -->

Prefix  : f
        |               <-- empty line means the symbol derives empty string -->
        ;               <-- end of production rule -->

Tail    : "+" E
        |               <-- empty line means the symbol derives empty string -->
        ;               <-- end of production rule -->
```

This sample input file will construct this output table:

```
LHS	RHS	                FIRST	FOLLOW	PREDICT
E	Prefix "(" E ")"	( f 	) 	( f 
E	v Tail	                v 	) 	v 
Prefix	f	                f 	( 	f 
Prefix	(null)	                ( 	( 	( 
Tail	"+" E	                + 	) 	+ 
Tail	(null)	                ) 	) 	) 
```