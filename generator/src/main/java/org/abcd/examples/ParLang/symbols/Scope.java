package org.abcd.examples.ParLang.symbols;

import java.util.*;

public class Scope {
    private String scopeName;
    private Scope parent;

    //The symbols in the scope
    private final HashMap<String, Attributes> symbols = new HashMap<>();
    private final Map<String, Attributes> params = new LinkedHashMap<>();
    private final HashMap<String, Attributes> stateSymbols = new HashMap<>();
    private final HashMap<String, Attributes> knowsSymbols = new HashMap<>();
    private final ArrayList<String> declaredLocalMethods = new ArrayList<>();
    private final ArrayList<String> declaredOnMethods = new ArrayList<>();



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

    public void addDeclaredLocalMethod(String id) {
        if(!this.declaredLocalMethods.contains(id)){
            this.declaredLocalMethods.add(id);
        }else{
            System.out.println("Duplicate local method id: " + id);
        }
    }

    public ArrayList<String> getDeclaredLocalMethods() {
        if(!this.declaredLocalMethods.isEmpty()){
            return this.declaredLocalMethods;
        }else{
            if(this.parent != null){
                return this.parent.getDeclaredLocalMethods();
            }else{
                return new ArrayList<>();
            }
        }
    }

    public void addDeclaredOnMethod(String id) {
        if(!this.declaredOnMethods.contains(id)){
            this.declaredOnMethods.add(id);
        }else{
            System.out.println("Duplicate on method id: " + id);
        }
    }

    public ArrayList<String> getDeclaredOnMethods() {
        if(!this.declaredOnMethods.isEmpty()){
            return this.declaredOnMethods;
        }else{
            if(this.parent != null){
                return this.parent.getDeclaredOnMethods();
            }else{
                return new ArrayList<>();
            }
        }
    }

}
