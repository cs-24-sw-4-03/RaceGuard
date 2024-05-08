package org.abcd.examples.ParLang.symbols;

public class Attributes {

    private String variableType;
    private String scope;

    public Attributes(String variableType){
        this.variableType = variableType;
    }

    public String getVariableType(){
        return this.variableType;
    }

    public void setScope(String scope){
        this.scope = scope;
    }

    public String getScope(){
        return this.scope;
    }
}
