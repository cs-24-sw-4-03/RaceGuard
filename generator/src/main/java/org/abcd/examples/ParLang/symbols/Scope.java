package org.abcd.examples.ParLang.symbols;

import java.util.*;

public class Scope {
    private String scopeName;
    private Scope parent;

    //The symbols in the scope
    private final HashMap<String, Attributes> symbols = new HashMap<>();
    private final LinkedHashMap<String, Attributes> params = new LinkedHashMap<>();
    private final HashMap<String, Attributes> stateSymbols = new HashMap<>();
    private final HashMap<String, Attributes> knowsSymbols = new HashMap<>();
    private final HashMap<String, Attributes> declaredLocalMethods = new HashMap<>();


    //Nested scopes within the current scope
    public final List<Scope> children = new ArrayList<>();

    public Scope(String scopeName){
        this.scopeName = scopeName;
    }

    public String getScopeName() {
        return this.scopeName;
    }

    public void setParent(Scope parent){
        this.parent = parent;
    }

    public Scope getParent() {
        return this.parent;
    }

    public void addSymbol(String id, Attributes attributes){
        this.symbols.put(id, attributes);
    }

    public HashMap<String, Attributes> getSymbols() {
        return this.symbols;
    }

    public void addParams(String id, Attributes attributes) {
        this.params.put(id, attributes);
    }

    public LinkedHashMap<String, Attributes> getParams() {
        return this.params;
    }

    public void addStateSymbols(String id, Attributes attributes) {
        this.stateSymbols.put(id, attributes);
    }

    public HashMap<String, Attributes> getStateSymbols() {
        return this.stateSymbols;
    }

    public void addKnowsSymbols(String id, Attributes attributes) {
        this.knowsSymbols.put(id, attributes);
    }

    public HashMap<String, Attributes> getKnowsSymbols() {
        return this.knowsSymbols;
    }

    public void addDeclaredLocalMethod(String id, Attributes attributes) {
        if(!this.declaredLocalMethods.containsKey(id)){
            this.declaredLocalMethods.put(id, attributes);
        }else{
            System.out.println("Duplicate method id: " + id);
        }
    }

    public HashMap<String, Attributes> getDeclaredLocalMethods() {
        if(!this.declaredLocalMethods.isEmpty()){
            return this.declaredLocalMethods;
        }else{
            if(this.parent != null){
                return this.parent.getDeclaredLocalMethods();
            }else{
                return new HashMap<>();
            }
        }
    }
}
