package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.Scope;
import org.abcd.examples.ParLang.symbols.SymbolTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolTableTests {

    private SymbolTable symbolTable;

    private static Scope scope1 = new Scope("1");
    private static Scope scope2 = new Scope("2");
    private static Scope scope3 = new Scope("3");
    private static Scope scope4 = new Scope("4");
    private static Scope scope5 = new Scope("5");
    private static Scope scope6 = new Scope("6");

    Attributes x;
    Attributes x2;
    Attributes y;
    Attributes yK;
    Attributes yK2;
    Attributes z;
    Attributes zS;
    Attributes zS2;


    @BeforeEach
    public void setUp(){
        this.symbolTable = new SymbolTable();
        scope1.setParent(symbolTable.getCurrentScope());
        scope2.setParent(symbolTable.getCurrentScope());
        scope3.setParent(scope1);
        scope4.setParent(scope1);
        scope5.setParent(scope2);
        scope6.setParent(scope2);

        symbolTable.getCurrentScope().children.add(scope1);
        symbolTable.getCurrentScope().children.add(scope2);
        scope1.children.add(scope3);
        scope1.children.add(scope4);
        scope2.children.add(scope5);
        scope2.children.add(scope6);

        x = new Attributes("int");
        x2 = new Attributes("double");

        y = new Attributes("string");
        yK = new Attributes("double");
        yK2 = new Attributes("int");


        z = new Attributes("double");
        zS = new Attributes("int");
        zS2 = new Attributes("string");


        scope1.addSymbol("x", x);
        scope1.addSymbol("y", y);
        scope1.addSymbol("z", z);

        scope1.addStateSymbols("z", zS);
        scope1.addKnowsSymbols("y", yK);


        scope4.addSymbol("x", x2);
        scope4.addStateSymbols("z", zS2);
        scope4.addKnowsSymbols("y", yK2);


    }


    //lookUpScope Tests:

    @Test
    public void lookUpScopeTestFound(){
        Scope foundScope = symbolTable.lookUpScope("6");
        assertSame(foundScope, scope6);
    }

    @Test
    public void lookUpScopeTestNotFound(){
        Scope foundScope = symbolTable.lookUpScope("7");
        assertNull(foundScope);
    }


    //addScope Tests:

    @Test
    public void addScopeTestCreated(){
        Scope currentScope = symbolTable.getCurrentScope();
        assertTrue(symbolTable.addScope("7"));
        assertNotEquals(symbolTable.getCurrentScope(),currentScope);
    }

    @Test
    public void addScopeTestAlreadyExists(){
        Scope currentScope = symbolTable.getCurrentScope();
        assertFalse(symbolTable.addScope("6"));
        assertSame(currentScope, symbolTable.getCurrentScope());
    }

    //leaveScope Tests:

    @Test
    public void leaveScopeTestGlobal(){
        Scope currentScope = symbolTable.getCurrentScope();
        symbolTable.leaveScope();
        assertSame(currentScope, symbolTable.getCurrentScope());
    }

    @Test
    public void leaveScopeTestLeft(){
        Scope currentScope = symbolTable.getCurrentScope();
        symbolTable.addScope("7");
        assertNotEquals(symbolTable.getCurrentScope(), currentScope);
        symbolTable.leaveScope();
        assertSame(currentScope, symbolTable.getCurrentScope());
    }


    //enterScope Tests:

    @Test
    public void enterScopeTestEntered(){
        Scope currentScope = symbolTable.getCurrentScope();
        assertTrue(symbolTable.enterScope("4"));
        assertSame(symbolTable.getCurrentScope(), scope4);
        assertNotEquals(symbolTable.getCurrentScope(), currentScope);
    }

    @Test
    public void enterScopeTestNotEntered(){
        Scope currentScope = symbolTable.getCurrentScope();
        assertFalse(symbolTable.enterScope("7"));
        assertSame(symbolTable.getCurrentScope(), currentScope);
    }


    //lookUpSymbol Tests:

    @Test
    public void lookUpSymbolTestFoundScope1(){
        symbolTable.enterScope("1");
        Attributes xScope1 = symbolTable.lookUpSymbol("x");
        assertSame(x, xScope1);

        Attributes zScope1 = symbolTable.lookUpSymbol("z");
        assertSame(z, zScope1);

        Attributes yScope1 = symbolTable.lookUpSymbol("y");
        assertSame(y, yScope1);
    }


    @Test
    public void lookUpSymbolTestFoundScope3(){
        symbolTable.enterScope("3");
        Attributes xScope3 = symbolTable.lookUpSymbol("x");
        assertSame(x, xScope3);

        Attributes zScope3 = symbolTable.lookUpSymbol("z");
        assertSame(z, zScope3);

        Attributes yScope3 = symbolTable.lookUpSymbol("y");
        assertSame(y, yScope3);
    }

    @Test
    public void lookUpSymbolTestFoundScope4(){
        symbolTable.enterScope("4");
        Attributes xScope4 = symbolTable.lookUpSymbol("x");
        assertSame(x2, xScope4);

        Attributes zScope4 = symbolTable.lookUpSymbol("z");
        assertSame(z, zScope4);

        Attributes yScope4 = symbolTable.lookUpSymbol("y");
        assertSame(y, yScope4);
    }

    @Test
    public void lookUpSymbolTestNotFoundScope5(){
        symbolTable.enterScope("5");
        Attributes xScope5 = symbolTable.lookUpSymbol("x");
        assertNull(xScope5);

        Attributes zScope5 = symbolTable.lookUpSymbol("z");
        assertNull(zScope5);

        Attributes yScope5 = symbolTable.lookUpSymbol("y");
        assertNull(yScope5);
    }


    //lookUpCurrentScope Tests:

    @Test
    public void lookUpSymbolCurrentScopeTestFoundScope1(){
        symbolTable.enterScope("1");
        Attributes xScope1 = symbolTable.lookUpSymbolCurrentScope("x");
        assertSame(x, xScope1);

        Attributes zScope1 = symbolTable.lookUpSymbolCurrentScope("z");
        assertSame(z, zScope1);

        Attributes yScope1 = symbolTable.lookUpSymbolCurrentScope("y");
        assertSame(y, yScope1);
    }


    @Test
    public void lookUpSymbolCurrentScopeTestFoundScope3(){
        symbolTable.enterScope("3");
        Attributes xScope3 = symbolTable.lookUpSymbolCurrentScope("x");
        assertNull(xScope3);

        Attributes zScope3 = symbolTable.lookUpSymbolCurrentScope("z");
        assertNull(zScope3);

        Attributes yScope3 = symbolTable.lookUpSymbolCurrentScope("y");
        assertNull(yScope3);
    }

    @Test
    public void lookUpSymbolCurrentScopeTestFoundScope4(){
        symbolTable.enterScope("4");
        Attributes xScope4 = symbolTable.lookUpSymbolCurrentScope("x");
        assertSame(x2, xScope4);

        Attributes zScope4 = symbolTable.lookUpSymbolCurrentScope("z");
        assertNull(zScope4);

        Attributes yScope4 = symbolTable.lookUpSymbolCurrentScope("y");
        assertNull(yScope4);
    }

    //lookUpStateSymbol Tests:

    @Test
    public void lookUpStateSymbolTestFoundScope1(){
        symbolTable.enterScope("1");
        Attributes zScope1 = symbolTable.lookUpStateSymbol("z");
        assertSame(zS, zScope1);

    }


    @Test
    public void lookUpStateSymbolTestFoundScope3(){
        symbolTable.enterScope("3");
        Attributes zScope3 = symbolTable.lookUpStateSymbol("z");
        assertSame(zS, zScope3);

    }

    @Test
    public void lookUpStateSymbolTestFoundScope4(){
        symbolTable.enterScope("4");
        Attributes zScope4 = symbolTable.lookUpStateSymbol("z");
        assertSame(zS2, zScope4);
    }

    @Test
    public void lookUpStateSymbolTestNotFoundScope5(){
        symbolTable.enterScope("5");
        Attributes zScope5 = symbolTable.lookUpStateSymbol("z");
        assertNull(zScope5);
    }

    //lookUpKnowsSymbol Tests:

    @Test
    public void lookUpKnowsSymbolTestFoundScope1(){
        symbolTable.enterScope("1");
        Attributes yScope1 = symbolTable.lookUpKnowsSymbol("y");
        assertSame(yK, yScope1);

    }


    @Test
    public void lookUpKnowsSymbolTestFoundScope3(){
        symbolTable.enterScope("3");
        Attributes yScope3 = symbolTable.lookUpKnowsSymbol("y");
        assertSame(yK, yScope3);

    }

    @Test
    public void lookUpKnowsSymbolTestFoundScope4(){
        symbolTable.enterScope("4");
        Attributes yScope4 = symbolTable.lookUpKnowsSymbol("y");
        assertSame(yK2, yScope4);
    }

    @Test
    public void lookUpKnowsSymbolTestNotFoundScope5(){
        symbolTable.enterScope("5");
        Attributes yScope5 = symbolTable.lookUpKnowsSymbol("y");
        assertNull(yScope5);
    }


}
