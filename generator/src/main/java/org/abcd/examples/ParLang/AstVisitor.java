package org.abcd.examples.ParLang;


import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.abcd.examples.ParLang.AST.*;

//class based on ParLangBaseVisitor. ParLangBaseVisitor has  "return visitChildren(ctx);" as the body of every of the overridden methods
public class AstVisitor<T> extends ParLangBaseVisitor<T> {
    @Override public T visitInit(ParLangParser.InitContext ctx) {
        InitNode initNode=new InitNode();

        return (T) childVisitor(initNode,ctx.children.toArray(ParseTree[]::new));
    }

    private AstNode childVisitor(AstNode node, ParseTree[] children){
        for(ParseTree c:children){
            if(c.getPayload() instanceof CommonToken){//if child is a CommonToken, e.g. "{", then skip. 
                continue;
            }
            node.addChild((AstNode) visit(c));
        }
        return node;
    }

    @Override public T visitMainFunc(ParLangParser.MainFuncContext ctx) {
        MainFuncDcl main= new MainFuncDcl();

        if(!ctx.arguments().getText().equals("()")){
            main.addChild((AstNode) visit(ctx.arguments()));
        }
        if(ctx.body()!=null){
            main.addChild((AstNode) visit(ctx.body()));
        }
        return (T) main;
    }

    @Override public T visitArguments(ParLangParser.ArgumentsContext ctx) {
        return visitChildren(ctx);
    }

    @Override public T visitBody(ParLangParser.BodyContext ctx) {
        Body body=new Body();
        return (T) childVisitor(body,ctx.children.toArray(ParseTree[]::new));
    }

    @Override public T visitStatement(ParLangParser.StatementContext ctx) {
        return visit(ctx.getChild(0));
    }


    @Override public T visitArithExp(ParLangParser.ArithExpContext ctx) {
        if(ctx.getChildCount()==1){
            return visit(ctx.term(0));
        }else{
            return (T) visitArithExpChild(ctx.getChild(1),ctx,1);
        }
    }


    private AstNode visitArithExpChild(ParseTree child, ParLangParser.ArithExpContext parent, int operatorIndex){
        int termIndex=(operatorIndex-1)/2; //index of first term
        int nextOperator=operatorIndex+2;

        ArithExpression.Type operator=getArithmeticBinaryOperator(child.getText());
        Expression leftChild=(Expression) visitTerm(parent.term(termIndex));
        Expression rightChild;

        if(parent.getChild(nextOperator)!= null){ //Are there more operators in the tree?
            rightChild=(Expression)visitArithExpChild(parent.getChild(nextOperator),parent,nextOperator);
        }else {
            rightChild=(Expression)  visitTerm(parent.term(termIndex+1));
        }
        return new ArithExpression(operator,leftChild,rightChild);
    }


    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitTerm(ParLangParser.TermContext ctx) {
        int childCount= ctx.getChildCount();

        if(childCount==1){
            return visitFactor(ctx.factor(0));
        }else{
            return (T) visitTermChild(ctx.getChild(1),ctx,1);
        }
    }

    private AstNode visitTermChild(ParseTree child, ParLangParser.TermContext parent, int operatorIndex){
        int factorIndex=(operatorIndex-1)/2; //index of first factor
        int nextOperator=operatorIndex+2;

        ArithExpression.Type operator=getArithmeticBinaryOperator(child.getText());
        Expression leftChild=(Expression) visitFactor(parent.factor(factorIndex));
        Expression rightChild;

        if(parent.getChild(nextOperator)!=null){ //Are there more operators?
            rightChild=(Expression) visitTermChild( parent.getChild(nextOperator),parent,nextOperator); //add right child (operator)
        }else {
            rightChild=(Expression)  visitFactor(parent.factor(factorIndex+1));
        }
        return new ArithExpression(operator,leftChild,rightChild);
    }

    @Override public T visitFactor(ParLangParser.FactorContext ctx) {
        ParseTree child=ctx.getChild(0);
        if(child instanceof ParLangParser.NumberContext){
            return visitNumber(ctx.number());
        }else if(child.getText().equals("(")){//is child a parentheses?
                return visit(ctx.arithExp());
        }else{
            System.out.println("is this the null?");
            return null;
        }
    }

    @Override public T visitNumber(ParLangParser.NumberContext ctx) {
        if(ctx.getText().contains(".")){
            return (T) new DoubleNode(Double.parseDouble(ctx.getText()));
        }else if(ctx.getChild(0) instanceof  ParLangParser.IntegerContext){
            return visitInteger(ctx.integer());
        }else{
            return null;
        }
    }


    @Override public T visitInteger(ParLangParser.IntegerContext ctx) {

        return (T)new IntegerNode(Integer.parseInt(ctx.getText()));
    }

    @Override public T visitBoolLiteral(ParLangParser.BoolLiteralContext ctx) { return visitChildren(ctx); }

    private static ArithExpression.Type getArithmeticBinaryOperator(String operator) {
        switch (operator) {
            case "+":
                return ArithExpression.Type.PLUS;
            case  "-":
                return ArithExpression.Type.MINUS;
            case "*":
                return ArithExpression.Type.MULTIPLY;
            case "/":
                return ArithExpression.Type.DIVIDE;
            case "%":
                return ArithExpression.Type.MODULO;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }













}