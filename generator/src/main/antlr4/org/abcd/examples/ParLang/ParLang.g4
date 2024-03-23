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

// Declaration used to declare variables
declaration : INT_TYPE IDENTIFIER (ASSIGN integer)? // optinal to assign value from the declaration
    | DOUBLE_TYPE IDENTIFIER (ASSIGN DOUBLE)? // when defining a variable the assignment type msut match
    | STRING_TYPE IDENTIFIER (ASSIGN STRING)?
    | BOOL_TYPE IDENTIFIER (ASSIGN (BOOL_FALSE | BOOL_TRUE))?
    | arrayAssign
    ;

// can define the length of the array or specify the array elements in between curly braces
arrayAssign : ARRAY_TYPE_INT arrayAssignLength
    | ARRAY_TYPE_DOUBLE arrayAssignLength
    | ARRAY_TYPE_BOOL arrayAssignLength
    | ARRAY_TYPE_STRING arrayAssignLength
    | ARRAY_TYPE_INT CURLY_OPEN integerList CURLY_CLOSE
    | ARRAY_TYPE_DOUBLE CURLY_OPEN doubleList CURLY_CLOSE
    | ARRAY_TYPE_BOOL CURLY_OPEN boolList CURLY_CLOSE
    | ARRAY_TYPE_STRING CURLY_OPEN stringList CURLY_CLOSE
    ;
// assignment of the length af an array
arrayAssignLength : IDENTIFIER ASSIGN SQUARE_OPEN STRICT_POS_INT SQUARE_CLOSE;

// commaseperated lists of primitives
integerList : integer (COMMA integer)*;
doubleList : DOUBLE (COMMA DOUBLE)*;
boolList : boolLiteral (COMMA boolLiteral)*;
stringList : STRING (COMMA STRING)*;


// Expression evaluating boolean value of a boolean expression
boolExp : boolAndExp (LOGIC_OR boolAndExp)*; // OR have lowest logical precedence

boolAndExp : boolTerm (LOGIC_AND boolTerm)*; //AND have higher precedence than OR

boolTerm : LOGIC_NEGATION boolExp //Negation have higher precedence than AND and OR
    | PARAN_OPEN boolExp PARAN_CLOSE //parenthesis have highest precedence
    | compareExp
    | boolLiteral //boolTerm can be a simple boolean TRUE or FALSE
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
    | boolLiteral
    ;
    
integer : INT
    | STRICT_POS_INT
    ;

boolLiteral : BOOL_TRUE
    | BOOL_FALSE
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
ARRAY_TYPE_INT : INT_TYPE SQUARE_OPEN SQUARE_CLOSE;
ARRAY_TYPE_BOOL : BOOL_TYPE SQUARE_OPEN SQUARE_CLOSE;
ARRAY_TYPE_DOUBLE : DOUBLE_TYPE SQUARE_OPEN SQUARE_CLOSE;
ARRAY_TYPE_STRING : STRING_TYPE SQUARE_OPEN SQUARE_CLOSE;
COLLECTION : 'collection';

//Actor specific keywords
SPAWN : 'Spawn';
STATE : 'State';
KNOWS : 'Knows';
ON_MSG : 'on';
SEND_MSG : '<-';

//Control structures
IF : 'if';
IF_ELSE : 'if else';
ELSE : 'else';
WHILE : 'while';
DO_WHILE : 'do while';
FOR : 'for';

STRICT_POS_INT : POS_DIGIT DIGIT* ; // Define INT that is strictly positive 0 not included
INT :   (MINUS | ) DIGIT+ ;  // Define token INT as one or more digits
DOUBLE : DIGIT+ DOT DIGIT+ ; // Define token for decimal number
STRING : DOUBLE_QUOTATION ~[\\"\t\r\n]* DOUBLE_QUOTATION;
BOOL_TRUE : 'TRUE' ; // define value of boolean TRUE
BOOL_FALSE : 'FALSE' ; // define value of boolean FALSE
IDENTIFIER : IDstart IDpart* ; // Define identifier token, identifier cannot start with a number
WAIT : 'wait' ; // Token used to wait for threads to finish executing
RETURN : 'return' ; // Token for returning from a function
FORK : 'fork' ; // Token used to define a multithreaded function does not have to return before continuing
COMMENT : '//' ~[\t\r\n]* '\t'? '\r'? '\n' -> skip ; //Define comment rule, skip comments
WS  :   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out