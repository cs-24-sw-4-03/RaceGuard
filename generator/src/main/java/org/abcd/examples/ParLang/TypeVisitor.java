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

    private String typeMatchOrConvert(String type1, String type2){
        if (type1.equals(type2)){
            return type1;
        }
        if (type1.equals("int") && type2.equals("double")){
            return "double";
        }
        if (type1.equals("double") && type2.equals("int")){
            return "double";
        }
        if (symbolTable.declaredScripts.contains(type1)){
            symbolTable.enterScope(type1);
            ArrayList<String> types = symbolTable.getActorsFollowingScript();
            if (types.contains(type2)){
                symbolTable.leaveScope();
                return type1;
            }
            symbolTable.leaveScope();
            return null;
        }
        if (symbolTable.declaredScripts.contains(type2)){
            symbolTable.enterScope(type2);
            ArrayList<String> types = symbolTable.getActorsFollowingScript();
            if (types.contains(type1)){
                symbolTable.leaveScope();
                return type2;
            }
            symbolTable.leaveScope();
        }
        return null;
    }

    private boolean canConvert(String assignTo, String assignFrom){
        if (assignTo.equals(assignFrom)){
            return true;
        }
        if (assignTo.equals("int") && assignFrom.equals("double")){
            return false;
        }
        if (assignTo.equals("double") && assignFrom.equals("int")){
            return true;
        }
        if (symbolTable.declaredScripts.contains(assignTo)){
            symbolTable.enterScope(assignTo);
            ArrayList<String> types = symbolTable.getActorsFollowingScript();
            if (types.contains(assignFrom)){
                symbolTable.leaveScope();
                return true;
            }
            symbolTable.leaveScope();
        }
        return false;
    }

    private Attributes accessType(AstNode node){
        Attributes attributes = null;
        if (node instanceof StateAccessNode){
            String id = ((StateAccessNode) node).getAccessIdentifier();
            attributes = symbolTable.lookUpStateSymbol(id);
        }
        if (node instanceof KnowsAccessNode){
            if (node instanceof KnowsAccessNode){
                String id = ((KnowsAccessNode) node).getAccessIdentifier();
                attributes = symbolTable.lookUpKnowsSymbol(id);
            }
        }
        if (attributes != null){
            return attributes;
        }
        else {
            throw new AccessTypeException("Access type not found for node: " + node.getClass().getName());
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
        /*try {*/
            this.symbolTable.enterScope(node.getId());
            this.visitChildren(node);
            if (node.getId() == null) {
                throw new ScriptDclException("Type is not defined for script declaration node");
            }
            node.setType(node.getId());
            this.symbolTable.leaveScope();
        /*}
        catch (ScriptDclException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ScriptDclException(e.getMessage() + " in ScriptDclNode"));
        }*/
    }

    @Override
    public void visit(ScriptMethodNode node) {
        /*try {*/
        symbolTable.enterScope(node.getId() + symbolTable.findActorParent(node));
        this.visitChildren(node);
            if (node.getType() == null) {
                throw new ScriptMethodException("Type is not defined for script method node");
            }
            if (node.getMethodType() == null) {
                throw new ScriptMethodException("(on/local) Method type is not defined for script method node");
            }
        symbolTable.leaveScope();
       /* }
        catch (ScriptMethodException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ScriptMethodException(e.getMessage() + " in ScriptMethodNode"));
        }*/
    }

    @Override
    public void visit(SendMsgNode node) {
        /*try {*/
            this.visitChildren(node);
       /* }
        catch (MethodCallException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodCallException(e.getMessage() + " in SendMsgNode"));
        }*/
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
        /*try { */
            if((node.getParent() instanceof MethodCallNode)) {
                String Name = node.getName();
                Attributes attributes = symbolTable.getDeclaredLocalMethods().get(Name);
                node.setType(attributes.getVariableType());
            }
            else if ((node.getParent() instanceof SendMsgNode)){
                String id = node.getName();
                Attributes attributes = symbolTable.lookUpSymbol(id);
                node.setType(attributes.getVariableType());
            }
            else {
                System.out.println("Symbol: " + node.getName());
                if (hasParent(node, StateNode.class)) {
                    node.setType(this.symbolTable.lookUpStateSymbol(node.getName()).getVariableType());
                }
                else if (hasParent(node, KnowsNode.class)) {
                    node.setType(this.symbolTable.lookUpKnowsSymbol(node.getName()).getVariableType());
                }
                else if (hasParent(node, FollowsNode.class)) {
                    node.setType(node.getName());
                }
                else {
                    System.out.println("Normal Symbol: " + node.getName());
                    node.setType(this.symbolTable.lookUpSymbol(node.getName()).getVariableType());
                }
            }
        /*}
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in IdentifierNode"));
        }*/
    }

    @Override
    public void visit(ParametersNode node) {
        /*try {*/
            this.visitChildren(node);
        /*}
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ParametersNode"));
        }*/
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
        /*try {*/
            String returnType = node.getChildren().get(0).getType();
            if (returnType == null) {
                throw new ReturnNodeException("Type is not defined for return statement");
            }
            node.setType(returnType);
        /*}
        catch (ReturnNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ReturnNodeException(e.getMessage() + " in ReturnStatementNode"));
        }*/
    }

    @Override
    public void visit(SpawnActorNode node) {
        /*try {*/
            this.visitChildren(node);
            if (node.getType() == null) {
                throw new SpawnActorException("Type is not defined for spawn actor node");
            }
       /* }
        catch (SpawnActorException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new SpawnActorException(e.getMessage() + " in SpawnActorNode"));
        }*/
    }

    @Override
    public void visit(MethodCallNode node) {
        /*try {*/
            if (!symbolTable.getDeclaredLocalMethods().containsKey(node.getMethodName())) {
                throw new MethodCallException("Method: " + node.getMethodName() + " not found");
            } else{
                System.out.println("Method: " + node.getMethodName() + " found");
                node.setType(symbolTable.getDeclaredLocalMethods().get(node.getMethodName()).getVariableType());
            }
            this.visitChildren(node);
        /*}
        catch (MethodCallException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodCallException(e.getMessage() + " in MethodCallNode"));
        }*/
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
        /*try{*/
            node.setType(node.getChildren().get(node.getChildren().size()-1).getType());
            if (node.getType() == null) {
                throw new LocalMethodBodyNodeException("Return type is not defined for local method body node");
            }
        /*}
        catch (LocalMethodBodyNodeException e) {
        exceptions.add(e);
        }
        catch (Exception e) {
        exceptions.add(new LocalMethodBodyNodeException(e.getMessage() + " in LocalMethodBodyNode"));
        }*/
    }

    @Override
    public void visit(ArgumentsNode node) {
        /*try {*/
            this.visitChildren(node);
            LinkedHashMap<String, Attributes> params;
            AstNode parent = node.getParent();
            if (parent instanceof MethodCallNode) {
                params = symbolTable.getCurrentScope().getParams();
                MethodCallNode methodCallNode = (MethodCallNode) parent;
                String methodName = methodCallNode.getMethodName();
                checkArgTypes(node, params, methodName);
            } else if (parent instanceof SpawnActorNode) {
                //We can call SpawnActor from any scope, hence we have to find the Actor scope where the Spawn we are calling is declared
                Scope ActorScope = symbolTable.lookUpScope(parent.getType());
                //Within the Actor Scope we enter the spawn scope to get the parameters associated with Spawn
                Scope SpawnScope = ActorScope.children.get(0);
                params = SpawnScope.getParams();
                SpawnActorNode spawnActorNode = (SpawnActorNode) parent;
                String actorName = spawnActorNode.getType();
                checkArgTypes(node, params, actorName);
            } else if (parent instanceof SendMsgNode) {
                //Cast the parent to SendMsgNode
                SendMsgNode sendMsgNode = (SendMsgNode) parent;
                //The first child of SendMsgNode is always a receiver node
                AstNode receiverNode = sendMsgNode.getChildren().get(0);
                String receiverName = sendMsgNode.getReceiver();
                //Method name is used to find the parameters to check the arguments up against
                String methodName = ((SendMsgNode) parent).getMsgName();
                Attributes attributes = null; //The attributes are used to get the correct method scope

                //The receiver can be: IdentifierNode, StateAccessNode, KnowsAccessNode, SelfNode or SenderNode
                if(receiverNode instanceof StateAccessNode){
                    attributes = symbolTable.lookUpStateSymbol(receiverName);
                }else if(receiverNode instanceof KnowsAccessNode){
                    attributes = symbolTable.lookUpKnowsSymbol(receiverName);
                }else if(receiverNode instanceof SelfNode){
                    String actorName = symbolTable.findActorParent(receiverNode);
                    attributes = symbolTable.lookUpSymbol(actorName);
                }else if(receiverNode instanceof IdentifierNode){
                    attributes = symbolTable.lookUpSymbol(receiverName);
                }

                //We do not type check sender as it is not possible to do statically
                if (!(receiverNode instanceof SenderNode) && attributes != null) {
                    Scope methodScope = symbolTable.lookUpScope(methodName + attributes.getVariableType());
                    params = methodScope.getParams();
                    checkArgTypes(node, params, methodName);
                }
            }else {
                throw new ArgumentsException("Arguments node parent is not a method call, spawn actor or send message node");
            }
        /*}
        catch (ArgumentsException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArgumentsException(e.getMessage() + " in ArgumentsNode"));
        }*/
    }
    private void checkArgTypes(ArgumentsNode node, LinkedHashMap<String, Attributes> params, String msgName){
        int size = node.getChildren().size();
        if (params.size() != size){
            throw new ArgumentsException("Number of arguments does not match the number of parameters in: " + msgName);
        }
        for (Map.Entry<String, Attributes> parameter : params.entrySet()) { //Iterates over parameters and arguments and matches them against each other
            int index = new ArrayList<>(params.keySet()).indexOf(parameter.getKey()); //Find the index of the parameter == index of argument
            String argType = node.getChildren().get(index).getType(); //Type of argument
            String paramType = parameter.getValue().getVariableType(); //Type of parameter
            if (!canConvert(paramType, argType)){
                throw new ArgumentsException("Argument type does not match parameter type in method: " + msgName);
            }
        }
    }

    @Override
    public void visit(AssignNode node) {
        /*try {*/
            this.visitChildren(node);
            String identifierType = node.getChildren().get(0).getType();
            String assignType = node.getChildren().get(1).getType();
            if (!canConvert(identifierType, assignType)) {
                throw new AssignExecption("Type mismatch in assignment between " + identifierType + " and " + assignType);
            }
            node.setType(identifierType);
        /*}
        catch (AssignExecption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new AssignExecption(e.getMessage() + " in AssignNode"));
        }*/
    }

    @Override
    public void visit(InitializationNode node) {
        this.visitChildren(node);
        /*try {*/
            String childType = node.getChildren().get(0).getType();
            if (childType == null) {
                throw new InitializationNodeException("Type is not defined for initialization node");
            }
            node.setType(childType);
       /* }
        catch (InitializationNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new InitializationNodeException(e.getMessage() + " in InitializationNode"));
        }*/
    }

    @Override
    public void visit(ListNode node) {
        this.visitChildren(node);
        /*try{*/
            String listType = node.getChildren().get(0).getType();
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals(listType)) {
                    throw new ListNodeException("List elements must be of the same type");
                }
            }
            node.setType(listType + "[]");
        /*}
        catch (ListNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ListNodeException(e.getMessage() + " in ListNode"));
        }*/
    }

    @Override
    public void visit(VarDclNode node) {
        this.visitChildren(node);
        /*try {*/
            int size = node.getChildren().size();
            String idType = node.getChildren().get(0).getType();
            if (size == 1) {
                node.setType(idType);
                return;
            }
            String initType = node.getChildren().get(1).getType();
            String typeMatch = typeMatchOrConvert(idType, initType);
            if (typeMatch == null) {
                throw new varDclNodeExeption("Type mismatch in declaration and initialization of variable " + node.getId());
            }
            node.setType(typeMatch);
       /* }
        catch (varDclNodeExeption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new varDclNodeExeption(e.getMessage() + " in VarDclNode"));
        }*/
    }

    @Override
    public void visit(ActorDclNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getId());
            this.visitChildren(node);
            node.setType(node.getId());
            this.symbolTable.leaveScope();
        /*}
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ActorDclNode"));
        }*/
    }
    @Override
    public void visit(SelfNode node) {
        //We do not visit children since this is a leaf node
        node.setType(symbolTable.findActorParent(node)); //A self node always refers to the actor it is contained within
    }

    @Override
    public void visit(SenderNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BoolCompareNode node) {
        this.visitChildren(node);
        /*try {*/
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("bool")) {
                    throw new BoolCompareException("all BoolCompareNode children does not have type bool");
                }
            }
            node.setType("bool");
        /*}
        catch (BoolCompareException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolCompareException(e.getMessage() + " in BoolCompareNode"));
        }*/
    }

    @Override
    public void visit(StateNode node) {
        //does not need types
        this.visitChildren(node);
    }

    @Override
    public void visit(FollowsNode node) {
        /*try {*/
            this.visitChildren(node);
            for (AstNode child : node.getChildren()) {
                if (child.getType() == null) {
                    throw new FollowsNodeException("FollowsNode children does not have type defined");
                }
            }
            node.setType("follows");
       /* }
        catch (FollowsNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new FollowsNodeException(e.getMessage() + " in FollowsNode"));
        }*/
    }

    @Override
    public void visit(KnowsNode node) {
        //does not need types
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodDclNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getId() + symbolTable.findActorParent(node));
            this.visitChildren(node);
            if (node.getMethodType().equals("local")){
                String childType = node.getChildren().get(1).getType(); //getting BodyNode child
                String nodeType = node.getType();
                String typeMatch = typeMatchOrConvert(nodeType, childType);
                if (typeMatch == null ) {
                    throw new MethodDclNodeException("Return does not match returnType of method " + node.getId());
                }
            }
            else if (node.getMethodType().equals("on")) {
                if (node.getType() == null) {
                    throw new MethodDclNodeException("Return type is not defined for on method node");
                }
            }
            else {
                throw new MethodCallException("Method type is not local or on of method: " + node.getId());
            }
            this.symbolTable.leaveScope();
        /*}
        catch (MethodDclNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodDclNodeException(e.getMessage() + " in MethodDclNode"));
        }*/
    }


    @Override
    public void visit(MainDclNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
       /* }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in MainDclNode"));
        }*/
    }

    @Override
    public void visit(SpawnDclNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        /*}
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in SpawnDclNode"));
        }*/
    }

    @Override
    public void visit(ExpNode node) {
        //abstract class
        this.visitChildren(node);
    }

    @Override
    public void visit(IntegerNode node) {
        /*try {*/
            if (node.getValue() == null) {
                throw new IntegerNodeException("IntegerNode value is null");
            }
            node.setType("int");
        /*}
        catch (IntegerNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new IntegerNodeException(e.getMessage() + " in IntegerNode"));
        }*/
    }

    @Override
    public void visit(DoubleNode node) {
        /*try {*/
            if (node.getValue() == null) {
                throw new DoubleNodeException("DoubleNode value is null");
            }
            node.setType("double");
        /*}
        catch (DoubleNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new DoubleNodeException(e.getMessage() + " in DoubleNode"));
        }*/
    }

    @Override
    public void visit(StringNode node) {
        /*try {*/
            if (node.getValue() == null) {
                throw new StringNodeException("StringNode value is null");
            }
            node.setType("string");
       /* }
        catch (StringNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new StringNodeException(e.getMessage() + " in StringNode"));
        }*/
    }

    @Override
    public void visit(BoolAndExpNode node) {
        this.visitChildren(node);
        /*try {*/
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("bool")) {
                    throw new BoolExpException("all BoolAndExpNode children does not have type bool");
                }
            }
            node.setType("bool");
        /*}
        catch (BoolExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolExpException(e.getMessage() + " in BoolAndExpNode"));
        }*/
    }

    @Override
    public void visit(BoolExpNode node) {
        this.visitChildren(node);
        /*try {*/
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("bool")) {
                    throw new BoolExpException("all BoolExpNode children does not have type bool");
                }
            }
            node.setType("bool");
        /*}
        catch (BoolExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolExpException(e.getMessage() + " in BoolExpNode"));
        }*/
    }

    @Override
    public void visit(ArithExpNode node) {
        this.visitChildren(node);
        /*try {*/
            //A child can either be a IntegerNode, DoubleNode, IdentifierNode, or ArithExpNode
            String leftType = node.getChildren().get(0).getType();
            String rightType = node.getChildren().get(1).getType();
            String resultType = typeMatchOrConvert(leftType, rightType);
            if (resultType == null) {
                throw new ArithExpException("Types do not match for ArithExp: " + leftType + " <--> " + rightType);
            }
            node.setType(resultType);
        /*}
        catch (ArithExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArithExpException(e.getMessage() + " in ArithExpNode"));
        }*/
    }

    @Override
    public void visit(NegatedBoolNode node) {
        this.visitChildren(node);
        /*try {*/
            if (node.getChildren().get(0).getType().equals("bool")) {
                node.setType("bool");
            } else {
                throw new BoolNodeException("NegatedBoolNode does not have type bool");
            }
        /*}
        catch (BoolNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolNodeException(e.getMessage() + " in NegatedBoolNode"));
        }*/
    }

    @Override
    public void visit(BoolNode node) {
        /*try {*/
            if (((BoolNode) node).getValue() == null) {
                throw new BoolNodeException("BoolNode does not have type bool");
            }
            node.setType("bool");
       /* }
        catch (BoolNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolNodeException(e.getMessage() + " in BoolNode"));
        }*/
    }

    @Override
    public void visit(CompareExpNode node) {
        this.visitChildren(node);
        /*try {*/
            AstNode leftChild = node.getChildren().get(0);
            AstNode rightChild = node.getChildren().get(1);
            if (compareExpTypeMatching(leftChild.getType(), rightChild.getType())) {
                node.setType("bool");
            } else {
                throw new CompareTypeMatchingException("Type mismatch in comparison expression between " + leftChild.getType() + " and " + rightChild.getType());
            }
       /* }
        catch (CompareTypeMatchingException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new CompareTypeMatchingException(e.getMessage() + " in CompareExpNode"));
        }*/
    }

    private boolean compareExpTypeMatching(String leftType, String rightType){
        if (leftType.equals(rightType) && leftType.equals("int") || leftType.equals("double")){
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void visit(IterationNode node) {
        //abstract class
        this.visitChildren(node);
    }

    @Override
    public void visit(WhileNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        /*}
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in WhileNode"));
        }*/
    }

    @Override
    public void visit(ForNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
       /* }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ForNode"));
        }*/
    }

    @Override
    public void visit(AccessNode node) {
        //abstract class
        this.visitChildren(node);
    }

    @Override
    public void visit(SelectionNode node) {
        /*try {*/
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        /*}
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in SelectionNode"));
        }*/
    }

    @Override
    public void visit(ArrayAccessNode node) {
        /*try{*/
            String id = node.getAccessIdentifier();
            Attributes attributes;
            if (hasParent(node, StateAccessNode.class)){
                attributes = symbolTable.lookUpStateSymbol(id);
            }
            else{
                attributes = symbolTable.lookUpSymbol(id);
            }
            if (attributes == null){
                throw new ArrayAccessException("Array: " + id + " not found");
            }
            node.setType(attributes.getVariableType());
        /*}
        catch (ArrayAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArrayAccessException(e.getMessage() + " in ArrayAccessNode"));
        }*/
    }

    @Override
    public void visit(StateAccessNode node) {
        /*try{*/
            if (!hasParent(node, ActorDclNode.class)){
                throw new StateAccessException("StateAccessNode is not a child of ActorDclNode");
            }
            if (!node.getChildren().isEmpty()){
                this.visitChildren(node);
                node.setType(node.getChildren().get(0).getType());
            }
            else {
                String id = node.getAccessIdentifier();
                Attributes attributes = symbolTable.lookUpStateSymbol(id);
                if (attributes == null) {
                    throw new StateAccessException("State: " + id + " not found");
                }
                node.setType(attributes.getVariableType());
            }
        /*}
        catch (StateAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new StateAccessException(e.getMessage() + " in StateAccessNode"));
        }*/
    }

    @Override
    public void visit(KnowsAccessNode node) {
        /*try {*/
            if (!hasParent(node, ActorDclNode.class)) {
                throw new KnowsAccessException("KnowsAccessNode is not a child of ActorDclNode");
            }
            this.visitChildren(node);
            String id = node.getAccessIdentifier();
            Attributes attributes = symbolTable.lookUpKnowsSymbol(id);
            node.setType(attributes.getVariableType());
        /*}
        catch (KnowsAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new KnowsAccessException(e.getMessage() + " in KnowsAccessNode"));
        }*/
    }

    @Override
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
        /*try {*/
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("string")) {
                    if (child.getType().equals("int") || child.getType().equals("double")) {
                        continue;
                    }
                    throw new PrintException("Print statement only accepts string arguments");
                }
            }
        /*}
        catch (PrintException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new PrintException(e.getMessage() + " in PrintCallNode"));
        }*/
    }

    private boolean hasParent(AstNode node, Class<?> parentClass) {
        AstNode parent = node.getParent();
        while (parent != null) {
            if (parent.getClass() == parentClass) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }
}