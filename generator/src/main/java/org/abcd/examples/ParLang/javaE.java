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
    LONG("long "),
    LONG_ARRAY("long[] "),
    LONG_ARRAY_2D("long[][] "),
    STRING("String "),
    STRING_ARRAY("String[] "),
    STRING_ARRAY_2D("String[][] "),
    DOUBLE("double "),
    DOUBLE_ARRAY("double[] "),
    DOUBLE_ARRAY_2D("double[][] "),
    BOOLEAN("boolean "),
    BOOLEAN_ARRAY("boolean[] "),
    BOOLEAN_ARRAY_2D("boolean[][] "),
    OBJECT_ARRAY("Object[]"),
    TELL("tell"),
    NEW("new "),
    GET_SELF("getSelf()"),
    INLINE_COMMENT("//"),
    NO_SENDER("ActorRef.noSender()");





    private String string;
    private javaE(String s){
        this.string=s;
    }

    public String getValue(){return string;}
}
