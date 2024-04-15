package org.abcd.examples.ParLang.symbols;

import java.util.*;

public class Scope {
    private String scopeName;
    private Scope parent;

    //The symbols in the scope
    //TODO: Research whether Hashmaps are the best for these lists
    private final HashMap<String, Attributes> symbols = new HashMap<>();
    private final Map<String, Attributes> params = new LinkedHashMap<>();
    private final HashMap<String, Attributes> stateSymbols = new HashMap<>();
    private final HashMap<String, Attributes> knowsSymbols = new HashMap<>();


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

    public Map<String, Attributes> getParams() {
        return this.params;
    }

    public void addStateSymbols(String id, Attributes attributes) {
        this.stateSymbols.put(id, attributes);
    }

    public Map<String, Attributes> getStateSymbols() {
        return this.stateSymbols;
    }

    public void addKnowsSymbols(String id, Attributes attributes) {
        this.knowsSymbols.put(id, attributes);
    }

    public Map<String, Attributes> getKnowsSymbols() {
        return this.knowsSymbols;
    }
}
