/** Grammars always start with a grammar header. This grammar is called
 *  ArrayInit and must match the filename: ParLang.g4
 */
grammar ParLang;
// parser rules start with lowercase letters, lexer rules with uppercase

//PARSER RULES -----------------------------------------------------------------------

// init used as start non-terminal for parser
init : script* actor* mainFunc actor* EOF;  // must have a main function end on an end of file character

/** TO DO !!!!!!!!!!!!!!!!

*/

//main function, here the program should start
mainFunc : MAIN parameters body;

//scripts can define methods that need to be implemented by following actors
script : SCRIPT_TYPE identifier CURLY_OPEN scriptMethod+ CURLY_CLOSE;
scriptMethod : (ON_METHOD | LOCAL_METHOD) identifier parameters (COLON allTypes)? SEMICOLON;

//defines the scripts an actor follows
follow : FOLLOWS identifier (COMMA identifier)*;

//declaration of an actor
actor : ACTOR_TYPE identifier follow? CURLY_OPEN actorState actorKnows spawn actorMethod* CURLY_CLOSE;
// state of an actor
actorState : STATE CURLY_OPEN (declaration SEMICOLON)* CURLY_CLOSE;
// defines which actors this actor knows
actorKnows : KNOWS CURLY_OPEN (identifier identifier SEMICOLON)* CURLY_CLOSE;
// defines how to create an instance of this actor type
spawn : SPAWN parameters body;
// methods of this actor
actorMethod: onMethod | localMethod;
onMethod : ON_METHOD  identifier parameters body;
localMethod: LOCAL_METHOD identifier parameters COLON allTypes localMethodBody;

//Ways to acces either the state of an actor or access the actors known of the current actor
actorAccess : stateAccess
    | knowsAccess
    ;
stateAccess: STATE DOT (IDENTIFIER | arrayAccess);
knowsAccess: KNOWS DOT IDENTIFIER;

printCall : PRINT PARAN_OPEN printBody PARAN_CLOSE SEMICOLON;
printBody : (STRING | arrayAccess | identifier) (PLUS (STRING | arrayAccess | identifier))*;

//the different control structures in the language
controlStructure : selection
    | forLoop
    | whileLoop
    ;

// for loop can take an identifier or declare one and have an evaluation expression and end of loop statement executed at the end of each run through
forLoop : FOR PARAN_OPEN (declaration |assignment)? SEMICOLON boolExp SEMICOLON forStatement? PARAN_CLOSE body;
//while loop only having a evaluation before each loop
whileLoop : WHILE PARAN_OPEN (boolExp) PARAN_CLOSE body;

//if statements must contain an if part
selection : IF PARAN_OPEN boolExp PARAN_CLOSE body (ELSE (selection|body))?;

// Declaration used to declare variables
declaration: allTypes identifier (initialization)?; //array type is included in allTypes
initialization:  ASSIGN (arithExp | primitive | list | spawnActor | methodCall | boolExp | arrayAccess | identifier);

//assignment used to assign a value to an already defined variable.
assignment: (arrayAccess | identifier) ASSIGN (arithExp | primitive | list | spawnActor | arrayAccess | methodCall | boolExp | identifier);

// Expression evaluating boolean value of a boolean expression
boolExp : boolAndExp (LOGIC_OR boolAndExp)*; // OR have lowest logical precedence
boolAndExp : boolCompare (LOGIC_AND boolCompare)*; //AND have higher precedence than OR
boolCompare : boolTerm ((EQUAL | NOTEQUAL) boolTerm)?; // Comparing boolean values
boolTerm : PARAN_OPEN boolExp PARAN_CLOSE //parenthesis have highest precedence
    | compareExp
    | boolLiteral //boolTerm can be a simple boolean TRUE or FALSE
    | arrayAccess
    | identifier
    | negatedBool
    | methodCall
    ;
// negates a boolean value
negatedBool : LOGIC_NEGATION PARAN_OPEN boolExp PARAN_CLOSE
    | LOGIC_NEGATION identifier
    | LOGIC_NEGATION boolLiteral
    | LOGIC_NEGATION stateAccess
    ;
// expression evaluating boolean value of two arithmetic expressions based on compare operator
compareExp : arithExp compareOperator arithExp;

// arithmetic expressions
arithExp : term ((PLUS | MINUS) term)* // PLUS and MINUS have lowest precedence of arithmetic operators
    | PARAN_OPEN arithExp PARAN_CLOSE
    ;
term : factor ((MULTIPLY | DIVIDE | MODULUS) factor)*; // MULTIPLY, DIVIDE and MODULUS have highest                                                     // precedence of arithmetic operators
factor : number
    | identifier
    | stateAccess
    | PARAN_OPEN arithExp PARAN_CLOSE// parenthesis have highest precedence when evaluating arithmetic expressions
    | unaryExp
    ;
unaryExp : MINUS PARAN_OPEN arithExp PARAN_CLOSE
    | MINUS (number | identifier)
    ; // unary minus operator

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
statement : declaration SEMICOLON
    | assignment SEMICOLON
    | sendMsg SEMICOLON
    | controlStructure
    | methodCall SEMICOLON
    | printCall
    | returnStatement
    | spawnActor SEMICOLON
    ;

//a for loop can only send messages, make a declaration or assignment, or make an arithmetic axpression in the lop-end statement
forStatement : sendMsg
    |assignment
    ;

// body is a block of code
body : CURLY_OPEN statement*  CURLY_CLOSE;
localMethodBody: CURLY_OPEN statement* returnStatement? CURLY_CLOSE;


// defines the parameters of a function
parameters : PARAN_OPEN ((allTypes | identifier) identifier (COMMA (allTypes | identifier) identifier)*)? PARAN_CLOSE;
// the arguments passed when calling function
arguments : PARAN_OPEN (value (COMMA value)*)? PARAN_CLOSE;

//send a message to Actor and request use of method
sendMsg : (SELF | identifier) SEND_MSG identifier arguments;

//way to call a method
methodCall : identifier arguments;

// to instanziate a new actor of a defined type
spawnActor : SPAWN identifier arguments;
//access array
arrayAccess : identifier SQUARE_OPEN arithExp SQUARE_CLOSE;

//arbritatry lsit of either ints, doubles, bools, or strings
list : CURLY_OPEN listItem (COMMA listItem)* CURLY_CLOSE;

// items that can be listed
listItem : value //alternatively "primitive | identifier" instead of value if we dont want somehting like "2+2" as element in an integer array.
    | list
    ;

identifier : IDENTIFIER
    | actorAccess
    ;

// can be any type defined in language
allTypes : primitiveType
    | primitiveType ARRAY_TYPE
    | ACTOR_TYPE
    | VOID_TYPE
    | identifier
    ;

//can be any primitive type in language
primitiveType : INT_TYPE
    | DOUBLE_TYPE
    | STRING_TYPE
    | BOOL_TYPE
    ;
// values can be any type in the language
value : (primitive | arithExp | boolExp | actorAccess | arrayAccess | SELF | identifier)
    ;

number : INT
    | DOUBLE
    ; //number can be either integer or double

//can be any primitive value
primitive : number
    | STRING
    | boolLiteral
    ;

//either boolean value true of false
boolLiteral : BOOL_TRUE
    | BOOL_FALSE
    ;

returnStatement : RETURN returnType? SEMICOLON;

//Return types
returnType : identifier
    | arithExp
    | boolExp
    | primitive
    | actorAccess
    ;


//--------------------------------------------------------------------------------------------------


//LEXER RULES -------------------------------------------------------------------------------------

//fragments used to ease other definitions
fragment DIGIT : [0-9];
fragment POS_DIGIT : [1-9]; //Strictly positive digit
fragment SMALL_LETTER : [a-zæøå];
fragment CAP_LETTER : [A-ZÆØÅ];
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
VOID_TYPE : 'void';
SCRIPT_TYPE : 'Script';

//Actor specific keywords
SPAWN : 'Spawn';
STATE : 'State';
KNOWS : 'Knows';
ON_METHOD : 'on';
LOCAL_METHOD : 'local';
SEND_MSG : '<-';
SELF : 'self';
FOLLOWS : 'follows';

//Control structures
IF : 'if';
ELSE : 'else';
WHILE : 'while';
FOR : 'for';

MAIN : 'main';
RETURN : 'return';
PRINT : 'print';
INT : DIGIT+ ;  // Define token INT as one or more digits
DOUBLE : DIGIT* DOT DIGIT+ ; // Define token for decimal number
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
DOUBLE_QUOTATION        : '"' | '“';
QUOTATION               : '\'';
