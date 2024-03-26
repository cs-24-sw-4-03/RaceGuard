/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: ParLang.g4
 */
grammar ParLang;
// parser rules start with lowercase letters, lexer rules with uppercase

//PARSER RULES -----------------------------------------------------------------------

// init used as start non-terminal for parser
init  : actor* mainFunc actor* EOF;  // must have a main function end on an end of file character


/** TO DO !!!!!!!!!!!!!!!!

*/

//main function, here the program should start
mainFunc : MAIN arguments body;

//declaration of an actor
actor : ACTOR_TYPE IDENTIFIER CURLY_OPEN actorState actorKnows spawn actorMethod* CURLY_CLOSE;
// state of an actor
actorState : STATE CURLY_OPEN (declaration SEMICOLON)* CURLY_CLOSE;
// defines which actors this actor knows
actorKnows : KNOWS CURLY_OPEN (IDENTIFIER (COMMA IDENTIFIER)*)? CURLY_CLOSE;
// defines how to create an instance of this actor type
spawn : SPAWN arguments body;
// methods of this actor
actorMethod : (ON_METHOD | LOCAL_METHOD) IDENTIFIER arguments body;
//Ways to acces either the state of an actor or access the actors known of the current actor
actorAccess : STATE DOT IDENTIFIER
    | KNOWS DOT IDENTIFIER
    ;

//the different control structures in the language
controlStructure : ifElse
    | forLoop
    | whileLoop
    ;

// for loop can take an identifier or declare one and have an evaluation expression and something that should happen at the end of each loop
forLoop : FOR PARAN_OPEN (IDENTIFIER | declaration)? SEMICOLON boolExp SEMICOLON (statement)? PARAN_CLOSE body;
//while loop only having a evaluation before each loop
whileLoop : WHILE PARAN_OPEN boolExp PARAN_CLOSE body;

//if statements must contain an if part
ifElse : IF PARAN_OPEN boolExp PARAN_CLOSE body elsePart?;
//the else part of an if statement is optional
elsePart : elseIf* ELSE body;
//else if parts are also optional
elseIf : ELSE_IF PARAN_OPEN boolExp PARAN_CLOSE body;

// Declaration used to declare variables
declaration : allTypes? (IDENTIFIER (ARRAY_TYPE)? | actorAccess) (ASSIGN (arithExp | primitive | arrayAssign | IDENTIFIER | actorAccess | spawnActor))?;

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
    | IDENTIFIER
    | actorAccess
    | PARAN_OPEN arithExp PARAN_CLOSE; // parenthesis have highest precedence when evaluating arithmetic expressions

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

//ACCEPTS: boolean expressions, arithmetic expressions,
//comparison of arithmetic expressions declarations, control structures, and sending of messages
statement : boolExp SEMICOLON
    | compareExp SEMICOLON
    | arithExp SEMICOLON
    | declaration SEMICOLON
    | sendMsg SEMICOLON
    | controlStructure
    ;

// body is a piece of code
body : CURLY_OPEN statement* CURLY_CLOSE;

// defines the arguments of a function
arguments : PARAN_OPEN (allTypes IDENTIFIER (COMMA allTypes IDENTIFIER)*)? PARAN_CLOSE;
// the paramerters passed when calling function
parameters : PARAN_OPEN (value (COMMA value)*)? PARAN_CLOSE;

//send a message to Actor and request use of method
sendMsg : IDENTIFIER SEND_MSG IDENTIFIER parameters;

// to instanziate a new actor of a defined type
spawnActor : SPAWN IDENTIFIER parameters;

// can define the length of the array or specify the array elements in between curly braces
arrayAssign : arrayAssignLength
    | list
    ;

// assignment of the length af an array
arrayAssignLength : IDENTIFIER ASSIGN SQUARE_OPEN STRICT_POS_INT SQUARE_CLOSE;

//arbritatry lsit of either ints, doubles, bools, or strings
list : CURLY_OPEN listItem (COMMA listItem)* CURLY_CLOSE;

// items that can be listed
listItem : integer
    | DOUBLE
    | STRING
    | IDENTIFIER
    | boolLiteral
    | list
    ;

// can be any type defined in language
allTypes : primitiveType
    | primitiveType ARRAY_TYPE
    | ACTOR_TYPE
    ;

//can be any primitive type in language
primitiveType : INT_TYPE
    | DOUBLE_TYPE
    | STRING_TYPE
    | BOOL_TYPE
    ;
// values can be any type in the language
value : (primitive | IDENTIFIER | arithExp | STRICT_POS_INT);

number : integer
    |DOUBLE
    ; //number can be either integer or double

//can be any primitive value
primitive : INT
    | DOUBLE
    | STRING
    | boolLiteral
    ;

integer : INT
    | STRICT_POS_INT
    ;

//either boolean value true of false
boolLiteral : BOOL_TRUE
    | BOOL_FALSE
    ;

//--------------------------------------------------------------------------------------------------


//LEXER RULES -------------------------------------------------------------------------------------

//fragments used to ease other definitions
fragment DIGIT : [0-9];
fragment POS_DIGIT : [1-9]; //Strictly positive digit
fragment SMALL_LETTER : [a-z];
fragment CAP_LETTER : [A-Z];
fragment LETTER : SMALL_LETTER | CAP_LETTER;
fragment IDstart : ( LETTER | '_' ); //since identifier cannot start with a digit
fragment IDpart : IDstart | DIGIT;

//Types in language
INT_TYPE : 'int';
DOUBLE_TYPE : 'double';
BOOL_TYPE : 'bool';
STRING_TYPE : 'string';
NULL_TYPE : 'null';
ARRAY_TYPE : '[]';
ACTOR_TYPE : 'Actor';

//Actor specific keywords
SPAWN : 'Spawn';
STATE : 'State';
KNOWS : 'Knows';
ON_METHOD : 'on';
LOCAL_METHOD : 'local';
SEND_MSG : '<-';

//Control structures
IF : 'if';
ELSE_IF : 'else-if';
ELSE : 'else';
WHILE : 'while';
FOR : 'for';

MAIN : 'main';
STRICT_POS_INT : POS_DIGIT DIGIT* ; // Define INT that is strictly positive 0 not included
INT :   (MINUS | ) DIGIT+ ;  // Define token INT as one or more digits
DOUBLE : DIGIT+ DOT DIGIT+ ; // Define token for decimal number
//strings are inside either quotation marks or double quotation marks
STRING : (DOUBLE_QUOTATION ~[\\"\t\r\n]* DOUBLE_QUOTATION) | (QUOTATION ~[\\"\t\r\n]* QUOTATION);
BOOL_TRUE : 'TRUE' ; // define value of boolean TRUE
BOOL_FALSE : 'FALSE' ; // define value of boolean FALSE
IDENTIFIER : IDstart IDpart* ; // Define identifier token, identifier cannot start with a number
COMMENT : '//' ~[\t\r\n]* '\t'? '\r'? '\n' -> skip ; //Define comment rule, skip comments
WS  :   [ \t\r\n]+ -> skip ; // Define whitespace rule, toss it out

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
