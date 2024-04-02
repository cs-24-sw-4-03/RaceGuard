package org.abcd.examples.ParLang.symbols;

import java.util.*;

public class Scope {
    private String scopeName;
    private Scope parent;

    //The symbols in the scope
    //TODO: Research whether Hashmaps are the best for these lists
    private HashMap<String, Attributes> symbols = new HashMap<>();
    private Map<String, Attributes> params = new LinkedHashMap<>();

    //Nested scopes within the current scope
    private List<Scope> children = new ArrayList<>();

    public Scope(String scopeName, Scope parent){
        this.scopeName = scopeName;
        this.parent = parent;
    }

    public String getScopeName() {
        return this.scopeName;
    }

    public Scope getParent() {
        return this.parent;
    }

    public void addSymbol(String id, Attributes attribute){
        this.symbols.put(id, attribute);
    }
    public HashMap<String, Attributes> getSymbols() {
        return this.symbols;
    }

    public void addParams(String id, Attributes attribute) {
        this.params.put(id, attribute);
    }
    public Map<String, Attributes> getParams() {
        return this.params;
    }



    public void addChildToScope(Scope child){
        this.children.add(child);
    }
}
