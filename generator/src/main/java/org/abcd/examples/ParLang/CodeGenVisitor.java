package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.symbols.Scope;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.io.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Objects;
import java.util.Iterator;


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
    //Gets current line with indentation given by localIndent at this moment and resets stringBuilder.
    private String getLineBasic() {
        String line = stringBuilder.toString().indent(localIndent* 4);
        stringBuilder.setLength(0); // Resets string builder
        return line;
    }

    private void resetStringBuilder(){
        resetStringBuilder(stringBuilder);
        resetCodeOutput(codeOutput);
        localIndent = 0;
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

    /***
     * Appends class definition string (e.g. "public class MyClass extends OtherClass")
     * @param access The access modifier of the class (e.g "public")
     * @param name The name of the class (e.g. "MyClass")
     * @param superClassName Give null as input here unless the class extends from a superclass. If there
     */
    private void appendClassDefinition(String access, String name, String superClassName) {
        stringBuilder
                .append(access)
                .append(javaE.CLASS.getValue())
                .append(name)
                .append(" ");
        if(superClassName!=null){
            stringBuilder
                    .append(javaE.EXTENDS.getValue())
                    .append(superClassName);
        }
    }

    /***
     * Append method declaration string (e.g. "private void myMethod")
     * @param access access modifier (e.g. "private")
     * @param returnType (e.g. "void")
     * @param name (e.g. "myMethod")
     */

    private void appendMethodDefinition(String access, String returnType, String name){
        stringBuilder
                .append(access)
                .append(returnType)
                .append(" ")
                .append(name);
    }

    /***
     * Appends a series of imports from the same package. e.g.:
     *
     *      "import akka.actor.UntypedAbstractActor;
     *       import akka.actor.ActorRef;
     *       import akka.event.Logging;"
     *
     * @param pack Name of the package (e.g. "akka.actor")
     * @param firstClassName The name of the first class imported from the package (e.g. "UntypedAbstractActor")
     * @param additionalClassNames The names of the remaining class names (e.g. "ActorRef" and "Logging")
     */
    private void appendImports(String pack, String firstClassName, String...additionalClassNames){
        appendImport(pack,firstClassName);// append import of the first class
        for(String className:additionalClassNames){ //append imports of the remaining classes
            appendImport(pack,className);
        }
        stringBuilder.append("\n");
    }

    /***2
     * Appends a single import statement (e.g. "import akka.actor.UntypedAbstractActor;")
     * @param pack Name of the package (e.g. "akka.actor")
     * @param className Name of the class (e.g. "UntypedAbstractActor")
     */

    private void appendImport(String pack,String className){
        stringBuilder
                .append(javaE.IMPORT.getValue())
                .append(pack)
                .append(".")
                .append(className)
                .append(";\n");
    }

    /***
     * Used to append a body of e.g. a class or a method.
     * Can be used if all the information required to produce the content of the body is present in the children of the node parameter.
     * @param node The children of this AST node constitutes all the body to be appended in the target code.
     */
    private void appendBody(AstNode node){
        stringBuilder.append( " {\n");
        codeOutput.add(getLineBasic() );//gets current line with indentation given by localIndent at this moment, resets stringBuilder, and adds the line to codeOutput.
        localIndent++; //content of the body is indented
        visitChildren(node);//append the content of the body by visiting the children of @param node.
        if(node instanceof ActorDclNode actorDclnode){//If node is an actor declaration, then the onReveice method needs to be appended.
            appendOnReceive(actorDclnode);
        }
        localIndent--;
        stringBuilder.append( "}\n");
        codeOutput.add(getLineBasic() );
    }

    /***
     * Appends and onReceive() method to the vody of an Actor.
     * @param node The ActorDclNode in the AST which is used to produce the body of the actor in the target code.
     */
    private void appendOnReceive(ActorDclNode node){
        Scope scope=symbolTable.lookUpScope(node.getId());//Get the scope of the actor.
        Iterator<String> onMethods= scope.getDeclaredOnMethods().keySet().iterator();//get an iterator over the on methods of the actor.
        String methodName;

        //append the method signature
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(javaE.VOID.getValue())
                .append(javaE.ONRECEIVE.getValue())//has value "onReceive(Object message) "
                .append("{\n");

        codeOutput.add(getLineBasic());//get line and add to codeOutput before indentation changes.
        localIndent++;

        //The body is an if-els chain.
        if(onMethods.hasNext()){//The first on-methods results in an if-statement.
            methodName=onMethods.next();
            appendIfElseChainLink("if",getOnReceiveIfCondition(methodName),getOnReceiveIfBody(methodName));
        }
        while (onMethods.hasNext()){//The remaining on-methods results in if-else statements
            methodName=onMethods.next();
            appendIfElseChainLink("if else",getOnReceiveIfCondition(methodName),getOnReceiveIfBody(methodName));
        }
        appendElse(javaE.UNHANDLED.getValue());//There is always and else statement in the end of the chain handling yet unhandled messages.

        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLineBasic()); //get line and add to codeOutput since indentation might change after calling this method.
    }

    /**
     * Appends a single if of if-else statement in an if-else chain.
     * @param type must have values of either "if" or "if else"
     * @param condition The condition of the if/if-else statement
     * @param body the body of the if/if-else statement
     */
    private void appendIfElseChainLink(String type,String condition,String body){
        String keyword;
        if(type.equals("if")){
            keyword=javaE.IF.getValue();
        }else if(type.equals("if else")) {
            keyword=javaE.IFELSE.getValue();
        }else{
            throw new IllegalArgumentException("argument type is not 'if' or 'if else'.");
        }
        stringBuilder
                .append(keyword)
                .append("(")
                .append(condition)
                .append(") {\n");
        codeOutput.add(getLineBasic());//get line before indentation changes.
        localIndent++;
        stringBuilder.append(body);
        codeOutput.add(getLineBasic());//get line before indentation changes.
        localIndent--;
        stringBuilder.append("}");
    }

    /**
     * Appends an else statement after an if-else chain.
     * @param body The body of the else statement
     */

    private void appendElse(String body){
        stringBuilder
                .append(javaE.ELSE.getValue())
                .append("{\n");
        codeOutput.add(getLineBasic());
        localIndent++;
        stringBuilder.append(body);
        codeOutput.add(getLineBasic());
        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLineBasic());//get the line since indentation might change after calling this method.
    }

    /**
     * @param methodName Name of the on-method
     * @return A condition for checking if incoming message is of the message type corresponding to the on-method.
     */
    private String getOnReceiveIfCondition(String methodName){
        return "message "+javaE.INSTANCEOF.getValue()+capitalizeFirstLetter(methodName)+" "+methodName+"Msg";
    }

    /***
     * @param methodName Name of the on-method
     * @return A statement which calls a private-method in the actor. This method has the functionality to be executed when the message corresponding to the on-method is received.
     */
    private String getOnReceiveIfBody(String methodName){
        return "on"+capitalizeFirstLetter(methodName)+"("+methodName+"Msg"+");";
    }

    private String capitalizeFirstLetter(String input) {
        if (input != null && !input.isEmpty()) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        } else {
            throw new IllegalArgumentException("Input string is null or empty");
        }
    }

    @Override
    public void visit(AccessNode node) {
        if(node instanceof StateAccessNode){
            visit((StateAccessNode) node);
        }else if(node instanceof KnowsAccessNode){
            visit((KnowsAccessNode) node);
        }
    }

    @Override
    public void visit(ArrayAccessNode node) {
        stringBuilder.append(node.getAccessIdentifier());
        //e.g: int x = a[0]; x = a[1];
        if(isGrandparentVarDclOrSiblingIdentifier(node) && accessArrayDimensions(node) == 1){
            accessArrayFirstChild(node);
        }
        //e.g: int x = a[0][0]; x = a[1][1];
        else if(isGrandparentVarDclOrSiblingIdentifier(node) && accessArrayDimensions(node) == 2){
            accessArrayFirstChild(node);
            accessArraySecondChild(node);
        }
        //Assign values to 1D array
        else if(accessArrayDimensions(node) == 1 && !(node.getParent() instanceof CompareExpNode)){
            //Set method for a index in a 1D array with a value
            //First child need a set method: .set(index, value)
            //e.g: a[0] = 1;
            //if trying to assign a 1D array with another 1D array
            //First child need a set method: .set(index, secondChild)
            //e.g: a[0] = b[0];
            if (node.getParent().getChildren().getFirst() == node) {
                stringBuilder.append(".set(");
                typeCastFirstArrayAccessNode(node);
                visitChild(node.getChildren().get(0));
                stringBuilder.append(", ");
            }
            //Get method for a index in a 1D array when assigning to another 1D array
            //Second child need a get method: .get(index)
            else {
                stringBuilder.append(".get(");
                typeCastFirstArrayAccessNode(node);
                visitChild(node.getChildren().get(0));
                stringBuilder.append(")");
            }
        }
        //Compare an array with another array or a value
        //e.g: a[0] == 1; a[0] == b[0];
        else if(node.getParent() instanceof CompareExpNode){
            if(accessArrayDimensions(node) == 1){
                stringBuilder.append(".get(");
                visitChild(node.getChildren().get(0));
                stringBuilder.append(")");
            }
            else{
                stringBuilder.append(".get(");
                visitChild(node.getChildren().get(0));
                stringBuilder.append(").get(");
                visitChild(node.getChildren().get(1));
                stringBuilder.append(")");
            }
        }
        //Assign a 2D array with another 2D array
        else {
                //First child of the assign node need a get and set method: .get(index).set(index, secondChild)
                if (node.getParent().getChildren().getFirst() == node) {
                    stringBuilder.append(".get(");
                    typeCastFirstArrayAccessNode(node);
                    visitChild(node.getChildren().get(0));
                    stringBuilder.append(").set(");
                    typeCastSecondArrayAccessNode(node);
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(", ");
                }
                //Second child of the assign node only needs get methods: .get(index).get(index)
                else {
                    stringBuilder.append(".get(");
                    typeCastFirstArrayAccessNode(node);
                    visitChild(node.getChildren().get(0));
                    stringBuilder.append(").get(");
                    typeCastSecondArrayAccessNode(node);
                    visitChild(node.getChildren().get(1));
                    stringBuilder.append(")");
            }
        }
    }

    private void typeCastFirstArrayAccessNode(ArrayAccessNode node){
        if(node.getChildren().get(0) instanceof IdentifierNode){
            stringBuilder.append("(int) ");
        }
    }
    private void typeCastSecondArrayAccessNode(ArrayAccessNode node){
        if(node.getChildren().get(1) instanceof IdentifierNode){
            stringBuilder.append("(int) ");
        }
    }
    //Checks if the grandparent is a VarDclNode or if the sibling is an identifier
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
    //Check if the array access node is a 1D array or 2D array
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

        appendClassDefinition(javaE.PUBLIC.getValue(), node.getId(),"UntypedAbstractActor");
        System.out.println("actor declaration");
        appendBody(node);//append the body of the actor class

        writeToFile(node.getId(), codeOutput);//Write the actor class to a separate file.
    }

    @Override
    public void visit(ArgumentsNode node) {

    }

    @Override
    public void visit(ArithExpNode node) {
            if(node.getIsParenthesized()) {
                stringBuilder.append("(");
            }

            visitChild(node.getChildren().get(0));
            stringBuilder.append(" " + node.getOpType().getValue() + " ");
            visitChild(node.getChildren().get(1));
            if (node.getIsParenthesized()) {
                stringBuilder.append(")");
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
       if(node.getChildren().get(0) instanceof ArrayAccessNode && node.getChildren().get(1) instanceof ArrayAccessNode){
            stringBuilder.append(".equals(");
            visitChild(node.getChildren().get(1));
            stringBuilder.append(")");
       }else{
            stringBuilder.append(node.getOperator());
            visitChild(node.getChildren().get(1));
       }

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

    //HashMap to convert the type of the array to the type of the arraylist
    private String HashMapConverter(IdentifierNode node){
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
        return HashMapConverter.get(node.getType());
    }

    @Override
    public void visit(IdentifierNode node) {
        String arrayType = HashMapConverter(node);
        if (node.getParent() instanceof KnowsNode) {
            stringBuilder
                    .append(javaE.PRIVATE.getValue())
                    .append(javaE.FINAL.getValue())
                    .append(javaE.ACTORREF.getValue())
                    .append(node.getName())
                    .append(";\n");
            codeOutput.add(getLineBasic());
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
        } else if(symbolTable.lookUpScope(node.getType())!=null) {//If there is a scope with the same name as the IdentierfierNode's type, then the type is an actor
             stringBuilder.append(javaE.ACTORREF.getValue());//appends "ActorRef ".
             stringBuilder.append(node.getName());

         } else if(node.getType()!= null){
            stringBuilder.append(VariableConverter(node.getType()));
            stringBuilder.append(" ");
            stringBuilder.append(node.getName());

        } else{
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
        stringBuilder
                .append(parLangE.KNOWS.getValue())
                .append(".")
                .append(node.getAccessIdentifier());
    }

    @Override
    public void visit(KnowsNode node) {
        visitChildren(node);
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
    //Separate the elements in the list by a comma
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
        appendBody(node);//Use the children of the LocalMethodBodyNode node to append the method's body in the target code.
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
            //To be done
        } else if (node.getMethodType().equals(parLangE.LOCAL.getValue())) {
            appendMethodDefinition(javaE.PRIVATE.getValue(), node.getType(),node.getId());
            visit(node.getParametersNode());//append parameters in target code
            visit((LocalMethodBodyNode) node.getBodyNode()); //append the method's body in the target code.
        }
    }





    @Override
    public void visit(NegatedBoolNode node) {
        stringBuilder.append("!");
        visitChild(node.getChildren().get(0));
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
        stringBuilder.append(javaE.RETURN.getValue());
        AstNode returnee=node.getReturnee();//get the expression which is returned (return <returnee>;)
        if(returnee instanceof IdentifierNode){
            visit((IdentifierNode) returnee);
        } else if (returnee instanceof ArithExpNode) {
            visit((ArithExpNode) returnee);
        } else if (returnee instanceof BoolExpNode) {
            visit((BoolExpNode) returnee);
        } else if (returnee instanceof AccessNode) {
            visit((AccessNode) returnee);
        } else if (returnee instanceof LiteralNode){
            visit((LiteralNode<?>) returnee);
        } else if (returnee==null) {//If nothing is returned, delete extra space after "return".
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(";\n");
        codeOutput.add(getLineBasic());
    }


     public void visit(LiteralNode<?> node){
        stringBuilder.append(node.getValue());
     }

    @Override
    public void visit(ScriptDclNode node) {
        resetStringBuilder();

        //We crate a public class for the script with the same name as the script.
        appendImports("akka.actor","ActorRef");
        appendClassDefinition(javaE.PUBLIC.getValue(),node.getId(),null);
        appendBody(node);//The body of the class has a static class for each on-method declared in the script.

        writeToFile(node.getId(),codeOutput); //The class is written to a separate file.
    }

    @Override
    public void visit(ScriptMethodNode node) {
        if(node.getMethodType().equals(parLangE.ON.getValue())){//local methods declared in a scrupt does not need to be handled here.
            //Create a static class for the on-method.
            stringBuilder
                    .append(javaE.PUBLIC.getValue())
                    .append(javaE.STATIC.getValue())
                    .append(javaE.FINAL.getValue())
                    .append(javaE.CLASS.getValue())
                    .append(node.getId());
            appendBody(node);//appends the body of the static class (prublic fields for each parameter in the method)
        }
    }

    @Override
    public void visit(ParametersNode node) {
        if(node.getParent() instanceof MethodDclNode) {
            //If parameters is part of method declaration in an actor we simply append them to the method declaration in the target code
            appendParameters(node);
        }else if (node.getParent() instanceof ScriptMethodNode){
            //If the method is declared in a script, the parameters are mapped to fields in the static class representing the method
            localIndent++;
            visitChildren(node, javaE.PUBLIC.getValue(),";\n"); //Insterts the parameters ad public fields in the method's static class
            localIndent--;
            codeOutput.add(getLineBasic() );
        }
    }

    private void appendParameters(ParametersNode node){
        stringBuilder.append("(");
        visitChildren(node,"",",");//appends list of parameters. There is a surplus comma after last parameter: "int p1, int p2,"
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);//delete the surplus comma
        stringBuilder.append(")");
    }



    //Standard selection node construction with if and else statements
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
        stringBuilder
                .append(parLangE.STATE.getValue())
                .append(".")
                .append(node.getAccessIdentifier());
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

        //if the parent is not a for node, add a semicolon, else don't
        if(!(node.getParent() instanceof ForNode)){
                stringBuilder.append(";\n");
                codeOutput.add(getLine());
            }

    }

    //Standard while loop construction
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
