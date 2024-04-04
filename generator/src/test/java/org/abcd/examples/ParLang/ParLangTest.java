package org.abcd.examples.ParLang;

// import ANTLR's runtime libraries
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

import org.abcd.examples.ParLang.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class ParLangTest {
    private static boolean mappingMade = false;
    private static Map<Integer, LexerTokens> tokenMap = new HashMap<>();

    public static boolean parseTest(String realInput){
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(realInput);

        // create a lexer that feeds off of input CharStream
        ParLangLexer lexer = new ParLangLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        ParLangParser parser = new ParLangParser(tokens);

        parser.removeErrorListeners();
        ErrorListener errListen = new ErrorListener();
        parser.addErrorListener(errListen);

        ParseTree tree = parser.init(); // begin parsing at init rule

        List<SyntaxError> errors = errListen.getSyntaxErrors();
        if (errors.size() != 0){return false;}
        return true;
    }
    public static List<org.antlr.v4.runtime.Token> lexerTest(String realInput){
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(realInput);

        // create a lexer that feeds off of input CharStream
        ParLangLexer lexer = new ParLangLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        //filling in the tokens
        tokens.fill();

        //create list of tokens to return
        List<org.antlr.v4.runtime.Token> tokenList = tokens.getTokens();
        return tokenList;
    }

    @BeforeEach
    public void mapping() {
        if (mappingMade) {
            return;
        }
        else {
            // make the mapping of lexer tokens
            tokenMap.put(1, LexerTokens.INT_TYPE);
            tokenMap.put(2, LexerTokens.DOUBLE_TYPE);
            tokenMap.put(3, LexerTokens.BOOL_TYPE);
            tokenMap.put(4, LexerTokens.STRING_TYPE);
            tokenMap.put(5, LexerTokens.NULL_TYPE);
            tokenMap.put(6, LexerTokens.ARRAY_TYPE);
            tokenMap.put(7, LexerTokens.ACTOR_TYPE);
            tokenMap.put(8, LexerTokens.VOID_TYPE);
            tokenMap.put(9, LexerTokens.SPAWN);
            tokenMap.put(10, LexerTokens.STATE);
            tokenMap.put(11, LexerTokens.KNOWS);
            tokenMap.put(12, LexerTokens.ON_METHOD);
            tokenMap.put(13, LexerTokens.LOCAL_METHOD);
            tokenMap.put(14, LexerTokens.SEND_MSG);
            tokenMap.put(15, LexerTokens.SELF);
            tokenMap.put(16, LexerTokens.IF);
            tokenMap.put(17, LexerTokens.ELSE_IF);
            tokenMap.put(18, LexerTokens.ELSE);
            tokenMap.put(19, LexerTokens.WHILE);
            tokenMap.put(20, LexerTokens.FOR);
            tokenMap.put(21, LexerTokens.MAIN);
            tokenMap.put(22, LexerTokens.STRICT_POS_INT);
            tokenMap.put(23, LexerTokens.INT);
            tokenMap.put(24, LexerTokens.DOUBLE);
            tokenMap.put(25, LexerTokens.STRING);
            tokenMap.put(26, LexerTokens.BOOL_TRUE);
            tokenMap.put(27, LexerTokens.BOOL_FALSE);
            tokenMap.put(28, LexerTokens.IDENTIFIER);
            tokenMap.put(29, LexerTokens.COMMENT);
            tokenMap.put(30, LexerTokens.WS);
            tokenMap.put(31, LexerTokens.ASSIGN);
            tokenMap.put(32, LexerTokens.COMMA);
            tokenMap.put(33, LexerTokens.CURLY_OPEN);
            tokenMap.put(34, LexerTokens.CURLY_CLOSE);
            tokenMap.put(35, LexerTokens.COLON);
            tokenMap.put(36, LexerTokens.SEMICOLON);
            tokenMap.put(37, LexerTokens.PARAN_OPEN);
            tokenMap.put(38, LexerTokens.PARAN_CLOSE);
            tokenMap.put(39, LexerTokens.SQUARE_OPEN);
            tokenMap.put(40, LexerTokens.SQUARE_CLOSE);
            tokenMap.put(41, LexerTokens.DOT);
            tokenMap.put(42, LexerTokens.GREATER);
            tokenMap.put(43, LexerTokens.GREATER_OR_EQUAL);
            tokenMap.put(44, LexerTokens.LESSTHAN);
            tokenMap.put(45, LexerTokens.LESSTHAN_OR_EQUAL);
            tokenMap.put(46, LexerTokens.EQUAL);
            tokenMap.put(47, LexerTokens.NOTEQUAL);
            tokenMap.put(48, LexerTokens.LOGIC_NEGATION);
            tokenMap.put(49, LexerTokens.LOGIC_AND);
            tokenMap.put(50, LexerTokens.LOGIC_OR);
            tokenMap.put(51, LexerTokens.PLUS);
            tokenMap.put(52, LexerTokens.MINUS);
            tokenMap.put(53, LexerTokens.MULTIPLY);
            tokenMap.put(54, LexerTokens.DIVIDE);
            tokenMap.put(55, LexerTokens.MODULUS);
            tokenMap.put(56, LexerTokens.DOUBLE_QUOTATION);
            tokenMap.put(57, LexerTokens.QUOTATION);
            tokenMap.put(58, LexerTokens.EOF);
            tokenMap.put(-1, LexerTokens.EOF); //when tokens return type EOF comes out as -1

            mappingMade = true;
        }
    }

//PARSER TEST------------------------------------------------------------------------------------------------
    @Test
    public void testArithExpParsing() {
        String input = "main(){int i = 2 + 3 * 4;}";
        assertTrue(ParLangTest.parseTest(input));
    }

    @Test
    public void testWrongArithExpParsing() {
        String input = "main(){ 5 + 3 * 4;}";
        assertFalse(ParLangTest.parseTest(input));
    }

    @Test
    public void testControlStructureParsing() {
        String input = "main(){if (x > 0) { x = x + 1; } else { x = x - 1; }}";
        assertTrue(ParLangTest.parseTest(input));
    }
    @Test
    public void testWrongControlStructureParsing() {
        String input = "main(){(x > 0) { x = x + 1; } else { x = x - 1; }}";
        assertFalse(ParLangTest.parseTest(input));
    }

    @Test
    public void testDeclarationParsing() {
        String input = "main(){int x = 10;}";
        assertTrue(ParLangTest.parseTest(input));
    }

    @Test
    public void testWrongDeclarationNoType() {
        String input = "main(){x = 10;}";
        assertTrue(ParLangTest.parseTest(input));
    }

    @Test
    public void testWrongDeclarationParsing() {
        String input = "main(){s int x = 10;}";
        assertFalse(ParLangTest.parseTest(input));
    }

    @Test
    public void testBooleanExpressionParsing() {
        String input = "main(){TRUE && (5 < 10) || FALSE;}";
        assertTrue(ParLangTest.parseTest(input));
    }

    @Test
    public void testWrongBooleanExpressionParsing() {
        String input = "main(){ && (5 < 10) || FALSE;}";
        assertFalse(ParLangTest.parseTest(input));
    }

    @Test
    public void testNoInput() {
        String input = "";
        assertFalse(ParLangTest.parseTest(input));
    }
    //-------------------------------------------------------------------------------------------------------


    //LEXER TEST--------------------------------------------------------------------------------------------
    @Test
    public void testLexerTypes() {
        String input = "int double bool string null [] Actor void";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(9,tokenList.size());
        int counter = 1;
        for(org.antlr.v4.runtime.Token token : tokenList){
            /*System.out.println("IIIIIIIIIIIIIIIIIIIIIIIIIIIIIii");
            System.out.println(token);
            System.out.println("counte: " + tokenMap.get(counter));
            System.out.println("number " + token.getType());
            System.out.println("type: " + tokenMap.get(token.getType()));*/
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 8){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerActorSpec() {
        String input = "Spawn State Knows on local <- self";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(8,tokenList.size());
        int counter = 9;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 15){
                counter++;
            }
            else { //last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerControlStructPlusMain() {
        String input = "if else-if else while for main";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(7,tokenList.size());
        int counter = 16;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 21){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerprimitivesAndIdentifier() {
        String input = "5 -5 3.5 \"string\" TRUE FALSE identifier";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(8,tokenList.size());
        int counter = 22;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 28){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerCommentAndWhitespace() {
        String input = "//hee\n//        rgtlb&%234*^`^*^`:;,.,zvlb§@£$€546{[7978\n              ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be 1 as the lexer should skip comments and whitespace
        assertEquals(1,tokenList.size());
        //the only token should be EOF
        assertEquals(tokenMap.get(58),tokenMap.get(tokenList.get(0).getType()));
    }

    @Test
    public void testLexerSingleSymbols() {
        String input = "= , { } : ; ( ) [ ] .";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(12,tokenList.size());
        int counter = 31;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 41){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerCompareOperators() {
        String input = "> >= < <= == !=";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(7,tokenList.size());
        int counter = 42;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 47){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerLogicOperators() {
        String input = "! && ||";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(4,tokenList.size());
        int counter = 48;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 50){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerArithOperators() {
        String input = "+ - * / %";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(6,tokenList.size());
        int counter = 51;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 55){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerQuotations() {
        String input = "\" ' ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(3,tokenList.size());
        int counter =56;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 57){
                counter++;
            }
            else {//last token is always EOF which is rule 58
                counter = 58;
            }
        }
    }

    @Test
    public void testLexerEOF() {
        String input = "";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        assertEquals(1,tokenList.size());
        assertEquals(tokenMap.get(58),tokenMap.get(tokenList.get(0).getType()));
    }

    @Test
    public void testStringSingleQuote() {
        String input = " 'hello world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        assertEquals(2,tokenList.size());
        assertEquals(tokenMap.get(25),tokenMap.get(tokenList.get(0).getType()));
        assertEquals(tokenMap.get(58),tokenMap.get(tokenList.get(1).getType()));
    }

    @Test
    public void testStringDoubleQuote() {
        String input = " \"hello world!\" ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        assertEquals(2,tokenList.size());
        assertEquals(tokenMap.get(25),tokenMap.get(tokenList.get(0).getType()));
        assertEquals(tokenMap.get(58),tokenMap.get(tokenList.get(1).getType()));
    }

    @Test
    public void testStringEscape1() {
        String input = " 'hello \n world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        assertEquals(6,tokenList.size()); //strings cannot contain \n and will then force lexer to tokenize otherwise
    }
    @Test
    public void testStringEscape2() {
        String input = " 'hello \t world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        assertEquals(6,tokenList.size()); //strings cannot contain \t and will then force lexer to tokenize otherwise
    }
    @Test
    public void testStringEscape3() {
        String input = " 'hello \r world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        assertEquals(6,tokenList.size()); //strings cannot contain \r and will then force lexer to tokenize otherwise
    }

    /*@Test
    public void testUnknownToken() { //lexer does not handle unknown tokens well yet
        String input = " @ ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLangTest.lexerTest(input);
        System.out.println("IIIIIIIIIIIIIIIIIIII");
        System.out.println(tokenList);
    }*/
}
enum LexerTokens {
    INT_TYPE,             //1
    DOUBLE_TYPE,          //2
    BOOL_TYPE,            //3
    STRING_TYPE,          //4
    NULL_TYPE,            //5
    ARRAY_TYPE,           //6
    ACTOR_TYPE,           //7
    VOID_TYPE,            //8
    SPAWN,                //9
    STATE,                //10
    KNOWS,                //11
    ON_METHOD,            //12
    LOCAL_METHOD,         //13
    SEND_MSG,             //14
    SELF,                 //15
    IF,                   //16
    ELSE_IF,              //17
    ELSE,                 //18
    WHILE,                //19
    FOR,                  //20
    MAIN,                 //21
    STRICT_POS_INT,       //22
    INT,                  //23
    DOUBLE,               //24
    STRING,               //25
    BOOL_TRUE,            //26
    BOOL_FALSE,           //27
    IDENTIFIER,           //28
    COMMENT,              //29
    WS,                   //30
    ASSIGN,               //31
    COMMA,                //32
    CURLY_OPEN,           //33
    CURLY_CLOSE,          //34
    COLON,                //35
    SEMICOLON,            //36
    PARAN_OPEN,           //37
    PARAN_CLOSE,          //38
    SQUARE_OPEN,          //39
    SQUARE_CLOSE,         //40
    DOT,                  //41
    GREATER,              //42
    GREATER_OR_EQUAL,     //43
    LESSTHAN,             //44
    LESSTHAN_OR_EQUAL,    //45
    EQUAL,                //46
    NOTEQUAL,             //47
    LOGIC_NEGATION,       //48
    LOGIC_AND,            //49
    LOGIC_OR,             //50
    PLUS,                 //51
    MINUS,                //52
    MULTIPLY,             //53
    DIVIDE,               //54
    MODULUS,              //55
    DOUBLE_QUOTATION,     //56
    QUOTATION,            //57
    EOF                   //58
}


class ErrorListener extends BaseErrorListener{
    private final List<SyntaxError> syntaxErrors = new ArrayList<>();

    public ErrorListener() {
    }

    List<SyntaxError> getSyntaxErrors() {
        return syntaxErrors;
    }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg, RecognitionException e)
    {
        syntaxErrors.add(new SyntaxError(recognizer, offendingSymbol, line, charPositionInLine, msg, e));
    }

    @Override
    public String toString() {
        return Utils.join(syntaxErrors.iterator(), "\n");
    }
}

class SyntaxError
{
    private final Recognizer<?, ?> recognizer;
    private final Object offendingSymbol;
    private final int line;
    private final int charPositionInLine;
    private final String message;
    private final RecognitionException e;

    SyntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e)
    {
        this.recognizer = recognizer;
        this.offendingSymbol = offendingSymbol;
        this.line = line;
        this.charPositionInLine = charPositionInLine;
        this.message = msg;
        this.e = e;
    }

    public Recognizer<?, ?> getRecognizer()
    {
        return recognizer;
    }

    public Object getOffendingSymbol()
    {
        return offendingSymbol;
    }

    public int getLine()
    {
        return line;
    }

    public int getCharPositionInLine()
    {
        return charPositionInLine;
    }

    public String getMessage()
    {
        return message;
    }

    public RecognitionException getException()
    {
        return e;
    }
}