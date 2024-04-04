package org.abcd.examples.ParLang.symbols;

import java.util.Stack;

public class SymbolTable {

    private Scope currentScope;
    final private Scope globalScope;
    final private Stack<Scope> scopeStack = new Stack<>();


    public SymbolTable() {
        this.globalScope = new Scope("global");
        currentScope = this.globalScope;
    }

    public Scope getCurrentScope(){
        return this.currentScope;
    }

    public void addScope(String scopeName){
        if(lookUpScope(scopeName) == null){
            Scope scope = new Scope(scopeName);
            scope.setParent(this.currentScope);
            this.currentScope.children.add(scope);
            this.scopeStack.push(this.currentScope);
            this.currentScope = scope;
        }
    }

    //Sets
    public void leaveScope(){
        if(!this.scopeStack.empty()){
            this.currentScope = this.scopeStack.pop();
        } else{
            this.currentScope = this.globalScope;
        }
    }

    //TODO: Find better name than searchScope
    public Scope findScope(String scopeName, Scope searchScope){
        if (this.globalScope.getScopeName().equals(scopeName)){
            return this.globalScope;
        }

        Scope scope = null;

        for(Scope childScope: searchScope.children){
            scope = findScope(scopeName, childScope);

            if(scope != null){
                break;
            }
        }

        return scope;
    }

    public Scope lookUpScope(String scopeName){
        return findScope(scopeName, this.globalScope);
    }

    public Attributes lookUpSymbol(){

    }

    public void insertSymbol(){

    }

    public void insertParams(){

    }

}
