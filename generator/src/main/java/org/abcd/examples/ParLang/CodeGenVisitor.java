package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class CodeGenVisitor implements NodeVisitor {


    String filePath = System.getProperty("user.dir") + "/output/Main.java";

    String dirPath = System.getProperty("user.dir") + "/output";

    StringBuilder stringBuilder = new StringBuilder();


    public void generate() throws IOException {
        File dir = new File(dirPath);

        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(filePath);
        System.out.println(file);

        FileOutputStream fileOutputStream = new FileOutputStream(file);

        fileOutputStream.write(stringBuilder.toString().getBytes());

    }

    public void visit(AstNode node) {
        for (AstNode childNode : node.getChildren())     {
            System.out.println(childNode.getChildren());
            childNode.accept(this);
        }

    }

    @Override
    public void visitChildren(AstNode node) {

    }

    @Override
    public void visit(AccessNode node) {

    }

    @Override
    public void visit(ActorDclNode node) {

    }

    @Override
    public void visit(ActorIdentifierNode node) {

    }

    @Override
    public void visit(ActorStateNode node) {

    }

    @Override
    public void visit(ArgumentsNode node) {

    }

    @Override
    public void visit(ArithExprNode node) {

    }

    @Override
    public void visit(AssignNode node) {

    }

    @Override
    public void visit(BodyNode node) {
        System.out.println("BODY NODE");

    }

    @Override
    public void visit(BoolNode node) {

    }

    @Override
    public void visit(CompareExpNode node) {

    }

    @Override
    public void visit(DclNode node) {

    }

    @Override
    public void visit(DoubleNode node) {

    }

    @Override
    public void visit(ExprNode node) {

    }

    @Override
    public void visit(FollowsNode node) {

    }

    @Override
    public void visit(ForNode node) {

    }

    @Override
    public void visit(IdentifierNode node) {

    }

    @Override
    public void visit(InitializationNode node) {

    }

    @Override
    public void visit(InitNode node) {
        System.out.println("ÌNIT NODE");

    }

    @Override
    public void visit(IntegerNode node) {

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
        System.out.println("VISIT MAIN!!!");
    }

    @Override
    public void visit(MethodCallNode node) {

    }

    @Override
    public void visit(MethodDclNode node) {

    }

    @Override
    public void visit(NegatedBoolNode node) {

    }

    @Override
    public void visit(ParametersNode node) {

    }

    @Override
    public void visit(PrintCallNode node) {

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

    @Override
    public void visit(SelectionNode node) {

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
    public void visit(StateAccessNode node) {

    }

    @Override
    public void visit(StringNode node) {

    }

    @Override
    public void visit(UnaryExpNode node) {

    }

    @Override
    public void visit(VarDclNode node) {

    }

    @Override
    public void visit(WhileNode node) {

    }
}
