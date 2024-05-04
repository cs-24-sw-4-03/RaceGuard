package org.abcd.examples.ParLang;

public enum javaE {
    CLASS("class "),
    PUBLIC("public "),
    PRIVATE("private "),
    STATIC("static "),
    FINAL("final "),
    IMPORT("import "),
    IMPLEMENTS("implements "),
    EXTENDS("extends "),
    ACTORREF("ActorRef ");

    private String string;
    private javaE(String s){
        this.string=s;
    }

    public String getValue(){return string;}
}
