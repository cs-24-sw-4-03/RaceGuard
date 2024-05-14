package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.*;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.Scope;
import org.abcd.examples.ParLang.symbols.SymbolTable;


import java.util.*;

public class TypeVisitor implements NodeVisitor {
    private SymbolTable symbolTable;
    private TypeContainer typeContainer;
    private List<RuntimeException> exceptions = new ArrayList<>();

    public List<RuntimeException> getExceptions() {
        return exceptions;
    }

    public TypeVisitor(SymbolTable symbolTable, TypeContainer typeContainer) {
        this.symbolTable = symbolTable;
        this.typeContainer = typeContainer;
    }

    private String arithTypeConvert(String type1, String type2){
        if (type1.equals(type2)){ //If the types are the same, return that type
            return type1;
        }
        if (type1.equals(parLangE.INT.getValue()) && type2.equals(parLangE.DOUBLE.getValue())){
            return parLangE.DOUBLE.getValue(); //if types are int and double, return double
        }
        if (type1.equals(parLangE.DOUBLE.getValue()) && type2.equals(parLangE.INT.getValue())){
            return parLangE.DOUBLE.getValue(); //if types are double and int, return double
        }
        return null;
    }

    private boolean canConvert(String assignTo, String assignFrom){
        if (assignTo.equals(assignFrom)){ //If the types are the same, return true
            return true;
        }
        if (assignTo.equals(parLangE.DOUBLE.getValue()) && assignFrom.equals(parLangE.INT.getValue())){
            return true; //if types are int and double, return true
        }
        if (assignTo.equals(parLangE.DOUBLE_ARRAY.getValue()) && assignFrom.equals(parLangE.INT_ARRAY.getValue())){
            return true; //if types are int[] and double[], return true
        }
        if (assignTo.equals(parLangE.DOUBLE_ARRAY_2D.getValue()) && assignFrom.equals(parLangE.INT_ARRAY_2D.getValue())){
            return true; //if types are int[][] and double[][], return true
        }
        if (symbolTable.declaredScripts.contains(assignTo)){ //If the assignTo is a script, check if the assignFrom is an actor following the script
            symbolTable.enterScope(assignTo); //Enter the scope of the script
            ArrayList<String> folowingActors = symbolTable.getActorsFollowingScript();
            if (folowingActors.contains(assignFrom)){
                symbolTable.leaveScope(); //Leave the scope of the script
                return true;
            }
            symbolTable.leaveScope(); //Leave the scope of the script
        }
        return false;
    }

    private Attributes accessType(AstNode node){
        Attributes attributes = null; //The attributes are used to get the correct type
        if (node instanceof StateAccessNode){
            String id = ((StateAccessNode) node).getAccessIdentifier();
            attributes = symbolTable.lookUpStateSymbol(id); //Look up the state symbol in the symbol table
        }
        if (node instanceof KnowsAccessNode){
            String id = ((KnowsAccessNode) node).getAccessIdentifier();
            attributes = symbolTable.lookUpKnowsSymbol(id); //Look up the knows symbol in the symbol table
        }
        if (attributes != null){ //If the attributes are not null, return them
            return attributes;
        }
        else { //If the attributes are null, throw an exception
            throw new AccessTypeException("Access type not found for node: " + node.getClass().getName() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
        }
    }

    @Override
    public void visitChildren(AstNode node){
        for (AstNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    @Override
    public void visit(ScriptDclNode node) {
        try {
            this.symbolTable.enterScope(node.getId());
            this.visitChildren(node);
            if (node.getId() == null) { //If the id is null, throw an exception (node not created correctly)
                throw new ScriptDclException("Type is not defined for script declaration node " + node.getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(node.getId());
            this.symbolTable.leaveScope();
        }
        catch (ScriptDclException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ScriptDclException(e.getMessage() + " in ScriptDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ScriptMethodNode node) {
        try {
        symbolTable.enterScope(node.getId() + symbolTable.findActorParent(node));
        this.visitChildren(node);
            if (node.getType() == null) { //If the type is null, throw an exception (node not created correctly)
                throw new ScriptMethodException("Type is not defined for script method node " + node.getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            if (node.getMethodType() == null) { //If the method type is null, throw an exception (node not created correctly)
                throw new ScriptMethodException("(on/local) Method type is not defined for script method node " + node.getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        symbolTable.leaveScope();
        }
        catch (ScriptMethodException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ScriptMethodException(e.getMessage() + " in ScriptMethodNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(SendMsgNode node) {
        try {
            this.visitChildren(node);
        }
        catch (MethodCallException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodCallException(e.getMessage() + " in SendMsgNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(InitNode node) {
        //does not need types
        this.visitChildren(node);
    }

    @Override
    public void visit(BodyNode node) {
        //does not need types
        this.visitChildren(node);
    }

    @Override
    public void visit(IdentifierNode node) {
        try {
            if((node.getParent() instanceof MethodCallNode)) {
                String Name = node.getName();
                Attributes attributes = symbolTable.getDeclaredLocalMethods().get(Name); //Get the attributes of the method
                node.setType(attributes.getVariableType()); //Set the type of the node to the type of the method
            }
            else if ((node.getParent() instanceof SendMsgNode)){
                String id = node.getName();
                Attributes attributes = symbolTable.lookUpSymbol(id); //Look up the symbol in the symbol table
                node.setType(attributes.getVariableType()); //Set the type of the node to the type of the symbol
            }
            else {
                if (hasParent(node, StateNode.class)) { //If the parent is a StateNode lookup in stateSymbols
                    node.setType(this.symbolTable.lookUpStateSymbol(node.getName()).getVariableType());
                }
                else if (hasParent(node, KnowsNode.class)) { //If the parent is a KnowsNode lookup in knowsSymbols
                    node.setType(this.symbolTable.lookUpKnowsSymbol(node.getName()).getVariableType());
                }
                else if (hasParent(node, FollowsNode.class)) { //If the parent is a FollowsNode simply set type
                    node.setType(node.getName());
                }
                else {
                    //If the parent is not a StateNode, KnowsNode, or followsNode lookup in the symbol table
                    node.setType(this.symbolTable.lookUpSymbol(node.getName()).getVariableType());
                }
            }
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in IdentifierNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }



    @Override
    public void visit(ParametersNode node) {
        try {
            this.visitChildren(node);
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ParametersNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
        try {
        AstNode parent = node.getParent();
        while (!(parent instanceof MethodDclNode)){ //Find the parent MethodDclNode
            parent = parent.getParent();
        } //returnStatementNode  should have a parent of type MethodDclNode due to lexer and parser rules
        if(!parent.getType().equals(parLangE.VOID.getValue())) {
            if (!node.getChildren().isEmpty()) {
                String returnType = node.getChildren().getFirst().getType();
                node.setType(returnType);
            }else {
                throw new ReturnNodeException("Type is not defined for return statement in method "+ ((MethodDclNode)parent).getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }else if(((MethodDclNode) parent).getMethodType().equals("on")){
            throw new ReturnNodeException("on method cannot return: " + ((MethodDclNode)parent).getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
        } else{
            if(node.getChildren().isEmpty()){ //since the the return type is void, the return statement should be empty
                node.setType(parLangE.VOID.getValue());
            }else {
                throw new ReturnNodeException("return type is not void for void-returning local method: " + ((MethodDclNode)parent).getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }
        }
        catch (ReturnNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ReturnNodeException(e.getMessage() + " in ReturnStatementNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(SpawnActorNode node) {
        try {
            this.visitChildren(node);
            if (node.getType() == null) {
                throw new SpawnActorException("Type is not defined for spawn actor node " + node.getLineNumber() + ":" + node.getColumnNumber() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            if (symbolTable.declaredScripts.contains(node.getType())) {
                throw new SpawnActorException("scripts cannot be spawned " + node.getType() + " " + node.getLineNumber() + ":" + node.getColumnNumber() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }
        catch (SpawnActorException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new SpawnActorException(e.getMessage() + " in SpawnActorNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(MethodCallNode node) {
        try {
            if (!symbolTable.getDeclaredLocalMethods().containsKey(node.getMethodName())) {
                // if the method is not declared in the local methods, throw an exception
                throw new MethodCallException("Method: " + node.getMethodName() + " not found" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            } else{
                node.setType(symbolTable.getDeclaredLocalMethods().get(node.getMethodName()).getVariableType());
            }
            this.visitChildren(node);
        }
        catch (MethodCallException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodCallException(e.getMessage() + " in MethodCallNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
        try{
        if(!node.getParent().getType().equals(parLangE.VOID.getValue())){ //If the method does not return void
            if(!node.getChildren().isEmpty()&&(node.getChildren().getLast()instanceof ReturnStatementNode)){
                // node has children and the last child is return statement
                node.setType(node.getChildren().getLast().getType());
            }else{
                throw new LocalMethodBodyNodeException("Return statement missing from local method which does not return void " + ((MethodDclNode)node.getParent()).getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }else{
            node.setType(parLangE.VOID.getValue());//If there is a return statement, it is checked in visit(ReturnStatement node) that it is "return;". So an error should already have been produced
        }
        }
        catch (LocalMethodBodyNodeException e) {
        exceptions.add(e);
        }
        catch (Exception e) {
        exceptions.add(new LocalMethodBodyNodeException(e.getMessage() + " in LocalMethodBodyNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ArgumentsNode node) {
        try {
            this.visitChildren(node);
            LinkedHashMap<String, Attributes> params; //The parameters are used to check the arguments against
            AstNode parent = node.getParent();
            if (parent instanceof MethodCallNode methodCallNode) {
                String actorType = symbolTable.findActorParent(node); //Find the actor type of the method call
                String methodName = methodCallNode.getMethodName();
                Scope actorScope = symbolTable.lookUpScope(methodName + actorType); //Find the scope of the method
                params = actorScope.getParams();
                checkArgTypes(node, params, methodName); //Check the arguments against the parameters
            } else if (parent instanceof SpawnActorNode spawnActorNode) {
                //We can call SpawnActor from any scope, hence we have to find the Actor scope where the Spawn we are calling is declared
                Scope ActorScope = symbolTable.lookUpScope(parent.getType());
                //Within the Actor Scope we enter the spawn scope to get the parameters associated with Spawn
                Scope SpawnScope = ActorScope.children.get(0);
                params = SpawnScope.getParams();
                String actorName = spawnActorNode.getType();
                checkArgTypes(node, params, actorName); //Check the arguments against the parameters
            } else if (parent instanceof SendMsgNode sendMsgNode) {
                //The first child of SendMsgNode is always a receiver node
                AstNode receiverNode = sendMsgNode.getChildren().get(0);
                String receiverName = sendMsgNode.getReceiver();
                //Method name is used to find the parameters to check the arguments up against
                String methodName = ((SendMsgNode) parent).getMsgName();
                Attributes attributes = null; //The attributes are used to get the correct method scope

                //The receiver can be: IdentifierNode, StateAccessNode, KnowsAccessNode or SelfNode
                if(receiverNode instanceof StateAccessNode){
                    attributes = symbolTable.lookUpStateSymbol(receiverName.replaceAll("State.","")); //Look up the state symbol
                }else if(receiverNode instanceof KnowsAccessNode){
                    attributes = symbolTable.lookUpKnowsSymbol(receiverName.replaceAll("Knows.","")); //Look up the knows symbol
                }else if(receiverNode instanceof SelfNode){
                    String actorName = symbolTable.findActorParent(receiverNode);
                    if (actorName == null) { //If the actorName is null, throw an exception
                        throw new SelfNodeException("SelfNode is not a child of ActorDclNode" + ". Line: " + receiverNode.getLineNumber() + " Column: " + receiverNode.getColumnNumber());
                    }
                    attributes = new Attributes(actorName); //Set the attributes to the actorName
                }else if(receiverNode instanceof IdentifierNode){
                    attributes = symbolTable.lookUpSymbol(receiverName);
                }

                if (attributes != null) {
                    Scope methodScope = symbolTable.lookUpScope(methodName + attributes.getVariableType());
                    params = methodScope.getParams();
                    checkArgTypes(node, params, methodName); //Check the arguments against the parameters
                }else{ //If the attributes are null, throw an exception
                    throw new SendMsgException("Attributes of receiver: " + receiverName + " could not be found" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }else { //If the parent is not a method call, spawn actor or send message node, throw an exception
                throw new ArgumentsException("Arguments node parent is not a method call, spawn actor or send message node" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }
        catch (ArgumentsException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArgumentsException(e.getMessage() + " in ArgumentsNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }
    private void checkArgTypes(ArgumentsNode node, LinkedHashMap<String, Attributes> params, String msgName){
        int size = node.getChildren().size();
        if (params.size() != size){ //If the number of parameters does not match the number of arguments, throw an exception
            throw new ArgumentsException("Number of arguments does not match the number of parameters in: " + msgName + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
        }
        for (Map.Entry<String, Attributes> parameter : params.entrySet()) { //Iterates over parameters and arguments and matches them against each other
            int index = new ArrayList<>(params.keySet()).indexOf(parameter.getKey()); //Find the index of the parameter == index of argument
            String argType = node.getChildren().get(index).getType(); //Type of argument
            String paramType = parameter.getValue().getVariableType(); //Type of parameter
            if (!canConvert(paramType, argType)){ //If the types do not match, throw an exception
                throw new ArgumentsException("Argument type does not match parameter type in method: " + msgName + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }
    }

    @Override
    public void visit(AssignNode node) {
        try {
            this.visitChildren(node);
            String identifierType = node.getChildren().get(0).getType();
            String assignType = node.getChildren().get(1).getType();
            if (!canConvert(identifierType, assignType)) { //If the types do not match, throw an exception
                throw new AssignExecption("Type mismatch in assignment between " + identifierType + " and " + assignType + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(identifierType);
        }
        catch (AssignExecption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new AssignExecption(e.getMessage() + " in AssignNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(InitializationNode node) {
        this.visitChildren(node);
        try {
            String childType = node.getChildren().get(0).getType();
            if (childType == null) { //If the type of the child is null typechecking cannot be done, thus throw exception
                throw new InitializationNodeException("Type is not defined for initialization node" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(childType);
        }
        catch (InitializationNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new InitializationNodeException(e.getMessage() + " in InitializationNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ListNode node) {
        this.visitChildren(node);
        try{
            String listType = node.getChildren().get(0).getType();
            for (AstNode child : node.getChildren()) {
                if (!canConvert(listType, child.getType())) { //If the types do not match, throw an exception
                    throw new ListNodeException("List elements must be of the same type listtype: " + node.getType() + " elementType: " + child.getType() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            node.setType(listType + "[]");
        }
        catch (ListNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ListNodeException(e.getMessage() + " in ListNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(VarDclNode node) {
        this.visitChildren(node);
        try {
            int size = node.getChildren().size();
            String idType = node.getChildren().get(0).getType();
            if (!node.isInitialized()) {
                node.setType(idType); //If the variable is not initialized, the type is the same as the id
                return;
            }
            String initType = node.getChildren().get(1).getType();
            if (!canConvert(idType, initType)) { //If the types do not match, throw an exception
                throw new varDclNodeExeption("Type mismatch in declaration and initialization of variable " + node.getId() + " of type " + idType + " and " + initType + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(idType); //If the types match, the type is the same as the id
        }
        catch (varDclNodeExeption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new varDclNodeExeption(e.getMessage() + " in VarDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ActorDclNode node) {
        try {
            this.symbolTable.enterScope(node.getId());
            this.visitChildren(node);
            node.setType(node.getId());
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ActorDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }
    @Override
    public void visit(SelfNode node) {
        try {
            //We do not visit children since this is a leaf node
            if (!hasParent(node, ActorDclNode.class)){ //A Selfnode is only allowed to be a child of an ActorDclNode
                throw new SelfNodeException("SelfNode is not a child of ActorDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(symbolTable.findActorParent(node)); //A self node always refers to the actor it is contained within
        }catch (SelfNodeException e) {
            exceptions.add(e);
        }
    }

    @Override
    public void visit(BoolCompareNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals(parLangE.BOOL.getValue())) { //If the children are not of type bool, throw an exception
                    throw new BoolCompareException("All BoolCompareNode children does not have type bool" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            node.setType(parLangE.BOOL.getValue());
        }
        catch (BoolCompareException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolCompareException(e.getMessage() + " in BoolCompareNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(KillNode node) {
        //does not need types
        if (!hasParent(node, ActorDclNode.class)){ //A KillNode is only allowed to be a child of an ActorDclNode
            throw new KillNodeException("KillNode is not a child of ActorDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
        }
    }

    @Override
    public void visit(StateNode node) {
        //does not need types
        this.visitChildren(node);
    }

    @Override
    public void visit(FollowsNode node) {
        try {
            this.visitChildren(node);
            for (AstNode child : node.getChildren()) {
                if (child.getType() == null) { //If the type of the child is null, throw an exception
                    throw new FollowsNodeException("FollowsNode children does not have type defined" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            List<String> scripts = new ArrayList<>(); // List of scripts the actor follows
            String errorMessage = "";  // Error message to be printed
            for(AstNode child : node.getChildren()) {
                scripts.add(child.getType()); //Add the script to the list
                errorMessage += child.getType() + ", "; //Add the script to the error message
            }
            hasCorrectScriptMethods(scripts, ((ActorDclNode) node.getParent()).getId()); //Check if the actor has the correct methods from the script
        }
        catch (FollowsNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new FollowsNodeException(e.getMessage() + " in FollowsNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    private void hasCorrectScriptMethods(List<String> scriptNames, String actorName){
        Scope actorScope = symbolTable.lookUpScope(actorName); //Find the scope of the actor
        HashMap<String, Attributes> actorMethods = actorScope.getDeclaredOnMethods(); //Get the on methods of the actor
        actorMethods.putAll(actorScope.getDeclaredLocalMethods()); //Add the local methods to the actor methods
        for (String scriptName : scriptNames){ //Iterate over the scripts the actor follows
            Scope scope = symbolTable.lookUpScope(scriptName); //Find the scope of the script
            HashMap<String, Attributes> scriptMethods = scope.getDeclaredOnMethods(); //Get the on methods of the script
            scriptMethods.putAll(scope.getDeclaredLocalMethods()); //Add the local methods to the script methods
            for (Map.Entry<String, Attributes> scriptMethod : scriptMethods.entrySet()){ //Iterate over the methods of the script
                String method = scriptMethod.getKey();
                if (!actorMethods.containsKey(scriptMethod.getKey())){ //If the actor does not have the method, throw an exception
                    throw new FollowsNodeException("Actor " + actorName + " does not have method: " + method + "from " + scriptName);
                }
                LinkedHashMap<String, Attributes> actorParams = symbolTable.lookUpScope(method+actorName).getParams(); //Get the parameters of the method in the actor
                LinkedHashMap<String, Attributes> scriptParams = symbolTable.lookUpScope(method+scriptName).getParams(); //Get the parameters of the method in the script
                if (actorParams.size() != scriptParams.size()){ //If the number of parameters do not match, throw an exception
                    throw new FollowsNodeException("Actor " + actorName + " does not have the same number of parameters as script " + scriptName + " in method " + method);
                }
                Set<String> set = scriptParams.keySet();
                Iterator<String> iter = set.iterator(); //Iterator for the script parameters
                for (Map.Entry<String, Attributes> actorParam : actorParams.entrySet()){ //Iterate over the parameters of the actor and script
                    String scriptKey = iter.next(); //Get the key of the scriptParameter
                    if (!actorParam.getValue().getVariableType().equals(scriptParams.get(scriptKey).getVariableType())){ //If the types do not match, throw an exception
                        throw new FollowsNodeException("Actor " + actorName + " does not have the same parameter types as script " + scriptName + " in method " + method);
                    }
                    if (!actorParam.getKey().equals(scriptKey)){ //If the parameter names do not match, throw an exception
                        throw new FollowsNodeException("Actor " + actorName + " does not have the same parameter names as script " + scriptName + " in method " + method);
                    }
                }
            }
        }
    }

    @Override
    public void visit(KnowsNode node) {
        //does not need types
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodDclNode node) {
        try {
            this.symbolTable.enterScope(node.getId() + symbolTable.findActorParent(node));
            this.visitChildren(node);
            if (node.getMethodType().equals(parLangE.LOCAL.getValue())){ //If the method type is local
                String childType = node.getChildren().get(1).getType(); //getting BodyNode child
                String nodeType = node.getType();
                if (!canConvert(nodeType, childType)) { //the return type and the type returned from the body should match
                    throw new MethodDclNodeException("Return does not match returnType of method " + node.getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            else if (node.getMethodType().equals(parLangE.ON.getValue())) { //If the method type is on
                if (node.getType() == null) { //If the return type is not defined, throw an exception
                    throw new MethodDclNodeException("Return type is not defined for on method " + node.getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            else { //If the method type is not local or on, throw an exception
                throw new MethodCallException("Method type is not local or on of method: " + node.getId() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            this.symbolTable.leaveScope();
        }
        catch (MethodDclNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodDclNodeException(e.getMessage() + " in MethodDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }


    @Override
    public void visit(MainDclNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in MainDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(SpawnDclNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in SpawnDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ExpNode node) {
        //abstract class
        this.visitChildren(node);
    }

    @Override
    public void visit(IntegerNode node) {
        try {
            if (node.getValue() == null) {
                throw new IntegerNodeException("IntegerNode value is null" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(parLangE.INT.getValue());
        }
        catch (IntegerNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new IntegerNodeException(e.getMessage() + " in IntegerNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(DoubleNode node) {
        try {
            if (node.getValue() == null) {
                throw new DoubleNodeException("DoubleNode value is null" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(parLangE.DOUBLE.getValue());
        }
        catch (DoubleNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new DoubleNodeException(e.getMessage() + " in DoubleNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(StringNode node) {
        try {
            if (node.getValue() == null) {
                throw new StringNodeException("StringNode value is null" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(parLangE.STRING.getValue());
        }
        catch (StringNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new StringNodeException(e.getMessage() + " in StringNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(BoolAndExpNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals(parLangE.BOOL.getValue())) {
                    throw new BoolExpException("all BoolAndExpNode children does not have type bool" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            node.setType(parLangE.BOOL.getValue());
        }
        catch (BoolExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolExpException(e.getMessage() + " in BoolAndExpNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(BoolExpNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals(parLangE.BOOL.getValue())) {
                    throw new BoolExpException("all BoolExpNode children does not have type bool" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
            node.setType(parLangE.BOOL.getValue());
        }
        catch (BoolExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolExpException(e.getMessage() + " in BoolExpNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ArithExpNode node) {
        this.visitChildren(node);
        try {
            //A child can either be a IntegerNode, DoubleNode, IdentifierNode, or ArithExpNode
            String leftType = node.getChildren().get(0).getType();
            String rightType = node.getChildren().get(1).getType();
            String resultType = arithTypeConvert(leftType, rightType);
            if (resultType == null) { //If the types do not match, throw an exception
                throw new ArithExpException("Types do not match for ArithExp: " + leftType + " <--> " + rightType + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(resultType);
        }
        catch (ArithExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArithExpException(e.getMessage() + " in ArithExpNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(NegatedBoolNode node) {
        this.visitChildren(node);
        try {
            if (node.getChildren().get(0).getType().equals(parLangE.BOOL.getValue())) { //If the child is of type bool
                node.setType(parLangE.BOOL.getValue());
            } else {
                throw new BoolNodeException("NegatedBoolNode does not have type bool" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }
        catch (BoolNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolNodeException(e.getMessage() + " in NegatedBoolNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(BoolNode node) {
        try {
            if (((BoolNode) node).getValue() == null) { //If the value of the node is null, throw an exception
                throw new BoolNodeException("BoolNode does not have type bool" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            node.setType(parLangE.BOOL.getValue());
        }
        catch (BoolNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolNodeException(e.getMessage() + " in BoolNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(CompareExpNode node) {
        this.visitChildren(node);
        try {
            AstNode leftChild = node.getChildren().get(0);
            AstNode rightChild = node.getChildren().get(1);
            if (compareExpTypeMatching(node.getOperator(), leftChild.getType(), rightChild.getType())) { //If the types match
                node.setType(parLangE.BOOL.getValue());
            } else {
                throw new CompareTypeMatchingException("Type mismatch in comparison expression between " + leftChild.getType() + " and " + rightChild.getType() + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
        }
        catch (CompareTypeMatchingException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new CompareTypeMatchingException(e.getMessage() + " in CompareExpNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    private boolean compareExpTypeMatching(String operator, String leftType, String rightType){
        if (leftType.equals(rightType) && leftType.equals(parLangE.INT.getValue()) || leftType.equals(parLangE.DOUBLE.getValue())){
            return true; //If the types are the same and are either int or double
        }
        else if (operator.equals("==") && leftType.equals(rightType) && leftType.equals(parLangE.BOOL.getValue())){
            return true; //If the types are the same and are bool
        }
        else {
            return false; //If the types do not match
        }
    }

    @Override
    public void visit(IterationNode node) {
        //abstract class
        this.visitChildren(node);
    }

    @Override
    public void visit(WhileNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in WhileNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ForNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ForNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(AccessNode node) {
        //abstract class
        this.visitChildren(node);
    }

    @Override
    public void visit(SelectionNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in SelectionNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(ArrayAccessNode node) {
        try{
            this.visitChildren(node);
            String id = node.getAccessIdentifier();
            Attributes attributes;
            if (id.contains("State.")){ //If the parent is a StateAccessNode, look up in stateSymbols
                attributes = symbolTable.lookUpStateSymbol(id.split("\\.")[1]);
            }
            else{
                attributes = symbolTable.lookUpSymbol(id); //Look up in symbol table
            }
            if (attributes == null){
                throw new ArrayAccessException("Array: " + id + " not found" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            String identifierType = attributes.getVariableType();
            int identifierDimensions = countBracketPairs(identifierType);
            if(identifierDimensions != node.getDimensions()){ //If the dimensions do not match, throw an exception
                throw new ArrayAccessException("Dimensions of type " + identifierType + "(" + identifierDimensions + ")" + " do not match access " + node.getAccessIdentifier() + printBrackets(node.getDimensions()) + "(" + node.getDimensions() + ") " + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            String accessType = removeBrackets(identifierType);
            node.setType(accessType);
        }
        catch (ArrayAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArrayAccessException(e.getMessage() + " in ArrayAccessNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    private int countBracketPairs(String arrayType){
        String bracketString = arrayType.replaceAll("[a-zA-Z]","").trim();
        //We are left with a string only containing brackets: Two cases: "[]" or "[][]"
        return bracketString.length()/2;
    }

    private String removeBrackets(String arrayType){
        //Remove all trailing brackets after access type
        return arrayType.split("\\[")[0];
    }

    private String printBrackets(int numberOfBrackets){
        StringBuilder bracketString = new StringBuilder();
        for(int i = 0; i < numberOfBrackets; i++){
            bracketString.append("[]");
        }
        return bracketString.toString();
    }

    @Override
    public void visit(StateAccessNode node) {
        try{
            if (!hasParent(node, ActorDclNode.class)){ //A StateAccessNode is only allowed to be a child of an ActorDclNode
                throw new StateAccessException("StateAccessNode is not a child of ActorDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            if (!node.getChildren().isEmpty()){ //If the node has children, visit them
                this.visitChildren(node);
                node.setType(node.getChildren().get(0).getType());
            }
            else {
                String id = node.getAccessIdentifier();
                Attributes attributes = symbolTable.lookUpStateSymbol(id); //Look up the state symbol
                if (attributes == null) {
                    throw new StateAccessException("State: " + id + " not found" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
                node.setType(attributes.getVariableType());
            }
        }
        catch (StateAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new StateAccessException(e.getMessage() + " in StateAccessNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(KnowsAccessNode node) {
        try {
            if (!hasParent(node, ActorDclNode.class)) { //A KnowsAccessNode is only allowed to be a child of an ActorDclNode
                throw new KnowsAccessException("KnowsAccessNode is not a child of ActorDclNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
            }
            this.visitChildren(node);
            String id = node.getAccessIdentifier();
            Attributes attributes = symbolTable.lookUpKnowsSymbol(id);
            node.setType(attributes.getVariableType());
        }
        catch (KnowsAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new KnowsAccessException(e.getMessage() + " in KnowsAccessNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    @Override
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals(parLangE.STRING.getValue())) { //If the child is not of type string
                    if (child.getType().equals(parLangE.INT.getValue()) || child.getType().equals(parLangE.DOUBLE.getValue())) { //If the child is of type int or double we cn convert it to string
                        continue;
                    }
                    throw new PrintException("Print statement only accepts string arguments" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber());
                }
            }
        }
        catch (PrintException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new PrintException(e.getMessage() + " in PrintCallNode" + ". Line: " + node.getLineNumber() + " Column: " + node.getColumnNumber()));
        }
    }

    private boolean hasParent(AstNode node, Class<?> parentClass) {
        AstNode parent = node.getParent();
        while (parent != null) {
            if (parent.getClass() == parentClass) {
                return true; //If the parent is of the class parentClass
            }
            parent = parent.getParent();
        }
        return false; // if the node does not have a parent of the class parentClass
    }
}