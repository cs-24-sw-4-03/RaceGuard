/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: ParLang.g4
 */
grammar ParLang;
// parser rules start with lowercase letters, lexer rules with uppercase

//PARSER RULES -----------------------------------------------------------------------

/** init used as start non-terminal for parser */
init  : value EOF;  // must match at least one value

/** ACCEPTS: boolean expressions, or arithmetic expressions */
value : boolExp
    | compareExp
    | arithExp
    ;


// Expression evaluating boolean value of a boolean expression
boolExp : boolAndExp (LOGIC_OR boolAndExp)*; // OR have lowest logical precedence

boolAndExp : boolTerm (LOGIC_AND boolTerm)*; //AND have higher precedence than OR

boolTerm : LOGIC_NEGATION boolExp //Negation have higher precedence than AND and OR
    | PARAN_OPEN boolExp PARAN_CLOSE //parenthesis have highest precedence
    | compareExp
    | BOOL_TRUE //boolTerm can be a simple boolean TRUE or FALSE
    | BOOL_FALSE
    ;


// expression evaluating boolean value of two arithmetic expressions based on compare operator
compareExp : arithExp compareOperator arithExp;


// arithmetic expressions
arithExp : term ((PLUS | MINUS) term)* // PLUS and MINUS have lowest precedence of arithmetic operators
    | PARAN_OPEN arithExp PARAN_CLOSE
    ;

term : factor ((MULTIPLY | DIVIDE | MODULUS) factor)*; // MULTIPLY, DIVIDE and MODULUS have highest
                                                        // precedence of arithmetic operators
factor : number
    | PARAN_OPEN arithExp PARAN_CLOSE; // parenthesis have highest precedence when evaluating arithmetic expressions

number : INT
    |DOUBLE
    ;

// operator to compare two arithmetic expressions
compareOperator : compareEqNEg;

compareEqNEg : EQUAL // EQUAL and NOTEQUAL have lower precedence than other compare operators
    | NOTEQUAL
    | compareOther
    ;

compareOther : GREATER // Other compare operators have same precedence
    | GREATER_OR_EQUAL
    | LESSTHAN_OR_EQUAL
    | LESSTHAN
    ;
//--------------------------------------------------------------------------------------------------


//LEXER RULES -------------------------------------------------------------------------------------

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
POS_INT : DIGIT+ ;
STRICT_POS_INT : '0'* POS_DIGIT DIGIT* ; // Define INT that is strictly positive 0 not included
DOUBLE : DIGIT+ DOT DIGIT+ ; // Define token for decimal number
BOOL_TRUE : 'TRUE' ; // define value of boolean TRUE
BOOL_FALSE : 'FALSE' ; // define value of boolean FALSE
WS  :   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
IDENTIFIER : IDstart IDpart* ; // Define identifier token, identifier cannot start with a number
WAIT : 'wait' ; // Token used to wait for threads to finish executing
RETURN : 'return' ; // Token for returning from a function
FORK : 'fork' ; // Token used to define a multithreaded function does not have to return before continuing
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

