package org.abcd.examples.ParLang;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.abcd.examples.ParLang.AstNodes.*;

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

    @Override public AstNode visitActor(ParLangParser.ActorContext ctx) {
        System.out.println("visit Actor");
        ActorDclNode node=new ActorDclNode(ctx.identifier().getText());
        return childVisitor(node,ctx.children.toArray(ParseTree[]::new));
    }

    @Override public AstNode visitActorState(ParLangParser.ActorStateContext ctx) {
        ActorStateNode node= new ActorStateNode();

        return childVisitor(node,ctx.children.toArray(ParseTree[]::new));
    }

    @Override public AstNode visitActorKnows(ParLangParser.ActorKnowsContext ctx) {
        System.out.println("visiting knows?");
        KnowsNode node= new KnowsNode();

        return childVisitor(node,ctx.children.toArray(ParseTree[]::new));
    }

    @Override public AstNode visitSpawn(ParLangParser.SpawnContext ctx) {
        SpawnNode node= new SpawnNode();

        if(!ctx.parameters().getText().equals("()")){
            node.addChild(visit(ctx.parameters()));//parameters not handled yet. The idea is to have arguments as children to the main node.
        }
        if(ctx.body()!=null){
            node.addChild(visit(ctx.body()));
        }
        return node;
    }

    @Override public AstNode visitOnMethod(ParLangParser.OnMethodContext ctx) {
        MethodDclNode node= new MethodDclNode(ctx.identifier().getText(),"void","on");
        if (ctx.parameters() != null) {
            node.addChild(visit(ctx.parameters()));
        }
        if(ctx.body()!=null){
            node.addChild(visit(ctx.body()));
        }
        return node;
    }


    @Override public AstNode visitLocalMethod(ParLangParser.LocalMethodContext ctx) {
        MethodDclNode node= new MethodDclNode(ctx.identifier().getText(),ctx.allTypes().getText(),"local");
        if (ctx.parameters() != null) {
            node.addChild(visit(ctx.parameters()));
        }
        if(ctx.body()!=null){
            node.addChild(visit(ctx.body()));
        }
        return node;
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

        ArithExprNode.OpType operator=getArithmeticBinaryOperator(child.getText());
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

        ArithExprNode.OpType operator=getArithmeticBinaryOperator(child.getText());
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

    private static ArithExprNode.OpType getArithmeticBinaryOperator(String operator) {
        switch (operator) {
            case "+":
                return ArithExprNode.OpType.PLUS;
            case  "-":
                return ArithExprNode.OpType.MINUS;
            case "*":
                return ArithExprNode.OpType.MULTIPLY;
            case "/":
                return ArithExprNode.OpType.DIVIDE;
            case "%":
                return ArithExprNode.OpType.MODULO;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }
    @Override public AstNode visitPrimitive(ParLangParser.PrimitiveContext ctx){
        //Primitives can be: INT, DOUBLE, STRING, and BOOL

        //In case the primitive is a STRING
        if(ctx.STRING() != null) {
            return new StringNode(ctx.getText());
        }
        return null;
    }


}