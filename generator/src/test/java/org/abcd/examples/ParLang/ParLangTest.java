package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.antlr.v4.runtime.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class ParLangTest {
    private static boolean mappingMade = false;
    private static Map<Integer, LexerTokens> tokenMap = new HashMap<>();

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
            tokenMap.put(8, LexerTokens.SPAWN);
            tokenMap.put(9, LexerTokens.STATE);
            tokenMap.put(10, LexerTokens.KNOWS);
            tokenMap.put(11, LexerTokens.ON_METHOD);
            tokenMap.put(12, LexerTokens.LOCAL_METHOD);
            tokenMap.put(13, LexerTokens.SEND_MSG);
            tokenMap.put(14, LexerTokens.SELF);
            tokenMap.put(15, LexerTokens.IF);
            tokenMap.put(16, LexerTokens.ELSE_IF);
            tokenMap.put(17, LexerTokens.ELSE);
            tokenMap.put(18, LexerTokens.WHILE);
            tokenMap.put(19, LexerTokens.FOR);
            tokenMap.put(20, LexerTokens.MAIN);
            tokenMap.put(21, LexerTokens.STRICT_POS_INT);
            tokenMap.put(22, LexerTokens.INT);
            tokenMap.put(23, LexerTokens.DOUBLE);
            tokenMap.put(24, LexerTokens.STRING);
            tokenMap.put(25, LexerTokens.BOOL_TRUE);
            tokenMap.put(26, LexerTokens.BOOL_FALSE);
            tokenMap.put(27, LexerTokens.IDENTIFIER);
            tokenMap.put(28, LexerTokens.COMMENT);
            tokenMap.put(29, LexerTokens.WS);
            tokenMap.put(30, LexerTokens.ASSIGN);
            tokenMap.put(31, LexerTokens.COMMA);
            tokenMap.put(32, LexerTokens.CURLY_OPEN);
            tokenMap.put(33, LexerTokens.CURLY_CLOSE);
            tokenMap.put(34, LexerTokens.COLON);
            tokenMap.put(35, LexerTokens.SEMICOLON);
            tokenMap.put(36, LexerTokens.PARAN_OPEN);
            tokenMap.put(37, LexerTokens.PARAN_CLOSE);
            tokenMap.put(38, LexerTokens.SQUARE_OPEN);
            tokenMap.put(39, LexerTokens.SQUARE_CLOSE);
            tokenMap.put(40, LexerTokens.DOT);
            tokenMap.put(41, LexerTokens.GREATER);
            tokenMap.put(42, LexerTokens.GREATER_OR_EQUAL);
            tokenMap.put(43, LexerTokens.LESSTHAN);
            tokenMap.put(44, LexerTokens.LESSTHAN_OR_EQUAL);
            tokenMap.put(45, LexerTokens.EQUAL);
            tokenMap.put(46, LexerTokens.NOTEQUAL);
            tokenMap.put(47, LexerTokens.LOGIC_NEGATION);
            tokenMap.put(48, LexerTokens.LOGIC_AND);
            tokenMap.put(49, LexerTokens.LOGIC_OR);
            tokenMap.put(50, LexerTokens.PLUS);
            tokenMap.put(51, LexerTokens.MINUS);
            tokenMap.put(52, LexerTokens.MULTIPLY);
            tokenMap.put(53, LexerTokens.DIVIDE);
            tokenMap.put(54, LexerTokens.MODULUS);
            tokenMap.put(55, LexerTokens.DOUBLE_QUOTATION);
            tokenMap.put(56, LexerTokens.QUOTATION);

            mappingMade = true;
        }
    }

    /**
     * Rigorous Test :-)
     */
    /*@Test
    public void shouldAnswerWithTrue() {
        assertTrue(true);
    }*/

    @Test
    public void testArithExpParsing() {
        String input = "main(){2 + 3 * 4;}";
        assertTrue(ParLang.parseTest(input));
    }

    @Test
    public void testWrongArithExpParsing() {
        String input = "main(){ + 3 * 4;}";
        assertFalse(ParLang.parseTest(input));
    }

    @Test
    public void testControlStructureParsing() {
        String input = "main(){if (x > 0) { x = x + 1; } else { x = x - 1; }}";
        assertTrue(ParLang.parseTest(input));
    }

    @Test
    public void testDeclarationParsing() {
        String input = "main(){int x = 10;}";
        assertTrue(ParLang.parseTest(input));
    }

    @Test
    public void testBooleanExpressionParsing() {
        String input = "main(){TRUE && (5 < 10) || FALSE;}";
        assertTrue(ParLang.parseTest(input));
    }

    @Test
    public void testWrongDeclarationParsing() {
        String input = "main(){s int x = 10;}";
        assertFalse(ParLang.parseTest(input));
    }

    @Test
    public void testLexerTypes() {
        String input = "int double bool string null [] Actor";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(8,tokenList.size());
        int counter = 1;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 7){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerActorSpec() {
        String input = "Spawn State Knows on local <- self";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(8,tokenList.size());
        int counter = 8;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 14){
                counter++;
            }
            else { //last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerControlStructPlusMain() {
        String input = "if else-if else while for main";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(7,tokenList.size());
        int counter = 15;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 20){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerprimitivesAndIdentifier() {
        String input = "5 -5 3.5 \"string\" TRUE FALSE identifier";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(8,tokenList.size());
        int counter = 21;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 27){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerCommentAndWhitespace() {
        String input = "//helle\n//        rgtlb&%234*^`^*^`:;,.,zvlb§@£$€546{[7978\n              ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be 1 as the lexer should skip comments and whitespace
        assertEquals(1,tokenList.size());
        assertEquals(tokenMap.get(57),tokenMap.get(tokenList.get(0).getType()));
    }

    @Test
    public void testLexerSingleSymbols() {
        String input = "= , { } : ; ( ) [ ] .";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(12,tokenList.size());
        int counter = 30;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 40){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerCompareOperators() {
        String input = "> >= < <= == !=";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(7,tokenList.size());
        int counter = 41;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 46){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerLogicOperators() {
        String input = "! && ||";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(4,tokenList.size());
        int counter = 47;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 49){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerArithOperators() {
        String input = "+ - * / %";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(6,tokenList.size());
        int counter = 50;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 54){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerQuotations() {
        String input = "\" ' ";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        //size should be one more than number of tokens as an input always will end on an End Of File (EOF) token
        assertEquals(3,tokenList.size());
        int counter =55;
        for(org.antlr.v4.runtime.Token token : tokenList){
            assertEquals(tokenMap.get(counter),tokenMap.get(token.getType()));
            if(counter != 56){
                counter++;
            }
            else {//last token is always EOF which is rule 57
                counter = 57;
            }
        }
    }

    @Test
    public void testLexerEOF() {
        String input = "";
        List<org.antlr.v4.runtime.Token> tokenList = ParLang.lexerTest(input);
        assertEquals(1,tokenList.size());
        assertEquals(tokenMap.get(57),tokenMap.get(tokenList.get(0).getType()));
    }
}
enum LexerTokens {
    INT_TYPE,             //1
    DOUBLE_TYPE,          //2
    BOOL_TYPE,            //3
    STRING_TYPE,          //4
    NULL_TYPE,            //5
    ARRAY_TYPE,           //6
    ACTOR_TYPE,           //7
    SPAWN,                //8
    STATE,                //9
    KNOWS,                //10
    ON_METHOD,            //11
    LOCAL_METHOD,         //12
    SEND_MSG,             //13
    SELF,                 //14
    IF,                   //15
    ELSE_IF,              //16
    ELSE,                 //17
    WHILE,                //18
    FOR,                  //19
    MAIN,                 //20
    STRICT_POS_INT,       //21
    INT,                  //22
    DOUBLE,               //23
    STRING,               //24
    BOOL_TRUE,            //25
    BOOL_FALSE,           //26
    IDENTIFIER,           //27
    COMMENT,              //28
    WS,                   //29
    ASSIGN,               //30
    COMMA,                //31
    CURLY_OPEN,           //32
    CURLY_CLOSE,          //33
    COLON,                //34
    SEMICOLON,            //35
    PARAN_OPEN,           //36
    PARAN_CLOSE,          //37
    SQUARE_OPEN,          //38
    SQUARE_CLOSE,         //39
    DOT,                  //40
    GREATER,              //41
    GREATER_OR_EQUAL,     //42
    LESSTHAN,             //43
    LESSTHAN_OR_EQUAL,    //44
    EQUAL,                //45
    NOTEQUAL,             //46
    LOGIC_NEGATION,       //47
    LOGIC_AND,            //48
    LOGIC_OR,             //49
    PLUS,                 //50
    MINUS,                //51
    MULTIPLY,             //52
    DIVIDE,               //53
    MODULUS,              //54
    DOUBLE_QUOTATION,     //55
    QUOTATION,            //56
    EOF                   //57
}

