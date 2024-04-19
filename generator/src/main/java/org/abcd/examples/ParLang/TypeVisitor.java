package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.DoubleNodeException;
import org.abcd.examples.ParLang.Exceptions.IntegerNodeException;
import org.abcd.examples.ParLang.Exceptions.StringNodeException;
import org.abcd.examples.ParLang.Exceptions.InitializationNodeException;
import org.abcd.examples.ParLang.Exceptions.ListNodeException;
import org.abcd.examples.ParLang.Exceptions.PrintException;
import org.abcd.examples.ParLang.Exceptions.varDclNodeExeption;

import org.abcd.examples.ParLang.symbols.SymbolTable;

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
    }

    @Override
    public void visit(MethodCallNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArgumentsNode node) {
        this.visitChildren(node);
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
    public void visit(BoolExpNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(ArithExpNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(NegatedBoolNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(BoolNode node) {
        this.visitChildren(node);
    }

    @Override
    public void visit(CompareExpNode node) {
        this.visitChildren(node);
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
