package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App.
 */
public class ParLangTest {
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
}
