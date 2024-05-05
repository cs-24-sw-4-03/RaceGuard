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
    ACTORREF("ActorRef "),
    RETURN("return "),
    VOID("void "),
    ONRECEIVE("onReceive(Object message) ");

    private String string;
    private javaE(String s){
        this.string=s;
    }

    public String getValue(){return string;}
}
