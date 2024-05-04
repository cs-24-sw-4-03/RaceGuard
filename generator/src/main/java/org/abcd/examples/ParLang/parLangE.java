package org.abcd.examples.ParLang;

public enum parLangE {
    ON("on"),
    LOCAL("local"),
    ACTOR("Actor");

    private String string;

    private parLangE(String s){this.string=s;}

    public String getValue(){return string;}
}
