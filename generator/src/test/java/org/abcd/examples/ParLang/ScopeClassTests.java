package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.Scope;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ScopeClassTests {

    private static final Scope scope1 = new Scope("1");
    private static final Scope scope2 = new Scope("2");
    private static final Scope scope3 = new Scope("3");
    private static final Scope scope4 = new Scope("4");
    private static final Scope scope5 = new Scope("5");


    @BeforeAll
    public static void setUp() {
        scope5.setParent(scope4);
        scope4.setParent(scope3);
        scope3.setParent(scope2);
        scope2.setParent(scope1);

        Attributes localAttributes2 = new Attributes("int");
        scope2.addDeclaredLocalMethod("localTest2", localAttributes2);

        Attributes localAttributes4 = new Attributes("int");
        scope4.addDeclaredLocalMethod("localTest4", localAttributes4);


        Attributes onAttributes2 = new Attributes("int");
        scope2.addDeclaredOnMethod("onTest2", onAttributes2);

        Attributes onAttributes4 = new Attributes("int");
        scope4.addDeclaredOnMethod("onTest4", onAttributes4);
    }


    @Test
    public void getDeclaredLocalMethodsTestScope1() {
        HashMap<String, Attributes> scopeDeclaredLocalMethods = scope1.getDeclaredLocalMethods();
        assertFalse(scopeDeclaredLocalMethods.containsKey("localTest2"));
        assertFalse(scopeDeclaredLocalMethods.containsKey("localTest4"));
    }

    @Test
    public void getDeclaredLocalMethodsTestScope2() {
        HashMap<String, Attributes> scopeDeclaredLocalMethods = scope2.getDeclaredLocalMethods();
        assertTrue(scopeDeclaredLocalMethods.containsKey("localTest2"));
        assertFalse(scopeDeclaredLocalMethods.containsKey("localTest4"));
    }

    @Test
    public void getDeclaredLocalMethodsTestScope3() {
        HashMap<String, Attributes> scopeDeclaredLocalMethods = scope3.getDeclaredLocalMethods();
        assertTrue(scopeDeclaredLocalMethods.containsKey("localTest2"));
        assertFalse(scopeDeclaredLocalMethods.containsKey("localTest4"));
    }

    @Test
    public void getDeclaredLocalMethodsTestScope4() {
        HashMap<String, Attributes> scopeDeclaredLocalMethods = scope4.getDeclaredLocalMethods();
        assertFalse(scopeDeclaredLocalMethods.containsKey("localTest2"));
        assertTrue(scopeDeclaredLocalMethods.containsKey("localTest4"));
    }

    @Test
    public void getDeclaredLocalMethodsTestScope5() {
        HashMap<String, Attributes> scopeDeclaredLocalMethods = scope5.getDeclaredLocalMethods();
        assertFalse(scopeDeclaredLocalMethods.containsKey("localTest2"));
        assertTrue(scopeDeclaredLocalMethods.containsKey("localTest4"));
    }


//------------------------------------------------------------------------------------------------


    @Test
    public void getDeclaredOnMethodsTestScope1() {
        HashMap<String, Attributes> scopeDeclaredOnMethods = scope1.getDeclaredOnMethods();
        assertFalse(scopeDeclaredOnMethods.containsKey("onTest2"));
        assertFalse(scopeDeclaredOnMethods.containsKey("onTest4"));
    }

    @Test
    public void getDeclaredOnMethodsTestScope2() {
        HashMap<String, Attributes> scopeDeclaredOnMethods = scope2.getDeclaredOnMethods();
        assertTrue(scopeDeclaredOnMethods.containsKey("onTest2"));
        assertFalse(scopeDeclaredOnMethods.containsKey("onTest4"));
    }

    @Test
    public void getDeclaredOnMethodsTestScope3() {
        HashMap<String, Attributes> scopeDeclaredOnMethods = scope3.getDeclaredOnMethods();
        assertTrue(scopeDeclaredOnMethods.containsKey("onTest2"));
        assertFalse(scopeDeclaredOnMethods.containsKey("onTest4"));
    }

    @Test
    public void getDeclaredOnMethodsTestScope4() {
        HashMap<String, Attributes> scopeDeclaredOnMethods = scope4.getDeclaredOnMethods();
        assertFalse(scopeDeclaredOnMethods.containsKey("onTest2"));
        assertTrue(scopeDeclaredOnMethods.containsKey("onTest4"));
    }

    @Test
    public void getDeclaredOnMethodsTestScope5() {
        HashMap<String, Attributes> scopeDeclaredOnMethods = scope5.getDeclaredOnMethods();
        assertFalse(scopeDeclaredOnMethods.containsKey("onTest2"));
        assertTrue(scopeDeclaredOnMethods.containsKey("onTest4"));
    }
}
