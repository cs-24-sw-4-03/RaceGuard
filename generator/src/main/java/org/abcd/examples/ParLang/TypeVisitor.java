package org.abcd.examples.ParLang;

import com.sun.source.tree.LiteralTree;
import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.*;
import org.abcd.examples.ParLang.symbols.SymbolTable;

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
    }

    @Override
    public void visit(ScriptMethodNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(SendMsgNode node) {
        this.visitChildren(node);
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
        node.setType(symbolTable.lookUpSymbol(node.getName()).getVariableType());
    }

    @Override
    public void visit(ParametersNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ReturnStatementNode node) {
        this.visitChildren(node);
        node.setType(node.getChildren().get(0).getType());
        if (node.getType() == null) {
            throw new ReturnNodeException("Type is not defined for return statement");
        }
    }

    @Override
    public void visit(SpawnActorNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(MethodCallNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
        node.setType(node.getChildren().get(node.getChildren().size()-1).getType());
        if (node.getType() == null) {
            throw new LocalMethodBodyNodeException("Return type is not defined for local method body node");
        }
    }

    @Override
    public void visit(ArgumentsNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(DclNode node) {
        this.visitChildren(node);
        String identifierType = node.getChildren().get(0).getType();
        String initType = node.getChildren().get(1).getType();
        if (!identifierType.equals(initType)) {
            throw new varDclNodeExeption("Type mismatch in declaration DclNode");
        }
        node.setType(identifierType);
    }

    @Override
    public void visit(AssignNode node) {
        this.visitChildren(node);
        String identifierType = node.getChildren().get(0).getType();
        String assignType = node.getChildren().get(1).getType();
        if (!identifierType.equals(assignType)) {
            throw new AssignExecption("Type mismatch in assignment");
        }
        node.setType(identifierType);
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
        String childType = node.getChildren().get(1).getType();
        if (node.getType().equals(childType)) {
            throw new MethodDclNodeException("Return does not match returnType of method");
        }
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
        //A child can either be a IntegerNode, DoubleNode, IdentifierNode, or ArithExpNode
        String leftType = node.getChildren().get(0).getType();
        String rightType = node.getChildren().get(1).getType();
        String resultType = findResultingType(leftType,rightType);
        if(resultType == null){
            throw new ArithExpException("Types do not match for ArithExp: " + leftType + " <--> " + rightType);
        }
        node.setType(resultType);
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