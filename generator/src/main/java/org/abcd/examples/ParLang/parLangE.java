package org.abcd.examples.ParLang;

//Used to evaluate strings in the source code. Changing these will require changes in CodeGenVisitor.
public enum parLangE {
    ON("on"),
    LOCAL("local"),
    ACTOR("Actor"),
    KNOWS("Knows"),
    STATE("State"),
    SELF("self"),
    VOID("void"),
    STRING("string"),
    INT("int"),
    DOUBLE("double");

    private String string;

    private parLangE(String s){this.string=s;}

    public String getValue(){return string;}
}
