package org.abcd.examples.ParLang;

//Used to evaluate strings in the source code. Changing these will require changes in CodeGenVisitor.
public enum parLangE {
    ON("on"),
    LOCAL("local"),
    ACTOR("Actor"),
    SCRIPT("Script"),
    KNOWS("Knows"),
    STATE("State"),
    SELF("self"),
    VOID("void"),
    STRING("string"),
    INT("int"),
    INT_ARRAY("int[]"),
    INT_ARRAY_2D("int[][]"),
    DOUBLE("double"),
    DOUBLE_ARRAY("double[]"),
    DOUBLE_ARRAY_2D("double[][]"),
    STRING_ARRAY("string[]"),
    STRING_ARRAY_2D("string[][]"),
    BOOL("bool"),
    BOOL_ARRAY("bool[]"),
    BOOL_ARRAY_2D("bool[][]"),
    SEND("<-"),
    REAPER("Reaper"),
    MAIN("Main");

    private String string;

    private parLangE(String s){this.string=s;}

    public String getValue(){return string;}
}
