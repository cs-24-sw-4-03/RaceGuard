/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: ParLang.g4
 */
grammar ParLang;
// parser rules start with lowercase letters, lexer rules with uppercase

//PARSER RULES -----------------------------------------------------------------------

/** init used as start non-terminal for parser */
init  : (value SEMICOLON)+ EOF;  // must match at least one value

/** ACCEPTS: boolean expressions, or arithmetic expressions */
value : boolExp
    | compareExp
    | arithExp
    | declaration
    ;


declaration : INT_TYPE IDENTIFIER (ASSIGN integer)?
    | DOUBLE_TYPE IDENTIFIER (ASSIGN DOUBLE)?
    | STRING_TYPE IDENTIFIER (ASSIGN STRING)?
    | BOOL_TYPE IDENTIFIER (ASSIGN (BOOL_FALSE | BOOL_TRUE))?
    | ARRAY_TYPE IDENTIFIER ASSIGN arrayAssign
    ;

arrayAssign : SQUARE_OPEN STRICT_POS_INT SQUARE_CLOSE // dont know if it can cause problems that an array can be difed as [0]
    | CURLY_OPEN typeList CURLY_CLOSE
    ;

typeList : integer (COMMA integer)*
    | DOUBLE (COMMA DOUBLE)*
    | (BOOL_TRUE | BOOL_FALSE) (COMMA (BOOL_TRUE | BOOL_FALSE))*
    | STRING (COMMA STRING)*
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

number : integer
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

primitiveType : INT_TYPE
    | DOUBLE_TYPE
    | STRING_TYPE
    | BOOL_TYPE
    ;

primitive : INT
    | DOUBLE
    | STRING
    | BOOL_FALSE
    | BOOL_TRUE
    ;
integer : INT
    | STRICT_POS_INT
    ;

//--------------------------------------------------------------------------------------------------


//LEXER RULES -------------------------------------------------------------------------------------

//fragments used to ease other definitions
fragment DIGIT : [0-9];
fragment POS_DIGIT : [1-9]; //Strictly positive digit
fragment SMALL_LETTER : [a-z];
fragment CAP_LETTER : [A_Z];
fragment LETTER : SMALL_LETTER | CAP_LETTER;
fragment IDstart : ( LETTER | '_' ); //since identifier cannot start with a digit
fragment IDpart : IDstart | DIGIT;

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


//Types in language
INT_TYPE : 'int';
DOUBLE_TYPE : 'double';
BOOL_TYPE : 'bool';
STRING_TYPE : 'string';
NULL_TYPE : 'null';
ARRAY_TYPE : (INT_TYPE | DOUBLE_TYPE | BOOL_TYPE | STRING_TYPE) SQUARE_OPEN SQUARE_CLOSE;
COLLECTION : 'collection';

//Control structures
IF : 'if';
IF_ELSE : 'if else';
ELSE : 'else';
WHILE : 'while';
DO_WHILE : 'do while';
FOR : 'for';

STRICT_POS_INT : '0'* POS_DIGIT DIGIT* ; // Define INT that is strictly positive 0 not included
INT :   (MINUS | ) DIGIT+ ;  // Define token INT as one or more digits
DOUBLE : DIGIT+ DOT DIGIT+ ; // Define token for decimal number
STRING : DOUBLE_QUOTATION ~["\\\t\r\n]* DOUBLE_QUOTATION;
BOOL_TRUE : 'TRUE' ; // define value of boolean TRUE
BOOL_FALSE : 'FALSE' ; // define value of boolean FALSE
WS  :   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out
IDENTIFIER : IDstart IDpart* ; // Define identifier token, identifier cannot start with a number
WAIT : 'wait' ; // Token used to wait for threads to finish executing
RETURN : 'return' ; // Token for returning from a function
FORK : 'fork' ; // Token used to define a multithreaded function does not have to return before continuing
COMMENT : '//' ~[\t\r\n]* '\t'? '\r'? '\n' -> skip ; //Define comment rule, skip comments