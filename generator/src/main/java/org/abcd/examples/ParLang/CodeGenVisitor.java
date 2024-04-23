package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

import java.io.*;
import java.util.ArrayList;

public class CodeGenVisitor implements NodeVisitor {


    String filePath = System.getProperty("user.dir") + "/output/Main.java";

    String dirPath = System.getProperty("user.dir") + "/output";


    StringBuilder stringBuilder = new StringBuilder();
    ArrayList<String> codeOutput = new ArrayList<>();

    int localIndent = 0; //indent for akka file generated

    public void generate() throws IOException {
        codeOutput.add(getLine());

        for (String line : codeOutput) {
            stringBuilder.append(line);
        }

        File dir = new File(dirPath);

        if(!dir.exists()){
            dir.mkdirs();
        }


        File file = new File(filePath);
        System.out.println(file);

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        fileOutputStream.write(stringBuilder.toString().getBytes());

    }

    private String getLine(){
        String line = stringBuilder.toString();
        stringBuilder.setLength(0); // Resets string builder
        int indent = localIndent;

        if (line.endsWith("}\n") || line.endsWith("}")){
            indent--;
        }
        line = line.indent(indent * 4);

        return line;
    }
    public void visit(AstNode node) {
        for (AstNode childNode : node.getChildren()) {
            System.out.println(childNode.getChildren());
            childNode.accept(this);
        }
    }

    private String VariableConverter(String type){
        switch (type) {
            case "int":
                return "long";
            case "double":
                return "double";
            case "bool":
                return "boolean";
            case "string":
                return "String";
            default:
                return type;
        }
    }

    public void visitChild(AstNode node){
        node.accept(this);
    }
    @Override
    public void visitChildren(AstNode node) {
        for (AstNode childNode : node.getChildren()) {
            childNode.accept(this);
        }

    }

    @Override
    public void visit(AccessNode node) {

    }

    @Override
    public void visit(ArrayAccessNode node) {

    }
    //Actor FactorialMain follows Factorial{State{}; Knows{}; Spawn{};}
    @Override
    public void visit(ActorDclNode node) {

    }

    @Override
    public void visit(ArgumentsNode node) {

    }

    @Override
    public void visit(ArithExpNode node) {
        AstNode leftChild = node.getChildren().get(0);
        AstNode rightChild = node.getChildren().get(1);

        if(node.getIsParenthesized()){
            stringBuilder.append("(");
            visitChild(leftChild);
            stringBuilder.append(" " + node.getOpType().getValue() + " ");
            visitChild(rightChild);
            stringBuilder.append(")");
        }
        else{
            visitChild(leftChild);
            stringBuilder.append(" " + node.getOpType().getValue() + " ");
            visitChild(rightChild);
        }

    }

    @Override
    public void visit(AssignNode node) {
    AstNode leftChild = node.getChildren().get(0);
    AstNode rightChild = node.getChildren().get(1);

    visitChild(leftChild);
    stringBuilder.append(" = ");
    visitChild(rightChild);
    if(!(node.getParent() instanceof ForNode)){ //if the parent is not a for node, add a semicolon, else don't
        stringBuilder.append(";\n");
        codeOutput.add(getLine());
        }

    }

    @Override
    public void visit(BodyNode node) {
        stringBuilder.append("{\n");
        codeOutput.add(getLine());
        localIndent++;

        this.visitChildren(node);

        stringBuilder.append("}\n");
        codeOutput.add(getLine());
    }

    @Override
    public void visit(StateNode node) {

    }

    @Override
    public void visit(BoolNode node) {
        visitChild(node.getChildren().get(0));
    }

    @Override
    public void visit(CompareExpNode node) {
       visitChild(node.getChildren().get(0));
       stringBuilder.append(node.getOperator());
       visitChild(node.getChildren().get(1));
    }


    @Override
    public void visit(DoubleNode node) {
        stringBuilder.append(node.getValue());

    }

    @Override
    public void visit(FollowsNode node) {

    }

    //for loop construction, can be either of the following:
    //for (VarDclNode;CompareExpNode; AssignNode) {BodyNode}
    //for (VarDclNode;CompareExpNode;Empty) {BodyNode}
    //for (Empty;CompareExpNode; AssignNode) {BodyNode}
    //for (Empty;CompareExpNode;Empty) {BodyNode}
    @Override
    public void visit(ForNode node) {
        stringBuilder.append("for(");
        //check if the second child is a compare expression and the third child is an assign node
        if (node.getChildren().get(1) instanceof CompareExpNode && node.getChildren().get(2) instanceof AssignNode) {
            visitChild(node.getChildren().get(0));
            stringBuilder.append("; ");
            visitChild(node.getChildren().get(1));
            stringBuilder.append("; ");
            visitChild(node.getChildren().get(2));
            stringBuilder.append(")");
            visitChild(node.getChildren().get(3));
        } else { //check if the first child is a compare expression
            if (node.getChildren().get(0) instanceof CompareExpNode) {
                stringBuilder.append("; ");
                visitChild(node.getChildren().get(0));
                stringBuilder.append(" ;");
                if(!(node.getChildren().get(1) instanceof AssignNode)){ //check if the second child is not an assign node
                    stringBuilder.append(")");
                    visitChild(node.getChildren().get(1));
                }else{ //check if the second child is an assign node
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(")");
                    visitChild(node.getChildren().get(2));
                }
            }
            //check if the second child is a compare, then it knows that first is a var dcl and third is the body node
            if(node.getChildren().get(1) instanceof CompareExpNode){
                visitChild(node.getChildren().get(0));
                stringBuilder.append("; ");
                visitChild(node.getChildren().get(1));
                stringBuilder.append(" ;)");
                visitChild(node.getChildren().get(2));
            }
        }
        codeOutput.add(getLine());
    }

    @Override
    public void visit(IdentifierNode node) {
        if(node.getType()!= null){
            stringBuilder.append(VariableConverter(node.getType()));
            stringBuilder.append(" ");
        }
        stringBuilder.append(node.getName());
    }


    @Override
    public void visit(InitializationNode node) {
        stringBuilder.append(" = ");
        this.visitChildren(node);
    }

    @Override
    public void visit(InitNode node) {


    }

    @Override
    public void visit(IntegerNode node) {
        stringBuilder.append(node.getValue());

    }

    @Override
    public void visit(IterationNode node) {

    }

    @Override
    public void visit(KnowsAccessNode node) {

    }

    @Override
    public void visit(KnowsNode node) {

    }

    @Override
    public void visit(ListNode node) {

    }

    @Override
    public void visit(LocalMethodBodyNode node) {

    }

    @Override
    public void visit(MainDclNode node) {
        stringBuilder.append("public static void main(String[] args)");
        this.visitChildren(node);
    }


    @Override
    public void visit(MethodCallNode node) {

    }

    @Override
    public void visit(MethodDclNode node) {

    }

    @Override
    public void visit(NegatedBoolNode node) {
        stringBuilder.append("!");
        visitChild(node.getChildren().get(0));
    }

    @Override
    public void visit(ParametersNode node) {

    }

    @Override
    public void visit(PrintCallNode node) {
        stringBuilder.append("System.out.println(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(");\n");
        codeOutput.add(getLine());
    }

    @Override
    public void visit(ReturnStatementNode node) {

    }

    @Override
    public void visit(ScriptDclNode node) {

    }

    @Override
    public void visit(ScriptMethodNode node) {

    }

    //DOES NOT WORK
    @Override
    public void visit(SelectionNode node) {
        stringBuilder.append("if(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
        visitChild(node.getChildren().get(1));

        for(int counter = 2; counter < node.getChildren().size(); counter+=2){
            if(node.getChildren().size() > 2 && counter < node.getChildren().size()-1){
                stringBuilder.append("else if(");
                visitChild(node.getChildren().get(counter));
                stringBuilder.append(")");
                visitChild(node.getChildren().get(counter+1));
            }}
        //check if there is an else statement
        if(node.getChildren().size()-2 > 0){
            stringBuilder.append("else");
            visitChild(node.getChildren().get(node.getChildren().size()-1));
        }
        codeOutput.add(getLine());

    }

    @Override
    public void visit(SendMsgNode node) {

    }

    @Override
    public void visit(SpawnActorNode node) {

    }

    @Override
    public void visit(SpawnDclNode node) {

    }

    @Override
    public void visit(ExpNode node) {

    }

    @Override
    public void visit(StateAccessNode node) {

    }

    @Override
    public void visit(StringNode node) {
    stringBuilder.append(node.getValue());
    }


    @Override
    public void visit(VarDclNode node) {
        visitChildren(node);
        if(!(node.getParent() instanceof ForNode)){ //if the parent is not a for node, add a semicolon, else don't
                stringBuilder.append(";\n");
                codeOutput.add(getLine());
            }
    }

    @Override
    public void visit(WhileNode node) {
        stringBuilder.append("while(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
        visitChild(node.getChildren().get(1));
        stringBuilder.append("\n");
        codeOutput.add(getLine());
    }
}
