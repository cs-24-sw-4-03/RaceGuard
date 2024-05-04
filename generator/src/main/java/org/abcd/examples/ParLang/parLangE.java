package org.abcd.examples.ParLang;

public enum parLangE {
    ON("on"),
    LOCAL("local"),
    ACTOR("Actor"),
    KNOWS("Knows"),
    STATE("State");

    private String string;

    private parLangE(String s){this.string=s;}

    public String getValue(){return string;}
}
