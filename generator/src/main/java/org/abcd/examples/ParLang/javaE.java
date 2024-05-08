package org.abcd.examples.ParLang;

//Java and Akka keywords used for generating target code. Notice there is a space after each keyword
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
    ONRECEIVE("onReceive(Object message) "),
    INSTANCEOF("instanceof "),
    IF("if "),
    ELSEIF(" else if "),
    ELSE(" else "),
    FOR("for "),
    WHILE("while "),
    UNHANDLED("unhandled(message);"),
    CURLY_OPEN("{\n"),
    CURLY_CLOSE("\n}"),
    SEMICOLON(";\n"),
    ACTOR_SYSTEM_NAME("system"),
    THIS("this"),
    EQUALS(" = "),
    LONG("long"),
    STRING("String"),
    DOUBLE("double"),
    BOOLEAN("boolean"),
    TELL("tell"),
    NEW("new "),
    GET_SELF("getSelf()"),
    INLINE_COMMENT("//");





    private String string;
    private javaE(String s){
        this.string=s;
    }

    public String getValue(){return string;}
}
