package org.abcd.examples.ParLang;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.abcd.examples.ParLang.AstNodes.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AstVisitor extends ParLangBaseVisitor<AstNode> {

    List<String> typeContainer = new ArrayList<String>(Arrays.asList("INT", "DOUBLE", "STRING", "BOOL", "VOID", "ACTOR", "ARRAY", "SCRIPT"));
    @Override public AstNode visitInit(ParLangParser.InitContext ctx) {
        InitNode initNode=new InitNode();
        return childVisitor(initNode,ctx.children);
    }

    private AstNode childVisitor(AstNode node, List<ParseTree> children){
        for(ParseTree c:children){
            if(c.getPayload() instanceof CommonToken){//if child is a CommonToken, e.g. "{", then skip.
                continue;
            }
            //print can be used for debugging
            //System.out.println(c.getText());
            node.addChild( visit(c));
        }
        return node;
    }

    @Override public AstNode visitMainFunc(ParLangParser.MainFuncContext ctx) {
        MainDclNode main= new MainDclNode(ctx.MAIN().getText());

        if(!ctx.parameters().getText().equals("()")){
            main.addChild(visit(ctx.parameters()));//parameters not handled yet. The idea is to have arguments as children to the main node.
        }
        if(ctx.body()!=null){
            main.addChild(visit(ctx.body()));
        }
        return main;
    }

    @Override public AstNode visitMethodCall(ParLangParser.MethodCallContext ctx) {
        MethodCallNode methodCallNode = new MethodCallNode(ctx.identifier().getText());
        if(ctx.arguments()!=null){ //If there are arguments
            return childVisitor(methodCallNode,ctx.children);
        }
            return methodCallNode;
    }

    @Override public AstNode visitParameters(ParLangParser.ParametersContext ctx){
        int numOfChildren=ctx.getChildCount();
        ParametersNode params = new ParametersNode();
        if (numOfChildren != 2){ //there are minimum 2 children, the parentheses
            //If there are more than 2 children, there are parameters
            for (int i = 1; i < numOfChildren; i+=3){
                params.addChild(new IdentifierNode(ctx.getChild(i+1).getText(), ctx.getChild(i).getText().toUpperCase()));
            }
        }
        return params;
    }
    @Override public AstNode visitArguments(ParLangParser.ArgumentsContext ctx){
        ArgumentsNode args = new ArgumentsNode();
        return childVisitor(args,ctx.children);
    }

    @Override public AstNode visitActor(ParLangParser.ActorContext ctx) {
        ActorDclNode node=new ActorDclNode(ctx.identifier().getText());
        typeContainer.add(ctx.identifier().getText().toUpperCase());

        List<ParseTree> children=new ArrayList<ParseTree>(ctx.children);
        children.remove(1);//remove identifier from list of children
        return childVisitor(node,children);
    }

    @Override public AstNode visitActorState(ParLangParser.ActorStateContext ctx) {
        ActorStateNode node= new ActorStateNode(ctx.STATE().getText());
        return childVisitor(node,ctx.children);
    }

    @Override public AstNode visitActorKnows(ParLangParser.ActorKnowsContext ctx) {
        int numOfChildren=ctx.getChildCount();
        KnowsNode knowsNode= new KnowsNode(ctx.KNOWS().getText());
        if (numOfChildren != 3){ //there are minimum 3 children, the parentheses and "knows" token
            //If there are more than 3 children, there are known actors
            for (int i = 2; i < numOfChildren; i+=3){
                knowsNode.addChild(new ActorIdentifierNode(ctx.getChild(i+1).getText(), ctx.getChild(i).getText()));
            }
        }
        return knowsNode;
    }

    @Override public AstNode visitSpawn(ParLangParser.SpawnContext ctx) {
        SpawnDclNode node= new SpawnDclNode();
        if(!ctx.parameters().getText().equals("()")){
            node.addChild(visit(ctx.parameters()));//parameters not handled yet. The idea is to have arguments as children to the main node.
        }
        if(ctx.body()!=null){
            node.addChild(visit(ctx.body()));
        }
        return node;
    }

    @Override public AstNode visitOnMethod(ParLangParser.OnMethodContext ctx) {
        MethodDclNode node= new MethodDclNode(ctx.identifier().getText(),"void",ctx.ON_METHOD().getText());
        if (ctx.parameters() != null) {
            node.addChild(visit(ctx.parameters()));
        }
        if(ctx.body()!=null){
            node.addChild(visit(ctx.body()));
        }
        return node;
    }


    @Override public AstNode visitLocalMethod(ParLangParser.LocalMethodContext ctx) {
        MethodDclNode node= new MethodDclNode(ctx.identifier().getText(),ctx.allTypes().getText(),ctx.LOCAL_METHOD().getText());
        if (ctx.parameters() != null) {
            node.addChild(visit(ctx.parameters()));
        }
        if(ctx.localMethodBody()!=null){
            node.addChild(visit(ctx.localMethodBody()));
        }
        return node;
    }
    
    @Override public AstNode visitBody(ParLangParser.BodyContext ctx) {
        BodyNode bodyNode =new BodyNode();
        return childVisitor(bodyNode,ctx.children);
    }

    @Override
    public AstNode visitDeclaration(ParLangParser.DeclarationContext ctx) {
        VarDclNode dclNode=new VarDclNode(ctx.identifier().getText(),ctx.allTypes().getText().toUpperCase());

        IdentifierNode idNode=new IdentifierNode(ctx.identifier().getText(),ctx.allTypes().getText().toUpperCase());

        dclNode.addChild(idNode); //add identifier as child

        ParLangParser.InitializationContext init=ctx.initialization();
        if(init!=null){//variable is initialized
            InitializationNode initializationNode=new InitializationNode();
            initializationNode.addChild(visit(init.getChild(1)));
            dclNode.addChild(initializationNode); //add initializationNode as child
        }
        return dclNode;
    }

    @Override
    public AstNode visitAssignment(ParLangParser.AssignmentContext ctx) {
        AssignNode assignNode = new AssignNode();

        AstNode varNode=visit(ctx.getChild(0));
        AstNode valueNode=visit(ctx.getChild(2));

        assignNode.addChild(varNode);
        assignNode.addChild(valueNode);

        return assignNode;
    }

    @Override
    public AstNode visitIdentifier(ParLangParser.IdentifierContext ctx) {
        return new IdentifierNode(ctx.IDENTIFIER().getText());
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
        AstNode leftChild= visit(parent.term(termIndex));
        AstNode rightChild;

        if(parent.getChild(nextOperator)!= null){ //Are there more operators in the tree?
            rightChild=visitArithExpChild(parent.getChild(nextOperator),parent,nextOperator);
        }else {
            rightChild=visit(parent.term(termIndex+1));
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
        AstNode leftChild=visit(parent.factor(factorIndex));
        AstNode rightChild;

        if(parent.getChild(nextOperator)!=null){ //Are there more operators?
            rightChild=visitTermChild( parent.getChild(nextOperator),parent,nextOperator); //add right child (operator)
        }else {
            rightChild=visit(parent.factor(factorIndex+1));
        }
        return new ArithExprNode(operator,leftChild,rightChild);
    }

    @Override public AstNode visitFactor(ParLangParser.FactorContext ctx) {
        if (ctx.getChild(0).getText().equals("(")) {
            return visit(ctx.arithExp());//If first child is a parentheses, treat the node as arithmetic expression
        }
        return visit(ctx.getChild(0));
    }

    @Override public AstNode visitUnaryExp(ParLangParser.UnaryExpContext ctx) {
        UnaryExpNode unaryExpNode = new UnaryExpNode();
        if(ctx.getChild(0).getText().equals("-")){
            unaryExpNode.setIsNegated(true);
        }
        List<ParseTree> children=new ArrayList<ParseTree>(ctx.children);
        children.remove(0);//remove the negation token
        return childVisitor(unaryExpNode,children);
    }

    @Override public AstNode visitNumber(ParLangParser.NumberContext ctx) {
        if(ctx.getText().contains(".")){
            return new DoubleNode(Double.parseDouble(ctx.getText()));
        }else {
            return new IntegerNode(Integer.parseInt(ctx.getText()));
        }
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

    @Override public AstNode visitWhileLoop(ParLangParser.WhileLoopContext ctx) {
        WhileNode whileNode=new WhileNode();
        return childVisitor(whileNode,ctx.children);
    }

    @Override public AstNode visitForLoop(ParLangParser.ForLoopContext ctx) {
        ForNode forNode=new ForNode();
        return childVisitor(forNode,ctx.children);
    }
    @Override public AstNode visitPrimitive(ParLangParser.PrimitiveContext ctx){
        //Primitives can be: INT, DOUBLE, STRING, and BOOL

        //In case the primitive is a STRING
        if(ctx.STRING() != null) {
            return new StringNode(ctx.getText());
        }
        return null;
    }

    @Override public AstNode visitLocalMethodBody(ParLangParser.LocalMethodBodyContext ctx){
        LocalMethodBodyNode methodBodyNode = new LocalMethodBodyNode();
        return childVisitor(methodBodyNode,ctx.children);
    }

    @Override public AstNode visitReturnStatement(ParLangParser.ReturnStatementContext ctx){
        ReturnStatementNode returnStatementNode = new ReturnStatementNode();
        if(ctx.getChild(1) != null){
            returnStatementNode.addChild(visit(ctx.getChild(1)));
        }
        return returnStatementNode;
    }
}