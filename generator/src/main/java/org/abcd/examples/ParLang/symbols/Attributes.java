package org.abcd.examples.ParLang.symbols;

public class Attributes {

    private String variableType;
    private String scope;
    private String kind; // TODO: Should find another name for this. Describes whether it is a declaration, function, class or similar


    //C* sets scope after creation of object. TODO: Find out if there any benefit to this?
    public Attributes(String variableType, String scope, String kind){
        this.variableType = variableType;
        this.scope = scope;
        this.kind = kind;

    }

    public String getVariableType(){
        return this.variableType;
    }

    public String getScope(){
        return this.scope;
    }

    public String getKind(){
        return this.kind;
    }
}
