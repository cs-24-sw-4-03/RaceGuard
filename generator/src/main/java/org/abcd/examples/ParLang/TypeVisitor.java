package org.abcd.examples.ParLang;

import com.sun.source.tree.LiteralTree;
import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.*;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.SymbolTable;
import org.abcd.examples.ParLang.symbols.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Override
    public void visitChildren(AstNode node){
        for (AstNode child : node.getChildren()) {
            child.accept(this);
        }
    }

    @Override
    public void visit(ScriptDclNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            if (node.getId() == null) {
                throw new ScriptDclException("Type is not defined for script declaration node");
            }
            node.setType(node.getId());
            this.symbolTable.leaveScope();
        }
        catch (ScriptDclException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ScriptDclException(e.getMessage() + " in ScriptDclNode"));
        }
    }

    @Override
    public void visit(ScriptMethodNode node) {
        this.visitChildren(node);
        try {
            if (node.getType() == null) {
                throw new ScriptMethodException("Type is not defined for script method node");
            }
            if (node.getMethodType() == null) {
                throw new ScriptMethodException("(on/local) Method type is not defined for script method node");
            }
        }
        catch (ScriptMethodException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ScriptMethodException(e.getMessage() + " in ScriptMethodNode"));
        }
    }

    @Override
    public void visit(SendMsgNode node) {
        try {
            if (symbolTable.lookUpSymbol(node.getMsgName()) == null) {
                throw new MethodCallException("Method: " + node.getMsgName() + " not found");
            }
            symbolTable.enterScope(node.getMsgName());
            this.visitChildren(node);
            symbolTable.leaveScope();
        }
        catch (MethodCallException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodCallException(e.getMessage() + " in SendMsgNode"));
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
            if (hasParent(node, StateNode.class)) {
                node.setType(this.symbolTable.lookUpStateSymbol(node.getName()).getVariableType());
            } else if (hasParent(node, KnowsNode.class)) {
                node.setType(this.symbolTable.lookUpKnowsSymbol(node.getName()).getVariableType());
            } else {
                node.setType(this.symbolTable.lookUpSymbolCurrentScope(node.getName()).getVariableType());
            }
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in IdentifierNode"));
        }
    }

    @Override
    public void visit(ParametersNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ParametersNode"));
        }
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
        try {
            String returnType = node.getChildren().get(0).getType();
            if (returnType == null) {
                throw new ReturnNodeException("Type is not defined for return statement");
            }
            node.setType(returnType);
        }
        catch (ReturnNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ReturnNodeException(e.getMessage() + " in ReturnStatementNode"));
        }
    }

    @Override
    public void visit(SpawnActorNode node) {
        try {
            this.visitChildren(node);
            if (node.getType() == null) {
                throw new SpawnActorException("Type is not defined for spawn actor node");
            }
        }
        catch (SpawnActorException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new SpawnActorException(e.getMessage() + " in SpawnActorNode"));
        }
    }

    @Override
    public void visit(MethodCallNode node) {
        try {
            if (symbolTable.lookUpSymbol(node.getMethodName()) == null) {
                throw new MethodCallException("Method: " + node.getMethodName() + " not found");
            }
            symbolTable.enterScope(node.getMethodName());
            this.visitChildren(node);
            symbolTable.leaveScope();
        }
        catch (MethodCallException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodCallException(e.getMessage() + " in MethodCallNode"));
        }
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
        try{
            node.setType(node.getChildren().get(node.getChildren().size()-1).getType());
            if (node.getType() == null) {
                throw new LocalMethodBodyNodeException("Return type is not defined for local method body node");
            }
        }
        catch (LocalMethodBodyNodeException e) {
        exceptions.add(e);
        }
        catch (Exception e) {
        exceptions.add(new LocalMethodBodyNodeException(e.getMessage() + " in LocalMethodBodyNode"));
        }
    }

    @Override
    public void visit(ArgumentsNode node) {
        this.visitChildren(node);
        try {
            Map<String, Attributes> params = symbolTable.getCurrentScope().getParams();
            AstNode parent = node.getParent();
            int numOfChildren = node.getChildren().size();
            if (parent instanceof MethodCallNode) {
                MethodCallNode methodCallNode = (MethodCallNode) parent;
                String methodName = methodCallNode.getMethodName();
                checkArgTypes(node, params, methodName);
            } else if (parent instanceof SpawnActorNode) {
                SpawnActorNode spawnActorNode = (SpawnActorNode) parent;
                String actorName = spawnActorNode.getType();
                checkArgTypes(node, params, actorName);
            } else if (parent instanceof SendMsgNode) {
                SendMsgNode sendMsgNode = (SendMsgNode) parent;
                String msgName = sendMsgNode.getMsgName();
                checkArgTypes(node, params, msgName);
            } else {
                throw new ArgumentsException("Arguments node parent is not a method call, spawn actor or send message node");
            }
        }
        catch (ArgumentsException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArgumentsException(e.getMessage() + " in ArgumentsNode"));
        }
    }
    private void checkArgTypes(ArgumentsNode node, Map<String, Attributes> params, String msgName){
        int size = node.getChildren().size();
        if (params.size() != size){
            throw new ArgumentsException("Number of arguments does not match the number of parameters in spawn actor: " + msgName);
        }
        for (int i = 0; i < size; i++) {
            String argType = node.getChildren().get(i).getType();
            String paramType = params.get(i).getVariableType();
            if (!argType.equals(paramType)){
                throw new ArgumentsException("Argument type does not match parameter type in send message: " + msgName);
            }
        }
    }

    /*@Override
    public void visit(DclNode node) {
        this.visitChildren(node);
        try{
            String identifierType = node.getChildren().get(0).getType();
            String initType = node.getChildren().get(1).getType();
            if (!identifierType.equals(initType)) {
                throw new varDclNodeExeption("Type mismatch in declaration DclNode");
            }
            node.setType(identifierType);
        }
        catch (varDclNodeExeption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new varDclNodeExeption(e.getMessage() + " in DclNode"));
        }
    }*/

    @Override
    public void visit(AssignNode node) {
        this.visitChildren(node);
        try {
            String identifierType = node.getChildren().get(0).getType();
            String assignType = node.getChildren().get(1).getType();
            if (!identifierType.equals(assignType)) {
                throw new AssignExecption("Type mismatch in assignment");
            }
            node.setType(identifierType);
        }
        catch (AssignExecption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new AssignExecption(e.getMessage() + " in AssignNode"));
        }
    }

    @Override
    public void visit(InitializationNode node) {
        this.visitChildren(node);
        try {
            String childType = node.getChildren().get(0).getType();
            if (childType == null) {
                throw new InitializationNodeException("Type is not defined for initialization node");
            }
            node.setType(childType);
        }
        catch (InitializationNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new InitializationNodeException(e.getMessage() + " in InitializationNode"));
        }
    }

    @Override
    public void visit(ListNode node) {
        this.visitChildren(node);
        try{
            String listType = node.getChildren().get(0).getType();
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals(listType)) {
                    throw new ListNodeException("List elements must be of the same type");
                }
            }
            node.setType(listType + "[]");
        }
        catch (ListNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ListNodeException(e.getMessage() + " in ListNode"));
        }
    }

    @Override
    public void visit(VarDclNode node) {
        this.visitChildren(node);
        try {
            System.out.println(node.getChildren().get(0).getType() + " " + node.getChildren().get(1).getType());
            String identifierType = node.getChildren().get(0).getType();
            String initType = node.getChildren().get(1).getType();
            if (!identifierType.equals(initType)) {
                throw new varDclNodeExeption("Type mismatch in declaration and initialization of variable");
            }
            node.setType(identifierType);
        }
        catch (varDclNodeExeption e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new varDclNodeExeption(e.getMessage() + " in VarDclNode"));
        }
    }

    @Override
    public void visit(ActorDclNode node) {
        try {
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            this.symbolTable.leaveScope();
        }
        catch (Exception e) {
            exceptions.add(new RuntimeException(e.getMessage() + " in ActorDclNode"));
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
                if (child.getType() == null) {
                    throw new FollowsNodeException("FollowsNode children does not have type defined");
                }
            }
            node.setType("follows");
        }
        catch (FollowsNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new FollowsNodeException(e.getMessage() + " in FollowsNode"));
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
            this.symbolTable.enterScope(node.getNodeHash());
            this.visitChildren(node);
            String childType = node.getChildren().get(1).getType();
            if (!node.getType().equals(childType)) {
                throw new MethodDclNodeException("Return does not match returnType of method");
            }
            this.symbolTable.leaveScope();
        }
        catch (MethodDclNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new MethodDclNodeException(e.getMessage() + " in MethodDclNode"));
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
            exceptions.add(new RuntimeException(e.getMessage() + " in MainDclNode"));
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
            exceptions.add(new RuntimeException(e.getMessage() + " in SpawnDclNode"));
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
                throw new IntegerNodeException("IntegerNode value is null");
            }
            node.setType("int");
        }
        catch (IntegerNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new IntegerNodeException(e.getMessage() + " in IntegerNode"));
        }
    }

    @Override
    public void visit(DoubleNode node) {
        try {
            if (node.getValue() == null) {
                throw new DoubleNodeException("DoubleNode value is null");
            }
            node.setType("double");
        }
        catch (DoubleNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new DoubleNodeException(e.getMessage() + " in DoubleNode"));
        }
    }

    @Override
    public void visit(StringNode node) {
        try {
            if (node.getValue() == null) {
                throw new StringNodeException("StringNode value is null");
            }
            node.setType("string");
        }
        catch (StringNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new StringNodeException(e.getMessage() + " in StringNode"));
        }
    }

    @Override
    public void visit(BoolAndExpNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("bool")) {
                    throw new BoolExpException("all BoolAndExpNode children does not have type bool");
                }
            }
            node.setType("bool");
        }
        catch (BoolExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolExpException(e.getMessage() + " in BoolAndExpNode"));
        }
    }

    @Override
    public void visit(BoolExpNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("bool")) {
                    throw new BoolExpException("all BoolExpNode children does not have type bool");
                }
            }
            node.setType("bool");
        }
        catch (BoolExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolExpException(e.getMessage() + " in BoolExpNode"));
        }
    }

    @Override
    public void visit(ArithExpNode node) {
        this.visitChildren(node);
        try {
            //A child can either be a IntegerNode, DoubleNode, IdentifierNode, or ArithExpNode
            String leftType = node.getChildren().get(0).getType();
            String rightType = node.getChildren().get(1).getType();
            String resultType = findResultingType(leftType, rightType);
            if (resultType == null) {
                throw new ArithExpException("Types do not match for ArithExp: " + leftType + " <--> " + rightType);
            }
            node.setType(resultType);
        }
        catch (ArithExpException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArithExpException(e.getMessage() + " in ArithExpNode"));
        }
    }

    private String findResultingType(String leftType, String rightType){
        if (leftType.equals("int") && rightType.equals("int"))
        {
            return "int";
        }
        if (leftType.equals("int") && rightType.equals("double") ||
            leftType.equals("double") && rightType.equals("int")){
            return "double";
        }
        //All other cases returns null(Also where left or right type == null)
        return null;
    }

    @Override
    public void visit(NegatedBoolNode node) {
        this.visitChildren(node);
        try {
            if (node.getChildren().get(0).getType().equals("bool")) {
                node.setType("bool");
            } else {
                throw new BoolNodeException("NegatedBoolNode does not have type bool");
            }
        }
        catch (BoolNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolNodeException(e.getMessage() + " in NegatedBoolNode"));
        }
    }

    @Override
    public void visit(BoolNode node) {
        try {
            if (((BoolNode) node).getValue() == null) {
                throw new BoolNodeException("BoolNode does not have type bool");
            }
            node.setType("bool");
        }
        catch (BoolNodeException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new BoolNodeException(e.getMessage() + " in BoolNode"));
        }
    }

    @Override
    public void visit(CompareExpNode node) {
        this.visitChildren(node);
        try {
            ArithExpNode leftChild = ((ArithExpNode) node.getChildren().get(0));
            ArithExpNode rightChild = ((ArithExpNode) node.getChildren().get(1));
            if (Objects.equals(leftChild.getType(), rightChild.getType()) &&
                    Objects.equals(leftChild.getType(), "Int") || Objects.equals(leftChild.getType(), "Double")) {
                node.setType("bool");
            } else {
                throw new CompareTypeMatchingException("Type mismatch in comparison expression");
            }
        }
        catch (CompareTypeMatchingException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new CompareTypeMatchingException(e.getMessage() + " in CompareExpNode"));
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
            exceptions.add(new RuntimeException(e.getMessage() + " in WhileNode"));
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
            exceptions.add(new RuntimeException(e.getMessage() + " in ForNode"));
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
            exceptions.add(new RuntimeException(e.getMessage() + " in SelectionNode"));
        }
    }

    @Override
    public void visit(ArrayAccessNode node) {
        try{
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
        }
        catch (ArrayAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new ArrayAccessException(e.getMessage() + " in ArrayAccessNode"));
        }
    }

    @Override
    public void visit(StateAccessNode node) {
        try{
            if (!hasParent(node, ActorDclNode.class)){
                throw new StateAccessException("StateAccessNode is not a child of ActorDclNode");
            }
            if (node.getChildren().size() > 0){
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
        }
        catch (StateAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new StateAccessException(e.getMessage() + " in StateAccessNode"));
        }
    }

    @Override
    public void visit(KnowsAccessNode node) {
        try {
            if (!hasParent(node, ActorDclNode.class)) {
                throw new KnowsAccessException("KnowsAccessNode is not a child of ActorDclNode");
            }
            this.visitChildren(node);
            for (AstNode child : node.getChildren()) {
                if (!TypeContainer.hasType(child.getType())) {
                    throw new KnowsAccessException("KnowsAccessNode children does not have known type");
                }
            }
        }
        catch (KnowsAccessException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new KnowsAccessException(e.getMessage() + " in KnowsAccessNode"));
        }
    }

    @Override
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
        try {
            for (AstNode child : node.getChildren()) {
                if (!child.getType().equals("string")) {
                    throw new PrintException("Print statement only accepts string arguments");
                }
            }
        }
        catch (PrintException e) {
            exceptions.add(e);
        }
        catch (Exception e) {
            exceptions.add(new PrintException(e.getMessage() + " in PrintCallNode"));
        }
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