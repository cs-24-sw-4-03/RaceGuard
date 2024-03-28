package org.abcd.examples.ParLang;


import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.abcd.examples.ParLang.AST.*;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;

public class AstVisitor<T> extends ParLangBaseVisitor<T> {
    @Override public T visitInit(ParLangParser.InitContext ctx) {
        InitNode initNode=new InitNode();

        return (T) childVisitor(initNode,ctx.children.toArray(ParseTree[]::new));
    }

    private AstNode childVisitor(AstNode node, ParseTree[] children){
        for(ParseTree c:children){
            if(c.getPayload() instanceof CommonToken){
                continue;
            }
            node.children.add((AstNode) visit(c));
        }
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitValue(ParLangParser.ValueContext ctx) {
        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDeclaration(ParLangParser.DeclarationContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitArrayAssign(ParLangParser.ArrayAssignContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitArrayAssignLength(ParLangParser.ArrayAssignLengthContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitIntegerList(ParLangParser.IntegerListContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitDoubleList(ParLangParser.DoubleListContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitBoolList(ParLangParser.BoolListContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitStringList(ParLangParser.StringListContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitBoolExp(ParLangParser.BoolExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitBoolAndExp(ParLangParser.BoolAndExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitBoolTerm(ParLangParser.BoolTermContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitCompareExp(ParLangParser.CompareExpContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitArithExp(ParLangParser.ArithExpContext ctx) {
        if(ctx.getChildCount()==1){
            return visit(ctx.term(0));
        }else{
            return (T) visitArithExpChild(ctx.getChild(1),ctx,1);
        }
    }

/*
            switch (child.getText()){
        case "+":
            node=new AddNode();
            break;
        case "-":
            node=new SubNode();
            break;
        default:
            return null;
    }
               node.children.add((AstNode) visitTerm(parent.term(termIndex))); //add left child(term)
            node.children.add(visitArithExpChild(parent.getChild(nextOperator),parent,nextOperator)); //add right child (operator)
    */

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
    /*
            AstNode node;
        switch (child.getText()){
            case "*":
                node=new MultNode();
                break;
            case "/":
                node=new DivNode();
                break;
            case "%":
                node=new ModNode();
                break;
            default:
                return null;
        }
        if(parent.getChild(nextOperator)!=null){ //Are there more operators?
            node.children.add((AstNode) visitTermChild( parent.getChild(nextOperator),parent,nextOperator)); //add right child (operator)
        }else {
            node.children.add((AstNode) visitFactor(parent.factor(factorIndex+1)));
        }
     */

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

    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitFactor(ParLangParser.FactorContext ctx) {
        ParseTree child=ctx.getChild(0);
        if(child instanceof ParLangParser.NumberContext){
            return visitNumber(ctx.number());
        }else if(child.getText().equals("(")){//is child a parentheses?
                return visit(ctx.arithExp());
        }else{
            return null;
        }
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitNumber(ParLangParser.NumberContext ctx) {
        if(ctx.getText().contains(".")){
            return (T) new DoubleNode(Double.parseDouble(ctx.getText()));
        }else if(ctx.getChild(0) instanceof  ParLangParser.IntegerContext){
            return visitInteger(ctx.integer());
        }else{
            return null;
        }
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitCompareOperator(ParLangParser.CompareOperatorContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitCompareEqNEg(ParLangParser.CompareEqNEgContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitCompareOther(ParLangParser.CompareOtherContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitPrimitiveType(ParLangParser.PrimitiveTypeContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitPrimitive(ParLangParser.PrimitiveContext ctx) { return visitChildren(ctx); }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
    @Override public T visitInteger(ParLangParser.IntegerContext ctx) {

        return visitChildren(ctx);
    }
    /**
     * {@inheritDoc}
     *
     * <p>The default implementation returns the result of calling
     * {@link #visitChildren} on {@code ctx}.</p>
     */
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