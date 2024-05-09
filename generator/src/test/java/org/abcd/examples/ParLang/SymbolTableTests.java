package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.symbols.Scope;
import org.abcd.examples.ParLang.symbols.SymbolTable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SymbolTableTests {

    private static SymbolTable symbolTable = new SymbolTable();

    private static Scope scope1 = new Scope("1");
    private static Scope scope2 = new Scope("2");
    private static Scope scope3 = new Scope("3");
    private static Scope scope4 = new Scope("4");
    private static Scope scope5 = new Scope("5");
    private static Scope scope6 = new Scope("6");

    /*
     *   Possible unit test candidates:
     *   SymbolTable:
     *   findActorParent
     *   addScope
     *   leaveScope
     *   enterScope
     *   lookUpSymbolCurrentScope
     *   lookUpSymbol
     *   lookUpStateSymbol
     *   lookUpKnowsSymbol
     */



    @BeforeAll
    public static void setUp(){
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
    }


    //lookUpScope Tests:

    @Test
    public void testLookUpScopeFound(){
        Scope foundScope = symbolTable.lookUpScope("6");
        assertSame(foundScope, scope6);
    }

    @Test
    public void testLookUpScopeNotFound(){
        Scope foundScope = symbolTable.lookUpScope("7");
        assertNull(foundScope);
    }


    //addScope Tests:


}
