package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.io.*;
import java.util.ArrayList;

public class CodeGenVisitor implements NodeVisitor {

    SymbolTable symbolTable;

    public CodeGenVisitor(SymbolTable symbolTable){
        this.symbolTable=symbolTable;
    }

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

    //an alternative version to getLine() where indent is not reduced if line ends with "}\n"
    private String getLinePlain() {
        String line = stringBuilder.toString().indent(localIndent* 4);
        stringBuilder.setLength(0); // Resets string builder
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

    //An alternative version of visitChildren where the Strings before and after is appended before and after the result of visiting the child node
    public void visitChildren(AstNode node, String before, String after){
        for (AstNode childNode : node.getChildren()) {
            stringBuilder.append(before);
            childNode.accept(this);
            stringBuilder.append(after);
        }
    }

    private void appendClassDefinition(String access, String name, String superClass) {
        stringBuilder.append(access).append(javaE.CLASS.getValue()).append(name).append(" ");
        if(superClass!=null){
            stringBuilder
                    .append(javaE.EXTENDS.getValue())
                    .append(superClass)
                    .append(" ");
        }
    }

    private void resetStringBuilder(){
        resetStringBuilder(stringBuilder);
        resetCodeOutput(codeOutput);
        localIndent = 0;
    }

    private void appendImports(String pack, String firstClassName, String...additionalClassNames){
        appendImport(pack,firstClassName);
        for(String className:additionalClassNames){
            appendImport(pack,className);
        }
        stringBuilder.append("\n");
    }

    private void appendImport(String pack,String className){
        stringBuilder
                .append(javaE.IMPORT.getValue())
                .append(pack)
                .append(".")
                .append(className)
                .append(";\n");
    }

    private void appendBody(AstNode node){
        stringBuilder.append( " {\n");
        codeOutput.add(getLinePlain() );
        localIndent++;
        visitChildren(node);
        localIndent--;
        stringBuilder.append( "}\n");
        codeOutput.add(getLinePlain() );//evt. getLine() her, men synes ikke det virker når der deklareres klasser og metoder inde i actors.
    }

    @Override
    public void visit(AccessNode node) {
    }

    @Override
    public void visit(ArrayAccessNode node) {

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
        resetStringBuilder();

        //imports necessary for most akka actor classes
        appendImports("akka.actor",
                "ActorRef",
                "ActorSystem",
                "Props",
                "UntypedAbstractActor",
                "Logging",
                "LoggingAdapter"
        );
        appendClassDefinition("public", node.getId(),"UntypedAbstractActor");
        appendBody(node);

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
    //In FactorialHelper this is: private final int currentValue;
    @Override
    public void visit(StateNode node) {
        visitChildren(node);
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
        if(node.getIsActor() && node.getType()!= null){
            stringBuilder.append(javaE.ACTORREF.getValue());
        } else if (node.getType()!= null) {
            stringBuilder.append(VariableConverter(node.getType())).append(" ");
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

         if(node.getMethodType().equals(parLangE.ON.getValue())){
            handleOnMethodDcl(node);
        } else if (node.getMethodType().equals(parLangE.LOCAL.getValue())) {
            handleLocalMethodDcl(node);
        }

    }

    private void handleOnMethodDcl(MethodDclNode node){

    }

    private void handleLocalMethodDcl(MethodDclNode node) {
        appendMethodDefinition("private",node.getMethodType(),node.getId());
        appendBody(node);

    }



    private void appendMethodDefinition(String access, String returnType, String name){
        stringBuilder
                .append(access)
                .append(" ")
                .append(returnType)
                .append(name)
                .append(" ");
    }

    @Override
    public void visit(NegatedBoolNode node) {
        stringBuilder.append("!");
        visitChild(node.getChildren().get(0));
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
        resetStringBuilder();

        appendImports("akka.actor","ActorRef");
        appendClassDefinition(javaE.PUBLIC.getValue(),node.getId(),null);
        appendBody(node);

        writeToFile(node.getId(),codeOutput);
    }

    @Override
    public void visit(ScriptMethodNode node) {
        if(node.getMethodType().equals(parLangE.ON.getValue())){
            stringBuilder
                    .append(javaE.PUBLIC.getValue())
                    .append(javaE.STATIC.getValue())
                    .append(javaE.FINAL.getValue())
                    .append(javaE.CLASS.getValue())
                    .append(node.getId());
            appendBody(node);
        }
    }

    @Override
    public void visit(ParametersNode node) {
        if(node.getParent() instanceof ScriptMethodNode){
            localIndent++;
            visitChildren(node, javaE.PUBLIC.getValue(),";\n");
            localIndent--;
            codeOutput.add(getLinePlain() );
        }
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
        if (node.getParent() instanceof StateNode) {
            stringBuilder.append("private final ");
        }
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
