package org.abcd.examples.ParLang;

import com.sun.source.tree.LiteralTree;
import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.*;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.util.Map;
import java.util.Objects;

public class TypeVisitor implements NodeVisitor {
    SymbolTable symbolTable;
    TypeContainer typeContainer;

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
        this.visitChildren(node);
        if (node.getId() == null){
            throw new ScriptDclException("Type is not defined for script declaration node");
        }
        node.setType(node.getId());
    }

    @Override
    public void visit(ScriptMethodNode node) {
        this.visitChildren(node);
        if (node.getType() == null){
            throw new ScriptMethodException("Type is not defined for script method node");
        }
        if (node.getMethodType() == null){
            throw new ScriptMethodException("(on/local) Method type is not defined for script method node");
        }
    }

    @Override
    public void visit(SendMsgNode node) {
        if (symbolTable.lookUpSymbol(node.getMsgName()) == null){
            throw new MethodCallException("Method: " + node.getMsgName() + " not found");
        }
        symbolTable.enterScope(node.getMsgName());
        this.visitChildren(node);
        symbolTable.leaveScope();
    }

    @Override
    public void visit(InitNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BodyNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(IdentifierNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ParametersNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SpawnActorNode node) {
        this.visitChildren(node);
        if (node.getType() == null){
            throw new SpawnActorException("Type is not defined for spawn actor node");
        }
    }

    @Override
    public void visit(MethodCallNode node) {
        if (symbolTable.lookUpSymbol(node.getMethodName()) == null){
            throw new MethodCallException("Method: " + node.getMethodName() + " not found");
        }
        symbolTable.enterScope(node.getMethodName());
        this.visitChildren(node);
        symbolTable.leaveScope();
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArgumentsNode node) {
        this.visitChildren(node);
        Map<String, Attributes> params = symbolTable.getCurrentScope().getParams();
        AstNode parent = node.getParent();
        int numOfChildren = node.getChildren().size();
        if (parent instanceof MethodCallNode){
            MethodCallNode methodCallNode = (MethodCallNode) parent;
            String methodName = methodCallNode.getMethodName();
            checkArgTypes(node, params, methodName);
        }
        else if (parent instanceof SpawnActorNode) {
            SpawnActorNode spawnActorNode = (SpawnActorNode) parent;
            String actorName = spawnActorNode.getType();
            checkArgTypes(node, params, actorName);
        }
        else if (parent instanceof SendMsgNode){
            SendMsgNode sendMsgNode = (SendMsgNode) parent;
            String msgName = sendMsgNode.getMsgName();
            checkArgTypes(node, params, msgName);
        }
        else {
            throw new ArgumentsException("Arguments node parent is not a method call, spawn actor or send message node");
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

    @Override
    public void visit(DclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(AssignNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(InitializationNode node) {
        this.visitChildren(node);
        String childType = node.getChildren().get(0).getType();
        if (childType == null) {
            throw new InitializationNodeException("Type is not defined for initialization node");
        }
        node.setType(childType);
    }

    @Override
    public void visit(ListNode node) {
        this.visitChildren(node);
        String listType = node.getChildren().get(0).getType();
        for (AstNode child : node.getChildren()) {
            if (!child.getType().equals(listType)) {
                throw new ListNodeException("List elements must be of the same type");
            }
        }
        node.setType(listType + "[]");
    }

    @Override
    public void visit(VarDclNode node) {
        this.visitChildren(node);
        String identifierType = node.getChildren().get(0).getType();
        String initType = node.getChildren().get(1).getType();
        if (!identifierType.equals(initType)) {
            throw new varDclNodeExeption("Type mismatch in declaration and initialization of variable");
        }
        node.setType(identifierType);
    }

    @Override
    public void visit(ActorDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(StateNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(FollowsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(KnowsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(MainDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SpawnDclNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ExpNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(IntegerNode node) {
        if (((IntegerNode) node).getValue() == null) {
            throw new IntegerNodeException("IntegerNode value is null");
        }
        node.setType("int");
    }

    @Override
    public void visit(DoubleNode node) {
        if (((DoubleNode) node).getValue() == null) {
            throw new DoubleNodeException("DoubleNode value is null");
        }
        node.setType("double");
    }

    @Override
    public void visit(StringNode node) {
        if (((StringNode) node).getValue() == null) {
            throw new StringNodeException("StringNode value is null");
        }
        node.setType("string");
    }

    @Override
    public void visit(BoolAndExpNode node) {
        this.visitChildren(node);
        for (AstNode child : node.getChildren()) {
            if (!child.getType().equals("bool")){
                throw new BoolExpException("all BoolAndExpNode children does not have type bool");
            }
        }
        node.setType("bool");
    }

    @Override
    public void visit(BoolExpNode node) {
        this.visitChildren(node);
        for (AstNode child : node.getChildren()) {
            if (!child.getType().equals("bool")){
                throw new BoolExpException("all BoolExpNode children does not have type bool");
            }
        }
        node.setType("bool");
    }

    @Override
    public void visit(ArithExpNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(NegatedBoolNode node) {
        this.visitChildren(node);
        if (node.getChildren().get(0).getType().equals("bool")){
            node.setType("bool");
        } else {
            throw new BoolNodeException("NegatedBoolNode does not have type bool");
        }
    }

    @Override
    public void visit(BoolNode node) {
        if(((BoolNode) node).getValue() == null){
            throw new BoolNodeException("BoolNode does not have type bool");
        }
        node.setType("bool");
    }

    @Override
    public void visit(CompareExpNode node) {
        this.visitChildren(node);
        ArithExpNode leftChild = ((ArithExpNode) node.getChildren().get(0));
        ArithExpNode rightChild = ((ArithExpNode) node.getChildren().get(1));
        if (Objects.equals(leftChild.getType(), rightChild.getType()) &&
                Objects.equals(leftChild.getType(), "Int") || Objects.equals(leftChild.getType(), "Double")){
            node.setType("bool");
        } else {
            throw new CompareTypeMatchingException("Type mismatch in comparison expression");
        }
    }

    @Override
    public void visit(IterationNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(WhileNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ForNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(AccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SelectionNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArrayAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(StateAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(KnowsAccessNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(PrintCallNode node) {
        this.visitChildren(node);
        for (AstNode child : node.getChildren()) {
            if (!child.getType().equals("string")) {
                throw new PrintException("Print statement only accepts string arguments");
            }
        }
    }


}
