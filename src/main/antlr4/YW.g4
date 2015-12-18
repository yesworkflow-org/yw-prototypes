grammar YW ;

// YW annotation compositions
script          : (codeBlock)+ ;
codeBlock       : (EOL)* begin (EOL)* (codeBlock | blockAnnotation)* end (EOL)* ;
blockAnnotation : (inputPort | outputPort | call) (EOL)* ;
inputPort       : (in | param) (EOL)* (portAnnotation)* ;
outputPort      : (out | ret) (EOL)* (portAnnotation)* ;
portAnnotation  : (as | uri) (EOL)*;

// YW annotation primitives
as              : asKeyword alias ;
begin           : beginKeyword blockName ;
call            : callKeyword blockName ;
end             : endKeyword blockName ;
file            : fileKeyword PATH_TEMPLATE ;
in              : inKeyword variableName ;
out             : outKeyword variableName ;
param           : paramKeyword variableName ;
ret             : returnKeyword variableName ; 
uri             : uriKeyword uriTemplate ;

// YW keywords
asKeyword       : '@as' ;
beginKeyword    : '@begin' ;
callKeyword     : '@call' ;
endKeyword      : '@end' ; 
fileKeyword     : '@file' ;
inKeyword       : '@in' ;
outKeyword      : '@out' ;
paramKeyword    : '@param' ;
returnKeyword   : '@return' ;
uriKeyword      : '@uri' ;

// YW keyword arguments
blockName       : ID ;
variableName    : ID ;
alias           : ID ;
uriTemplate     : ((scheme) ':')? PATH_TEMPLATE ;
scheme          : 'file' | 'http' ; 

// lexer rules
ID              : [a-zA-Z0-9\_]+ ;
PATH_TEMPLATE   : ID ;
EOL             : [\r\n] ;
WS              : [ \t]+ -> skip ;