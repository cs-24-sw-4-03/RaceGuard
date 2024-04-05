package org.abcd.examples.ParLang;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.abcd.examples.ParLang.AstNodes.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AstVisitor extends ParLangBaseVisitor<AstNode> {
    @Override public AstNode visitInit(ParLangParser.InitContext ctx) {
        InitNode initNode=new InitNode();

        return childVisitor(initNode,ctx.children.toArray(ParseTree[]::new));
    }

    private AstNode childVisitor(AstNode node, ParseTree[] children){
        for(ParseTree c:children){
            if(c.getPayload() instanceof CommonToken){//if child is a CommonToken, e.g. "{", then skip.
                continue;
            }
            node.addChild( visit(c));
        }
        return node;
    }

    @Override public AstNode visitMainFunc(ParLangParser.MainFuncContext ctx) {
        MainDclNode main= new MainDclNode();

        if(!ctx.parameters().getText().equals("()")){
            main.addChild(visit(ctx.parameters()));//parameters not handled yet. The idea is to have arguments as children to the main node.
        }
        if(ctx.body()!=null){
            main.addChild(visit(ctx.body()));
        }
        return main;
    }

    @Override public AstNode visitParameters(ParLangParser.ParametersContext ctx){
        System.out.println("KIGHER: " + ctx.parent);
        System.out.println("KIGHER: " + ctx.parent.getPayload());
        int numOfChildren=ctx.getChildCount();
        ParametersNode params = new ParametersNode();
        if (numOfChildren != 2){ //there are minimum 2 children, the parentheses
            //If there are more than 2 children, there are parameters
            for (int i = 1; i < numOfChildren; i+=3){
                params.addChild(new IdentifierNode(ctx.getChild(i+1).getText(), LanguageType.valueOf(ctx.getChild(i).getText().toUpperCase())));
            }
        }
        return params;
    }

    @Override public AstNode visitBody(ParLangParser.BodyContext ctx) {
        BodyNode bodyNode =new BodyNode();
        return childVisitor(bodyNode,ctx.children.toArray(ParseTree[]::new));
    }

    @Override public AstNode visitStatement(ParLangParser.StatementContext ctx) {
        return visit(ctx.getChild(0));//if statement has more than one child, the second one is ";". We just visit the  child always.
    }


    @Override public AstNode visitArithExp(ParLangParser.ArithExpContext ctx) {
        if(ctx.getChildCount()==1){
            return visit(ctx.term(0));
        }else{
            return visitArithExpChild(ctx.getChild(1),ctx,1);
        }
    }

    private AstNode visitArithExpChild(ParseTree child, ParLangParser.ArithExpContext parent, int operatorIndex){
        int termIndex=(operatorIndex-1)/2; //index of first term in a list of just the terms (not including operators).
        int nextOperator=operatorIndex+2;

        ArithExprNode.Type operator=getArithmeticBinaryOperator(child.getText());
        ExprNode leftChild=(ExprNode) visit(parent.term(termIndex));
        ExprNode rightChild;

        if(parent.getChild(nextOperator)!= null){ //Are there more operators in the tree?
            rightChild=(ExprNode)visitArithExpChild(parent.getChild(nextOperator),parent,nextOperator);
        }else {
            rightChild=(ExprNode)  visit(parent.term(termIndex+1));
        }
        return new ArithExprNode(operator,leftChild,rightChild);
    }


    @Override public AstNode visitTerm(ParLangParser.TermContext ctx) {
        int childCount= ctx.getChildCount();

        if(childCount==1){
            return visit(ctx.factor(0));
        }else{
            return visitTermChild(ctx.getChild(1),ctx,1);
        }
    }

    private AstNode visitTermChild(ParseTree child, ParLangParser.TermContext parent, int operatorIndex){
        int factorIndex=(operatorIndex-1)/2; //index of first factor in a list of just the factors
        int nextOperator=operatorIndex+2;

        ArithExprNode.Type operator=getArithmeticBinaryOperator(child.getText());
        ExprNode leftChild=(ExprNode) visit(parent.factor(factorIndex));
        ExprNode rightChild;

        if(parent.getChild(nextOperator)!=null){ //Are there more operators?
            rightChild=(ExprNode) visitTermChild( parent.getChild(nextOperator),parent,nextOperator); //add right child (operator)
        }else {
            rightChild=(ExprNode)  visit(parent.factor(factorIndex+1));
        }
        return new ArithExprNode(operator,leftChild,rightChild);
    }

    @Override public AstNode visitFactor(ParLangParser.FactorContext ctx) {
        ParseTree child=ctx.getChild(0);
        if(child instanceof ParLangParser.NumberContext){
            return visit(ctx.number());
        }else if(child.getText().equals("(")){//If first child is a parentheses, treat the node as arithmetic expression
                return visit(ctx.arithExp());
        }else{
            return null;
        }
    }

    @Override public AstNode visitNumber(ParLangParser.NumberContext ctx) {
        if(ctx.getText().contains(".")){
            return new DoubleNode(Double.parseDouble(ctx.getText()));
        }else if(ctx.getChild(0) instanceof  ParLangParser.IntegerContext){
            return visit(ctx.integer());
        }else{
            return null;
        }
    }


    @Override public AstNode visitInteger(ParLangParser.IntegerContext ctx) {
        return new IntegerNode(Integer.parseInt(ctx.getText()));
    }

    private static ArithExprNode.Type getArithmeticBinaryOperator(String operator) {
        switch (operator) {
            case "+":
                return ArithExprNode.Type.PLUS;
            case  "-":
                return ArithExprNode.Type.MINUS;
            case "*":
                return ArithExprNode.Type.MULTIPLY;
            case "/":
                return ArithExprNode.Type.DIVIDE;
            case "%":
                return ArithExprNode.Type.MODULO;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }
}