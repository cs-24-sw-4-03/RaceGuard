/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: ParLang.g4
 */
grammar ParLang;
// parser rules start with lowercase letters, lexer rules with uppercase
//PARSER RULES
/** A rule called init that matches comma-separated values between {...}. */
init  : value EOF;  // must match at least one value

/** A value can be either a nested array/struct or a simple integer (INT) */
value : boolExp
    | compareExp
    | exp
    ;

boolExp : boolAndExp (LOGIC_OR boolAndExp)*;

boolAndExp : boolTerm (LOGIC_AND boolTerm)*; //AND have higher precedence than OR

boolTerm : LOGIC_NEGATION boolExp //Negation have higher precedence than AND and OR
    | PARAN_OPEN boolExp PARAN_CLOSE //parenthesis have highest precedence
    | compareExp
    | BOOL_LITERAL
    ;

compareExp : exp compareEqNEg exp;

exp : term ((PLUS | MINUS) term)*
    | PARAN_OPEN exp PARAN_CLOSE
    ;

term : factor ((MULTIPLY | DIVIDE | MODULUS) factor)*;

factor : number
    | PARAN_OPEN exp PARAN_CLOSE;

number : INT
    |DOUBLE
    ;

compareEqNEg : EQUAL // all comparing operators have same precedence
    | NOTEQUAL
    | compareOther
    ;
compareOther : GREATER
    | GREATER_OR_EQUAL
    | LESSTHAN_OR_EQUAL
    | LESSTHAN
    ;

//LEXER RULES
ASSIGN                  : '=';
COMMA                   : ',';
CURLY_OPEN              : '{';
CURLY_CLOSE             : '}';
COLON                   : ':';
SEMICOLON               : ';';
PARAN_OPEN              : '(';
PARAN_CLOSE             : ')';
SQUARE_OPEN             : '[';
SQUARE_CLOSE            : ']';
DOT                     : '.';
GREATER                 : '>';
GREATER_OR_EQUAL        : '>=';
LESSTHAN                : '<';
LESSTHAN_OR_EQUAL       : '<=';
EQUAL                   : '==';
NOTEQUAL                : '!=';
LOGIC_NEGATION          : '!';
LOGIC_AND               : '&&';
LOGIC_OR                : '||';
PLUS                    : '+';
MINUS                   : '-';
MULTIPLY                : '*';
DIVIDE                  : '/';
MODULUS                 : '%';
DOUBLE_QUOTATION        : '"';
QUOTATION               : '\'';

//fragments used to ease other definitions
fragment DIGIT : [0-9];
fragment POS_DIGIT : [1-9]; //Strictly positive digit
fragment IDstart : ( [a-z] | [A-Z] | '_' ); //since identifier cannot start with a digit
fragment IDpart : IDstart | DIGIT;

INT :   (MINUS | ) DIGIT+ ;  // Define token INT as one or more digits
POS_INT : '0'* POS_DIGIT DIGIT* ; // Define INT that is strictly positive
DOUBLE : DIGIT+ DOT DIGIT+; // Define token for decimal number
BOOL_LITERAL : 'TRUE' | 'FALSE'; // Define values of a boolean
WS  :   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
IDENTIFIER : IDstart IDpart* ; // Define identifier token, identifier cannot start with a number
WAIT : 'wait';
RETURN : 'return';
FORK : 'fork';
COMMENT : '//' ~[\t\r\n]* '\t'? '\r'? '\n' -> skip ; //Define comment rule, skip comments

//Types in language
INT_TYPE : 'int';
DOUBLE_TYPE : 'double';
BOOL_TYPE : 'bool';
STRING_TYPE : 'string';
NULL_TYPE : 'null';
ARRAY_TYPE : SQUARE_OPEN SQUARE_CLOSE;
COLLECTION : 'collection';

//Control structures
IF : 'if';
IF_ELSE : 'if else';
ELSE : 'else';
WHILE : 'while';
DO_WHILE : 'do while';
FOR : 'for';

