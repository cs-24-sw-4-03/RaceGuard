package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CodeGenVisitor implements NodeVisitor {

    private String dirPath = System.getProperty("user.dir") + "/output";

    StringBuilder stringBuilder = new StringBuilder(); // Used to generate a single line of code. Ends with a \n
    ArrayList<String> codeOutput = new ArrayList<>(); // Used to store lines of code

    int localIndent = 0; // indent for file generated. 4 spaces per indent

    private String getLine() {
        String line = stringBuilder.toString();
        stringBuilder.setLength(0); // Resets string builder
        int indent = localIndent;

        if (line.endsWith("}\n")){
            indent--;
        }
        line = line.indent(indent * 4);

        return line;
    }

    public void visit(AstNode node) {
        for (AstNode childNode : node.getChildren()) {
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
        stringBuilder.append(node.getAccessIdentifier());
        if(isGrandparentVarDclOrSiblingIdentifier(node) && accessArrayDimensions(node) == 1){
           accessArrayFirstChild(node);
        }
        else if(isGrandparentVarDclOrSiblingIdentifier(node) && accessArrayDimensions(node) == 2){
            accessArrayFirstChild(node);
            accessArraySecondChild(node);
        }
        else if(accessArrayDimensions(node) == 1){
            stringBuilder.append(".set(");
            if(node.getChildren().get(0) instanceof IdentifierNode){
                stringBuilder.append("(int) ");
            }
            visitChild(node.getChildren().get(0));
            stringBuilder.append(", ");
            visit(node.getParent().getChildren().get(1));
        } else {
            if(node.getParent() instanceof AssignNode && node.getParent().getChildren().get(0) instanceof ArrayAccessNode && node.getParent().getChildren().get(1) instanceof ArrayAccessNode){
                if(node.getParent().getChildren().getFirst() == node){
                    stringBuilder.append(".get(");
                    if(node.getChildren().get(0) instanceof IdentifierNode){
                        stringBuilder.append("(int) ");
                    }
                    visitChild(node.getChildren().get(0));
                    stringBuilder.append(").set(");
                    if(node.getChildren().get(1) instanceof IdentifierNode){
                        stringBuilder.append("(int) ");
                    }
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(", ");
                }
                else {
                    stringBuilder.append(".get(");
                    if(node.getChildren().get(0) instanceof IdentifierNode){
                        stringBuilder.append("(int) ");
                    }
                    visitChild(node.getChildren().get(0));
                    stringBuilder.append(").get(");
                    if(node.getChildren().get(1) instanceof IdentifierNode){
                        stringBuilder.append("(int) ");
                    }
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(")");
                }
            }
            else {
                stringBuilder.append(".get(");
                if(node.getChildren().get(0) instanceof IdentifierNode){
                    stringBuilder.append("(int) ");
                }
                visitChild(node.getChildren().get(0));

            }

/*
stringBuilder.append(".get(");
                if(node.getChildren().get(0) instanceof IdentifierNode){
                    stringBuilder.append("(int) ");
                }
                visitChild(node.getChildren().get(0));

                if() {
                    stringBuilder.append(").get(");
                    if(node.getChildren().get(1) instanceof IdentifierNode){
                        stringBuilder.append("(int) ");
                    }
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(")");
                }


                else {
                    stringBuilder.append(").set(");
                    if(node.getChildren().get(1) instanceof IdentifierNode){
                        stringBuilder.append("(int) ");
                    }
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(", ");
                }

 */


                /*
                visitChild(node.getChildren().get(1));
                stringBuilder.append(", ");
                visitChild(node.getParent().getChildren().get(1));


                 */


        }
    }
    private boolean isGrandparentVarDclOrSiblingIdentifier(ArrayAccessNode node){
        return node.getParent().getParent() instanceof VarDclNode || node.getParent().getChildren().get(0) instanceof IdentifierNode;

    }
    private void accessArrayFirstChild(ArrayAccessNode node){
        stringBuilder.append(".get(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
    }
    private void accessArraySecondChild(ArrayAccessNode node){
        stringBuilder.append(".get(");
        visitChild(node.getChildren().get(1));
        stringBuilder.append(")");
    }
    private int accessArrayDimensions(ArrayAccessNode node){
        if(node.getChildren().size() == 1){
            return 1;
        }
        else{
            return 2;
        }
    }
    private void resetStringBuilder(StringBuilder sb) {
        sb.setLength(0);
    }
    private void resetCodeOutput(ArrayList<String> codeOutput) {
        codeOutput.clear();
    }
    
    private void writeToFile(String fileName, ArrayList<String> codeOutput) { 
        try {
            File dir = new File(dirPath);
            if(!dir.exists()){
                dir.mkdirs();
            }

            File file = new File(dirPath + "/" + fileName + ".java");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                for (String line : codeOutput) {
                    fos.write(line.getBytes());
                }
                System.out.println("New file was created: " + file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Actor FactorialMain follows Factorial{State{}; Knows{}; Spawn{};}
    //1. Reset StringBuilder and CodeOutput
    //2. Create new File dependent on the node.getId()
    //3. visit children
    //4. Write the file
    @Override
    public void visit(ActorDclNode node) {
        resetStringBuilder(stringBuilder);
        resetCodeOutput(codeOutput);
        localIndent = 0;

        //imports necessary for most akka actor classes
        stringBuilder
        .append("import akka.actor.typed.ActorRef; \n")
        .append("import akka.actor.typed.Behavior; \n")
        .append("import akka.actor.typed.javadsl.*; \n")
        .append("import akka.actor.typed.ActorSystem; \n");

        stringBuilder.append("public class ")
            .append(node.getId())
            .append(" extends AbstractBehavior<") // Extending AbstractBehavior to manage state and behavior
            .append(" {\n");
        codeOutput.add(getLine());
        localIndent++;
        visitChildren(node);

        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLine());

        writeToFile(node.getId(), codeOutput);

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

    if(!(node.getChildren().get(1) instanceof ListNode) && !(node.getChildren().get(0) instanceof ArrayAccessNode)){
            visitChild(leftChild);
            stringBuilder.append(" = ");
            visitChild(rightChild);
    } else {

        visitChild(leftChild);
        visitChild(rightChild);
        stringBuilder.append(")");
    }
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
    //In FactorialHelper this is: private final int currentValue;
    @Override
    public void visit(StateNode node) {
        stringBuilder.append("private ");
        visitChildren(node);
        codeOutput.add(getLine());

    }

    @Override
    public void visit(BoolNode node) {

        visitChild(node.getChildren().get(0));
    }

    @Override
    public void visit(BoolAndExpNode node) {
        for(int i = 0; i < node.getChildren().size(); i ++){
            if(node.getIsParenthesized()) {
                if (i == 0) {
                    stringBuilder.append("(");
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(" && ");
                    visitChild(node.getChildren().get(i));
                    if(node.getChildren().size()-1 == node.getChildren().indexOf(node.getChildren().get(i))){
                        stringBuilder.append(")");
                    }
                }
            }
            else {
                if (i == 0) {
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(" && ");
                    visitChild(node.getChildren().get(i));
                }
            }
        }

    }

    @Override
    public void visit(BoolExpNode node) {
        for(int i = 0; i < node.getChildren().size(); i ++){
            if(node.getIsParenthesized()) {
                if (i == 0) {
                    stringBuilder.append("(");
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(" || ");
                    visitChild(node.getChildren().get(i));
                    if(node.getChildren().size()-1 == node.getChildren().indexOf(node.getChildren().get(i))){
                        stringBuilder.append(")");
                    }
                }
            }
            else {
                if (i == 0) {
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(" || ");
                    visitChild(node.getChildren().get(i));
                }
            }
        }

    }

    @Override
    public void visit(SelfNode node) {

    }

    @Override
    public void visit(SenderNode node) {

    }

    @Override
    public void visit(CompareExpNode node) {
        if(node.getIsParenthesized()){
            stringBuilder.append("(");
        }

       visitChild(node.getChildren().get(0));
       stringBuilder.append(node.getOperator());
       visitChild(node.getChildren().get(1));

       if(node.getIsParenthesized()){
              stringBuilder.append(")");
         }
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
        HashMap<String, String> HashMapConverter = new HashMap<>();
        //1 dimensional arrays
        HashMapConverter.put("int[]", "ArrayList<Integer>");
        HashMapConverter.put("double[]", "ArrayList<Double>");
        HashMapConverter.put("bool[]", "ArrayList<Boolean>");
        HashMapConverter.put("string[]", "ArrayList<String>");
        //2 dimensional arrays
        HashMapConverter.put("int[][]", "ArrayList<ArrayList<Integer>>");
        HashMapConverter.put("double[][]", "ArrayList<ArrayList<Double>>");
        HashMapConverter.put("bool[][]", "ArrayList<ArrayList<Boolean>>");
        HashMapConverter.put("string[][]", "ArrayList<ArrayList<String>>");
        String arrayType = HashMapConverter.get(node.getType());

        if(node.getType()!= null && !node.getType().equals(VariableConverter(node.getType()))){
            stringBuilder.append(VariableConverter(node.getType()));
            stringBuilder.append(" ");
            stringBuilder.append(node.getName());
        } else if(arrayType !=null){
            stringBuilder.append(arrayType);
            stringBuilder.append(" ");
            stringBuilder.append(node.getName());
            stringBuilder.append(" = new ArrayList<>();\n");


            //Check if the parent is a var dcl node and has more than one child, then append the name of the node which
            //is used to add elements to the arraylist in the ListNode
            if(node.getParent() instanceof VarDclNode && node.getParent().getChildren().size() > 1){
                stringBuilder.append(node.getName());
            }
        }
        else{
            stringBuilder.append(node.getName());
        }
    }

    @Override
    public void visit(InitializationNode node) {
        if(!(node.getChildren().get(0) instanceof ListNode)){
            stringBuilder.append(" = ");
        }
        visitChildren(node);
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
        if (!(node.getParent() instanceof AssignNode && node.getParent().getChildren().get(0) instanceof ArrayAccessNode)) {
            if (node.getChildren() != null && !(node.getParent() instanceof ListNode)) {
                stringBuilder.append(".addAll(Arrays.asList(");
            } else {
                stringBuilder.append("new ArrayList<>(Arrays.asList(");
            }
            separateElementsList(node);
        }
    }
    private void separateElementsList(ListNode node) {
        for(int i = 0; i < node.getChildren().size(); i++){
            visitChild(node.getChildren().get(i));
            if(i != node.getChildren().size()-1){
                stringBuilder.append(", ");
            }
        }
        stringBuilder.append("))");
    }

    @Override
    public void visit(LocalMethodBodyNode node) {

    }

    @Override
    public void visit(MainDclNode node) {
        resetStringBuilder(stringBuilder);
        resetCodeOutput(codeOutput);
        localIndent = 0;
        stringBuilder.append("public class Main {\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("public static void main(String[] args)");
        this.visitChildren(node);
        localIndent--;
        stringBuilder.append("\n}");
        codeOutput.add(getLine());
        writeToFile(node.getId(), codeOutput);
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
        //check if the array is a 1D array or 2D array else just normal print
        if (isOneDimensionalArray(node)) {
            printOneDimensionalArray(node);
        } else if(isTwoDimensionalArray(node)){
            printTwoDimensionalArray(node);
        }
        else {
            visitChild(node.getChildren().get(0));
        }
        visitPrintChildrenFromChildOne(node);
        stringBuilder.append(");\n");
        codeOutput.add(getLine());
    }

    //Check if the print call node is a one dimensional array
    private boolean isOneDimensionalArray(PrintCallNode node) {
        return node.getChildren().get(0) instanceof ArrayAccessNode &&
                node.getChildren().get(0).getChildren().size() == 1;
    }
    //Check if the print call node is a two dimensional array
    private boolean isTwoDimensionalArray(PrintCallNode node) {
        return node.getChildren().get(0) instanceof ArrayAccessNode &&
                node.getChildren().get(0).getChildren().size() == 2;
    }
    //Print the one dimensional array
    private void printOneDimensionalArray(PrintCallNode node){
        stringBuilder.append(((ArrayAccessNode) node.getChildren().get(0)).getAccessIdentifier());
        stringBuilder.append(".get(");
        if(node.getChildren().get(0).getChildren().get(0) instanceof IdentifierNode){ //typecast if the child is an identifier
            stringBuilder.append("(int) ");
        }
        visit(node.getChildren().get(0));
        stringBuilder.append(")");
    }
    //Print the two dimensional array
    private void printTwoDimensionalArray(PrintCallNode node){
        stringBuilder.append(((ArrayAccessNode) node.getChildren().get(0)).getAccessIdentifier());
        stringBuilder.append(".get(");
        if(node.getChildren().get(0).getChildren().get(0) instanceof IdentifierNode){ //typecast if the child is an identifier
            stringBuilder.append("(int) ");
        }
        visitChild(node.getChildren().get(0).getChildren().get(0));
        stringBuilder.append(").get(");
        if(node.getChildren().get(0).getChildren().get(1) instanceof IdentifierNode){//typecast if the child is an identifier
            stringBuilder.append("(int) ");
        }
        visitChild(node.getChildren().get(0).getChildren().get(1));
        stringBuilder.append(")");
    }
    //Visit all the children of the print call node except the first one
    private void visitPrintChildrenFromChildOne(PrintCallNode node) {
        if(node.getChildren().size() > 1){
            for(int i = 1; i < node.getChildren().size(); i++){
                stringBuilder.append(" + ");
                visitChild(node.getChildren().get(i));
            }
        }
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

        //check if there is an else statement
        if(node.getChildren().size() > 2){
            stringBuilder.append("else ");
            visitChild(node.getChildren().get(2));
        }
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
