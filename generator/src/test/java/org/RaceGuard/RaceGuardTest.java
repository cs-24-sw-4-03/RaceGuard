package org.RaceGuard;

// import ANTLR's runtime libraries
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit test for simple App.
 */

public class RaceGuardTest {

    public static boolean parseTest(String realInput){
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(realInput);

        // create a lexer that feeds off of input CharStream
        RaceGuardLexer lexer = new RaceGuardLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // create a parser that feeds off the tokens buffer
        RaceGuardParser parser = new RaceGuardParser(tokens);

        parser.removeErrorListeners();
        ErrorListener errListen = new ErrorListener();
        parser.addErrorListener(errListen);

        ParseTree tree = parser.init(); // begin parsing at init rule

        List<SyntaxError> errors = errListen.getSyntaxErrors();
        if (!errors.isEmpty()){return false;}
        return true;
    }
    public static List<org.antlr.v4.runtime.Token> lexerTest(String realInput){
        // create a CharStream that reads from standard input
        ANTLRInputStream input = new ANTLRInputStream(realInput);

        // create a lexer that feeds off of input CharStream
        RaceGuardLexer lexer = new RaceGuardLexer(input);

        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        //filling in the tokens
        tokens.fill();

        //create list of tokens to return
        return tokens.getTokens();
    }

//PARSER TEST------------------------------------------------------------------------------------------------
    @Test
    public void testArithExpParsing() {
        String input = "main(){int i = 2 + 3 * 4;}";
        assertTrue(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testWrongArithExpParsing() {
        String input = "main(){ 5 + 3 * 4;}";
        assertFalse(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testControlStructureParsing() {
        String input = "main(){if (x > 0) { x = x + 1; } else { x = x - 1; }}";
        assertTrue(RaceGuardTest.parseTest(input));
    }
    @Test
    public void testWrongControlStructureParsing() {
        String input = "main(){(x > 0) { x = x + 1; } else { x = x - 1; }}";
        assertFalse(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testDeclarationParsing() {
        String input = "main(){int x = 10;}";
        assertTrue(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testWrongDeclarationNoType() {
        String input = "main(){x = 10;}";
        assertTrue(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testWrongDeclarationParsing() {
        String input = "main(){s int x = 10;}";
        assertFalse(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testBooleanExpressionParsing() {
        String input = "main(){bool x = TRUE && (5 < 10) || FALSE;}";
        assertTrue(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testWrongBooleanExpressionParsing() {
        String input = "main(){ && (5 < 10) || FALSE;}";
        assertFalse(RaceGuardTest.parseTest(input));
    }

    @Test
    public void testNoInput() {
        String input = "";
        assertFalse(RaceGuardTest.parseTest(input));
    }
    //-------------------------------------------------------------------------------------------------------


    //LEXER TEST--------------------------------------------------------------------------------------------
    @Test
    public void testLexerTypes() {
        String input = "int double bool string [] Actor void";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(8,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerActorSpec() {
        String input = "Spawn State Knows on local <- self follows KILL";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(10,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerControlStructPlusMain() {
        String input = "if else while for main return print TRUE FALSE";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(10,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerprimitivesAndIdentifier() {
        String input = "5 3.5 \"string\" identifier";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(5,tokenList.size());
        tokenList.removeLast();
        int counter = 0;
        for(org.antlr.v4.runtime.Token token : tokenList){
            switch (counter){
                case 0:
                    assertEquals(RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()),"INT");
                    break;
                case 1: {
                    assertEquals(RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()),"DOUBLE");
                    break;
                }
                case 2: {
                    assertEquals(RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()),"STRING");
                    break;
                }
                case 3: {
                    assertEquals(RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()),"IDENTIFIER");
                    break;
                }
            }
            counter++;
        }
    }

    @Test
    public void testLexerCommentAndWhitespace() {
        String input = "//hee\n//        rgtlb&%234*^`^*^`:;,.,zvlb§@£$€546{[7978\n              ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be 1 as the lexer should skip comments and whitespace
        assertEquals(1,tokenList.size());
        //the only token should be EOF
        org.antlr.v4.runtime.Token token = tokenList.get(0);
        assertEquals(token.getText().replaceAll("<","").replaceAll(">",""), RaceGuardLexer.VOCABULARY.getSymbolicName(token.getType()));
    }

    @Test
    public void testLexerSingleSymbols() {
        String input = "= , { } : ; ( ) [ ] .";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(12,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerCompareOperators() {
        String input = "> >= < <= == !=";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(7,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerLogicOperators() {
        String input = "! && ||";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(4,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerArithOperators() {
        String input = "+ - * / %";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(6,tokenList.size());
        tokenList.removeLast();
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
        }
    }

    @Test
    public void testLexerSingleQuote() {
        String input = " \' ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(2,tokenList.size());
        org.antlr.v4.runtime.Token token = tokenList.get(0);
        assertEquals(token.getText(),RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceFirst("''", ""));
    }

    @Test
    public void testLexerDoubleQuote() {
        String input = "\" ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(2,tokenList.size());
        org.antlr.v4.runtime.Token token = tokenList.get(0);
        assertEquals("DOUBLE_QUOTATION",RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
    }

    @Test
    public void testLexerEOF() {
        String input = "";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        assertEquals(1,tokenList.size());
        org.antlr.v4.runtime.Token token = tokenList.get(0);
        assertEquals(token.getText().replaceAll("<","").replaceAll(">",""), RaceGuardLexer.VOCABULARY.getSymbolicName(token.getType()));
    }

    @Test
    public void testStringSingleQuote() {
        String input = " 'hello world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        assertEquals(2,tokenList.size());
        org.antlr.v4.runtime.Token token = tokenList.get(0);
        assertEquals("STRING",RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
    }

    @Test
    public void testStringDoubleQuote() {
        String input = " \"hello world!\" ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        assertEquals(2,tokenList.size());
        org.antlr.v4.runtime.Token token = tokenList.get(0);
        assertEquals("STRING",RaceGuardLexer.VOCABULARY.getDisplayName(token.getType()).replaceAll("'", ""));
    }

    @Test
    public void testStringEscape1() {
        String input = " 'hello \n world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        assertEquals(6,tokenList.size()); //strings cannot contain \n and will then force lexer to tokenize otherwise
    }
    @Test
    public void testStringEscape2() {
        String input = " 'hello \t world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        assertEquals(6,tokenList.size()); //strings cannot contain \t and will then force lexer to tokenize otherwise
    }
    @Test
    public void testStringEscape3() {
        String input = " 'hello \r world!' ";
        List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        assertEquals(6,tokenList.size()); //strings cannot contain \r and will then force lexer to tokenize otherwise
    }

    @Test
    public void testUnknownToken() { //lexer does not handle unknown tokens well yet
        String input = " @ ";
        try {
            List<org.antlr.v4.runtime.Token> tokenList = RaceGuardTest.lexerTest(input);
        } catch (Exception e) {
            System.out.println(e.getMessage() + "--------------");
            assertTrue(e instanceof NullPointerException);
        }
    }
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

class SyntaxError {
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
}
