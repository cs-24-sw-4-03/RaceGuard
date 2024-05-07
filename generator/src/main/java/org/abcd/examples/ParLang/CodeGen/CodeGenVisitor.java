package org.abcd.examples.ParLang.CodeGen;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.NodeVisitor;
import org.abcd.examples.ParLang.javaE;
import org.abcd.examples.ParLang.parLangE;
import org.abcd.examples.ParLang.symbols.Scope;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.io.*;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static org.abcd.examples.ParLang.CodeGen.CodeGenUtils.*;


public class CodeGenVisitor implements NodeVisitor {

    SymbolTable symbolTable;

    public CodeGenVisitor(SymbolTable symbolTable){
        this.symbolTable=symbolTable;
    }

    private String dirPath = System.getProperty("user.dir") + "/output";

    StringBuilder sb = new StringBuilder(); // Used to generate a single line of code. Ends with a \n
    ArrayList<String> codeOutput = new ArrayList<>(); // Used to store lines of code
    int indent = 0; // local indent for file generated. 4 spaces per indent

    private int uniqueActorsCounter = 0; // Stores the amount of spawned actors






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
            sb.append(before);
            childNode.accept(this);
            sb.append(after);
        }
    }

    /***
     * Appends class definition string (e.g. "public class MyClass extends OtherClass")
     * @param access The access modifier of the class (e.g "public")
     * @param name The name of the class (e.g. "MyClass")
     * @param superClassName Give null as input here unless the class extends from a superclass. If there
     */
    private void appendClassDefinition(String access, String name, String superClassName) {
        sb
                .append(access)
                .append(javaE.CLASS.getValue())
                .append(name)
                .append(" ");
        if(superClassName!=null){
            sb
                    .append(javaE.EXTENDS.getValue())
                    .append(superClassName);
        }
    }

    /***
     * Appends static final class definition string
     * @param access access modifier
     * @param name name of class
     */

    private void appendStaticFinalClassDef(String access, String name){
        sb
                .append(access)
                .append(javaE.STATIC.getValue())
                .append(javaE.FINAL.getValue())
                .append(javaE.CLASS.getValue())
                .append(name);
    }



    /***
     * Append method declaration string (e.g. "private void myMethod")
     * @param access access modifier (e.g. "private")
     * @param returnType (e.g. "void")
     * @param name (e.g. "myMethod")
     */

    private void appendMethodDefinition(String access, String returnType, String name){
        sb
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
        sb.append("\n");
    }

    /***2
     * Appends a single import statement (e.g. "import akka.actor.UntypedAbstractActor;")
     * @param pack Name of the package (e.g. "akka.actor")
     * @param className Name of the class (e.g. "UntypedAbstractActor")
     */

    private void appendImport(String pack,String className){
        sb
                .append(javaE.IMPORT.getValue())
                .append(pack)
                .append(".")
                .append(className)
                .append(javaE.SEMICOLON.getValue());
    }

    /***
     * Used to append a body of e.g. a class or a method.
     * Can be used if all the information required to produce the content of the body is present in the children of the node parameter.
     * @param node The children of this AST node constitutes all the body to be appended in the target code.
     */
    private void appendBody(AstNode node){
        appendBodyOpen(node);
        appendBodyClose();
    }

    /***
     * Can be used if something has to be added after visting the children.
     * @param node the parent of the body.
     */
    private void appendBodyOpen(AstNode node){
        appendBodyOpen(node,"","");
    }

    private void appendBodyOpen(AstNode node,String before,String after){
        sb.append( " {\n");
        codeOutput.add(getLine() );//gets current line with indentation given by localIndent at this moment, resets stringBuilder, and adds the line to codeOutput.
        indent++; //content of the body is indented
        visitChildren(node,before,after);//append the content of the body by visiting the children of @param node.
    }

    /**
     * Used to finish a body after appending what is needed.
     */

    private void appendBodyClose(){
        indent--;
        sb.append( "}\n");
        codeOutput.add(getLine() );
    }

    private void appendConstructor(String className,List<IdentifierNode> params){
        sb
                .append(javaE.PUBLIC.getValue())
                .append(className)
                .append("(");
        if(params.size()>0){
            for(IdentifierNode param:params){
                sb
                        .append(param.getType())
                        .append(" ")
                        .append(param.getName())
                        .append(", ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }

        sb.append(") {\n");
        codeOutput.add(getLine());
        indent++;
        if(params!=null){
            for(IdentifierNode param:params){
                sb
                        .append(javaE.THIS.getValue())
                        .append(".")
                        .append(param.getName())
                        .append(javaE.EQUALS.getValue())
                        .append(param.getName())
                        .append(";\n");
            }
        }
        codeOutput.add(getLine());
        indent--;
        sb.append("}\n");
        codeOutput.add(getLine());
    }

    /***
     * Appends and onReceive() method to the body of an Actor.
     * @param node The ActorDclNode in the AST which is used to produce the body of the actor in the target code.
     */
    private void appendOnReceive(ActorDclNode node){
        Scope scope=symbolTable.lookUpScope(node.getId());//Get the scope of the actor.
        Iterator<String> onMethods= scope.getDeclaredOnMethods().keySet().iterator();//get an iterator over the on methods of the actor.
        String methodName;

        //append the method signature
        sb
                .append(javaE.PUBLIC.getValue())
                .append(javaE.VOID.getValue())
                .append(javaE.ONRECEIVE.getValue())//has value "onReceive(Object message) "
                .append("{\n");

        codeOutput.add(getLine());//get line and add to codeOutput before indentation changes.
        indent++;

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

        indent--;
        sb.append("}\n");
        codeOutput.add(getLine()); //get line and add to codeOutput since indentation might change after calling this method.
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
            keyword=javaE.ELSEIF.getValue();
        }else{
            throw new IllegalArgumentException("argument type is not 'if' or 'if else'.");
        }
        sb
                .append(keyword)
                .append("(")
                .append(condition)
                .append(") {\n");
        codeOutput.add(getLine());//get line before indentation changes.
        indent++;
        sb.append(body);
        codeOutput.add(getLine());//get line before indentation changes.
        indent--;
        sb.append("}");
    }

    /**
     * Appends an else statement after an if-else chain.
     * @param body The body of the else statement
     */

    private void appendElse(String body){
        sb
                .append(javaE.ELSE.getValue())
                .append("{\n");
        codeOutput.add(getLine());
        indent++;
        sb.append(body);
        codeOutput.add(getLine());
        indent--;
        sb.append("}\n");
        codeOutput.add(getLine());//get the line since indentation might change after calling this method.
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
        sb.append(node.getAccessIdentifier());
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
                sb.append(".set(");
                typeCastFirstArrayAccessNode(node);
                visitChild(node.getChildren().get(0));
                sb.append(", ");
            }
            //Get method for a index in a 1D array when assigning to another 1D array
            //Second child need a get method: .get(index)
            else {
                sb.append(".get(");
                typeCastFirstArrayAccessNode(node);
                visitChild(node.getChildren().get(0));
                sb.append(")");
            }
        }
        //Compare an array with another array or a value
        //e.g: a[0] == 1; a[0] == b[0];
        else if(node.getParent() instanceof CompareExpNode){
            if(accessArrayDimensions(node) == 1){
                sb.append(".get(");
                visitChild(node.getChildren().get(0));
                sb.append(")");
            }
            else{
                sb.append(".get(");
                visitChild(node.getChildren().get(0));
                sb.append(").get(");
                visitChild(node.getChildren().get(1));
                sb.append(")");
            }
        }
        //Assign a 2D array with another 2D array
        else {
                //First child of the assign node need a get and set method: .get(index).set(index, secondChild)
                if (node.getParent().getChildren().getFirst() == node) {
                    sb.append(".get(");
                    typeCastFirstArrayAccessNode(node);
                    visitChild(node.getChildren().get(0));
                    sb.append(").set(");
                    typeCastSecondArrayAccessNode(node);
                    visitChild(node.getChildren().get(1));
                    sb.append(", ");
                }
                //Second child of the assign node only needs get methods: .get(index).get(index)
                else {
                    sb.append(".get(");
                    typeCastFirstArrayAccessNode(node);
                    visitChild(node.getChildren().get(0));
                    sb.append(").get(");
                    typeCastSecondArrayAccessNode(node);
                    visitChild(node.getChildren().get(1));
                    sb.append(")");
            }
        }
    }

    private void typeCastFirstArrayAccessNode(ArrayAccessNode node){
        if(node.getChildren().get(0) instanceof IdentifierNode){
            sb.append("(int) ");
        }
    }
    private void typeCastSecondArrayAccessNode(ArrayAccessNode node){
        if(node.getChildren().get(1) instanceof IdentifierNode){
            sb.append("(int) ");
        }
    }
    //Checks if the grandparent is a VarDclNode or if the sibling is an identifier
    private boolean isGrandparentVarDclOrSiblingIdentifier(ArrayAccessNode node){
        return node.getParent().getParent() instanceof VarDclNode || node.getParent().getChildren().get(0) instanceof IdentifierNode;

    }
    private void accessArrayFirstChild(ArrayAccessNode node){
        sb.append(".get(");
        visitChild(node.getChildren().get(0));
        sb.append(")");
    }
    private void accessArraySecondChild(ArrayAccessNode node){
        sb.append(".get(");
        visitChild(node.getChildren().get(1));
        sb.append(")");
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
                "UntypedAbstractActor"
        );
        appendImports("akka.event",
                "Logging",
                "LoggingAdapter"
        );

        appendClassDefinition(javaE.PUBLIC.getValue(), node.getId(),"UntypedAbstractActor");

        //append the body of the actor class
        appendBodyOpen(node);
        appendOnReceive(node);
        appendBodyClose();

        writeToFile(node.getId(), codeOutput);//Write the actor class to a separate file.
    }

    @Override
    public void visit(ArgumentsNode node) {

    }

    @Override
    public void visit(ArithExpNode node) {
            if(node.getIsParenthesized()) {
                sb.append("(");
            }

            visitChild(node.getChildren().get(0));
            sb.append(" " + node.getOpType().getValue() + " ");
            visitChild(node.getChildren().get(1));
            if (node.getIsParenthesized()) {
                sb.append(")");
            }

    }

    @Override
    public void visit(AssignNode node) {
    AstNode leftChild = node.getChildren().get(0);
    AstNode rightChild = node.getChildren().get(1);

    if(!(node.getChildren().get(1) instanceof ListNode) && !(node.getChildren().get(0) instanceof ArrayAccessNode)){
            visitChild(leftChild);
            sb.append(" = ");
            visitChild(rightChild);
    } else {

        visitChild(leftChild);
        visitChild(rightChild);
        sb.append(")");
    }
    if(!(node.getParent() instanceof ForNode)){ //if the parent is not a for node, add a semicolon, else don't
        sb.append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
        }

    }

    @Override
    public void visit(BodyNode node) {
        if (node.getParent() instanceof MainDclNode) {
            sb
                    .append(javaE.CURLY_OPEN.getValue());
            codeOutput.add(getLine());
            indent++;
            sb
                    .append("ActorSystem system = ActorSystem.create(\"system\")")
                    .append(javaE.SEMICOLON.getValue());
            visitChildren(node);
            appendBodyClose();
        } else if (node.getParent() instanceof SpawnDclNode) { //No curly brackets needed
            String outerScopeName = symbolTable.findActorParent(node);
            sb
                    .append(javaE.PUBLIC.getValue())
                    .append(outerScopeName)
                    .append("() ");
            appendBody(node);
        } else {
            appendBody(node);
        }
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
                    sb.append("(");
                    visitChild(node.getChildren().get(i));
                } else {
                    sb.append(" && ");
                    visitChild(node.getChildren().get(i));
                    if(node.getChildren().size()-1 == node.getChildren().indexOf(node.getChildren().get(i))){
                        sb.append(")");
                    }
                }
            }
            else {
                if (i == 0) {
                    visitChild(node.getChildren().get(i));
                } else {
                    sb.append(" && ");
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
                    sb.append("(");
                    visitChild(node.getChildren().get(i));
                } else {
                    sb.append(" || ");
                    visitChild(node.getChildren().get(i));
                    if(node.getChildren().size()-1 == node.getChildren().indexOf(node.getChildren().get(i))){
                        sb.append(")");
                    }
                }
            }
            else {
                if (i == 0) {
                    visitChild(node.getChildren().get(i));
                } else {
                    sb.append(" || ");
                    visitChild(node.getChildren().get(i));
                }
            }
        }

    }

    @Override
    public void visit(SelfNode node) {

    }

    @Override
    public void visit(CompareExpNode node) {
        if(node.getIsParenthesized()){
            sb.append("(");
        }

       visitChild(node.getChildren().get(0));
       if(node.getChildren().get(0) instanceof ArrayAccessNode && node.getChildren().get(1) instanceof ArrayAccessNode){
            sb.append(".equals(");
            visitChild(node.getChildren().get(1));
            sb.append(")");
       }else{
            sb.append(node.getOperator());
            visitChild(node.getChildren().get(1));
       }

       if(node.getIsParenthesized()){
              sb.append(")");
         }
    }


    @Override
    public void visit(DoubleNode node) {
        sb.append(node.getValue());

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
        sb.append(javaE.FOR.getValue()).append("(");
        //check if the second child is a compare expression and the third child is an assign node
        if (node.getChildren().get(1) instanceof CompareExpNode && node.getChildren().get(2) instanceof AssignNode) {
            visitChild(node.getChildren().get(0));
            sb.append("; ");
            visitChild(node.getChildren().get(1));
            sb.append("; ");
            visitChild(node.getChildren().get(2));
            sb.append(")");
            visitChild(node.getChildren().get(3));
        } else { //check if the first child is a compare expression
            if (node.getChildren().get(0) instanceof CompareExpNode) {
                sb.append("; ");
                visitChild(node.getChildren().get(0));
                sb.append(" ;");
                if(!(node.getChildren().get(1) instanceof AssignNode)){ //check if the second child is not an assign node
                    sb.append(")");
                    visitChild(node.getChildren().get(1));
                }else{ //check if the second child is an assign node
                    visitChild(node.getChildren().get(1));
                    sb.append(")");
                    visitChild(node.getChildren().get(2));
                }
            }
            //check if the second child is a compare, then it knows that first is a var dcl and third is the body node
            if(node.getChildren().get(1) instanceof CompareExpNode){
                visitChild(node.getChildren().get(0));
                sb.append("; ");
                visitChild(node.getChildren().get(1));
                sb.append(" ;)");
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
            sb
                    .append(javaE.PRIVATE.getValue())
                    .append(javaE.FINAL.getValue())
                    .append(javaE.ACTORREF.getValue())
                    .append(node.getName())
                    .append(javaE.SEMICOLON.getValue());
            codeOutput.add(getLine());
        } else if(arrayType !=null){
            sb
                    .append(arrayType)
                    .append(" ")
                    .append(node.getName())
                    .append(" = new ArrayList<>()")
                    .append(javaE.SEMICOLON.getValue());
            //Check if the parent is a var dcl node and has more than one child, then append the name of the node which
            //is used to add elements to the arraylist in the ListNode
            if(node.getParent() instanceof VarDclNode && node.getParent().getChildren().size() > 1){
                sb.append(node.getName());
            }
        } else if(symbolTable.lookUpScope(node.getType())!=null) {//If there is a scope with the same name as the IdentierfierNode's type, then the type is an actor
             sb
                     .append(javaE.ACTORREF.getValue())//appends "ActorRef ".
                     .append(node.getName());
         }
        else if(node.getType()!= null){
             if (node.getParent() instanceof ReturnStatementNode || node.getParent() instanceof AssignNode) {
                 sb.append(node.getName());
             } else {
                 sb.append(VariableConverter(node.getType()));
                 sb.append(" ");
                 sb.append(node.getName());
             }

        }
        else{
            sb.append(node.getName());
        }
    }

    @Override
    public void visit(InitializationNode node) {
        if(!(node.getChildren().get(0) instanceof ListNode)){
            sb.append(" = ");
        }
        visitChildren(node);
    }

    @Override
    public void visit(InitNode node) {


    }

    @Override
    public void visit(IntegerNode node) {
        sb.append(node.getValue());

    }

    @Override
    public void visit(IterationNode node) {

    }

    @Override
    public void visit(KnowsAccessNode node) {
        sb
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
                sb.append(".addAll(Arrays.asList(");
            } else {
                sb.append("new ArrayList<>(Arrays.asList(");
            }
            separateElementsList(node);
        }
    }
    //Separate the elements in the list by a comma
    private void separateElementsList(ListNode node) {
        for(int i = 0; i < node.getChildren().size(); i++){
            visitChild(node.getChildren().get(i));
            if(i != node.getChildren().size()-1){
                sb.append(", ");
            }
        }
        sb.append("))");
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        appendBody(node);//Use the children of the LocalMethodBodyNode node to append the method's body in the target code.
    }

    @Override
    public void visit(MainDclNode node) {
        resetStringBuilder();
        //public class Main {
        appendImports("akka.actor",
                "ActorSystem",
                "ActorRef",
                "Props",
                "UntypedAbstractActor");
        sb
                .append(javaE.PUBLIC.getValue())
                .append(javaE.CLASS.getValue())
                .append("Main")
                .append(" {\n");
        codeOutput.add(getLine());
        indent++;
        //public static void main(String[] args) {
        //      ActorSystem system = ActorSystem.create("system");
        //} //end of main method
        sb
                .append(javaE.PUBLIC.getValue())
                .append(javaE.STATIC.getValue())
                .append(javaE.VOID.getValue())
                .append("main(String[] args)");
        //codeOutput.add(getLineBasic());
        visitChildren(node);
        appendBodyClose();
        writeToFile(node.getId(), codeOutput);
    }


    @Override
    public void visit(MethodCallNode node) {

    }

    @Override
    public void visit(MethodDclNode node) {
        if(node.getMethodType().equals(parLangE.ON.getValue())){
            if(!isMethodInFollowedScript(node)){
                //We create a static class. Instances of this class is sent as message when the on-method is called.
                String className=capitalizeFirstLetter(node.getId());
                appendStaticFinalClassDef(javaE.PUBLIC.getValue(),className);//It is important that it is public since other actors must be able to access it.
                appendBodyOpen(node.getChildren().getFirst(),javaE.PUBLIC.getValue(),";\n");
                appendConstructor(className,(List<IdentifierNode>)(List<?>) node.getChildren().get(0).getChildren());
                appendBodyClose();
            }
            appenBehvaiour(node);

            //To be done
        } else if (node.getMethodType().equals(parLangE.LOCAL.getValue())) {
            appendMethodDefinition(javaE.PRIVATE.getValue(), node.getType(),node.getId());
            visit(node.getParametersNode());//append parameters in target code
            visit((LocalMethodBodyNode) node.getBodyNode()); //append the method's body in the target code.
        }
    }

    private void appenBehvaiour(MethodDclNode node){
        appendMethodDefinition(javaE.PRIVATE.getValue(),javaE.VOID.getValue(),node.getId());
        appendBody(node.getChildren().get(1));
    }

    private boolean isMethodInFollowedScript(MethodDclNode node){
        //MethodDclNode's parent is always an actor.
        // If the actor follows a script, a FollowsNode is the first child of this actor
        AstNode firstChildOfActor=node.getParent().getChildren().get(0);
        if(firstChildOfActor instanceof FollowsNode followsNode){
            List<IdentifierNode> followedScripts=(List<IdentifierNode>)(List<?>) followsNode.getChildren();//Casting through intermediate wildcard type in or to be able to cast the list.
            for(IdentifierNode script:followedScripts){
                Scope scriptScope=symbolTable.lookUpScope(script.getName());
                if(scriptScope.getDeclaredOnMethods().containsKey(node.getId())){
                    return true;
                }
            }
        }
        return false;
    }




    @Override
    public void visit(NegatedBoolNode node) {
        sb.append("!");
        visitChild(node.getChildren().get(0));
    }



    @Override
    public void visit(PrintCallNode node) {
        sb.append("System.out.println(");
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
        sb.append(")").append(javaE.SEMICOLON.getValue());
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
        sb.append(((ArrayAccessNode) node.getChildren().get(0)).getAccessIdentifier());
        sb.append(".get(");
        if(node.getChildren().get(0).getChildren().get(0) instanceof IdentifierNode){ //typecast if the child is an identifier
            sb.append("(int) ");
        }
        visit(node.getChildren().get(0));
        sb.append(")");
    }
    //Print the two dimensional array
    private void printTwoDimensionalArray(PrintCallNode node){
        sb.append(((ArrayAccessNode) node.getChildren().get(0)).getAccessIdentifier());
        sb.append(".get(");
        if(node.getChildren().get(0).getChildren().get(0) instanceof IdentifierNode){ //typecast if the child is an identifier
            sb.append("(int) ");
        }
        visitChild(node.getChildren().get(0).getChildren().get(0));
        sb.append(").get(");
        if(node.getChildren().get(0).getChildren().get(1) instanceof IdentifierNode){//typecast if the child is an identifier
            sb.append("(int) ");
        }
        visitChild(node.getChildren().get(0).getChildren().get(1));
        sb.append(")");
    }
    //Visit all the children of the print call node except the first one
    private void visitPrintChildrenFromChildOne(PrintCallNode node) {
        if(node.getChildren().size() > 1){
            for(int i = 1; i < node.getChildren().size(); i++){
                sb.append(" + ");
                visitChild(node.getChildren().get(i));
            }
        }
    }

    @Override
    public void visit(ReturnStatementNode node) {
        sb.append(javaE.RETURN.getValue());
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
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
    }


     public void visit(LiteralNode<?> node){
        sb.append(node.getValue());
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
        if(node.getMethodType().equals(parLangE.ON.getValue())){//local methods declared in a script does not need to be handled here.
            String className=capitalizeFirstLetter(node.getId());
            appendStaticFinalClassDef(javaE.PUBLIC.getValue(),className);//Create a static class for the on-method.

            //appends opening of the body of the static class and creates public fields for each parameter in the method.
            appendBodyOpen(node);

            List<IdentifierNode> params=new ArrayList<IdentifierNode>();//prepare list of parameters for constructor
            if(!node.getChildren().isEmpty()){
               params=(List<IdentifierNode>)(List<?>)node.getChildren().get(0).getChildren(); //set list of parameters if there are any.
            }
            appendConstructor(className,params);//append the constructor in the body.

            appendBodyClose();//close the body
        }
    }

    @Override
    public void visit(ParametersNode node) {
        if(node.getParent() instanceof MethodDclNode) {
            //If parameters is part of method declaration in an actor we simply append them to the method declaration in the target code
            appendParameters(node);
        }else if (node.getParent() instanceof ScriptMethodNode){
            //If the method is declared in a script, the parameters are mapped to fields in the static class representing the method
            indent++;
            visitChildren(node, javaE.PUBLIC.getValue(),javaE.SEMICOLON.getValue()); //Insterts the parameters ad public fields in the method's static class
            indent--;
            codeOutput.add(getLine() );
        }
    }

    private void appendParameters(ParametersNode node){
        sb.append("(");
        visitChildren(node,"",",");//appends list of parameters. There is a surplus comma after last parameter: "int p1, int p2,"
        if(node.getChildren().size()>0){
            sb.deleteCharAt(sb.length() - 1);//delete the surplus comma
        }
        sb.append(")");
    }



    //Standard selection node construction with if and else statements
    @Override
    public void visit(SelectionNode node) {
        sb.append(javaE.IF.getValue()).append("(");
        visitChild(node.getChildren().get(0));
        sb.append(")");
        visitChild(node.getChildren().get(1));

        //check if there is an else statement
        if(node.getChildren().size() > 2){
            sb.append(javaE.ELSE.getValue());
            visitChild(node.getChildren().get(2));
        }
    }

    @Override
    public void visit(SendMsgNode node) {

    }
    
    private int getNextUniqueActor() {
        return uniqueActorsCounter++;
    }
    @Override
    public void visit(SpawnActorNode node) {
        String outerScopeName = symbolTable.findActorParent(node);
        if (outerScopeName != null) { //Actor or Script
            sb
                    .append("getContext().actorOf(Props.create(")
                    .append(node.getType())
                    .append(".class")
                    .append("), \"")
                    .append(getNextUniqueActor())
                    .append("\")");
        }
        else { //null means it's main
            sb
                    .append("system.actorOf(Props.create(")
                    .append(node.getType())
                    .append(".class")
                    .append("), \"")
                    .append(getNextUniqueActor())
                    .append("\")");

        }
    }

    @Override
    public void visit(SpawnDclNode node) {
        visitChildren(node);

    }

    @Override
    public void visit(ExpNode node) {

    }

    @Override
    public void visit(StateAccessNode node) {
        sb
                .append(parLangE.STATE.getValue())
                .append(".")
                .append(node.getAccessIdentifier());
    }

    @Override
    public void visit(StringNode node) {
    sb.append(node.getValue());
    }


    @Override
    public void visit(VarDclNode node) {
        if (node.getParent() instanceof StateNode) {
            sb.append(javaE.PRIVATE.getValue()).append(javaE.FINAL.getValue());
        }
        visitChildren(node);

        //if the parent is not a for node, add a semicolon, else don't
        if(!(node.getParent() instanceof ForNode)){
                sb.append(javaE.SEMICOLON.getValue());
                codeOutput.add(getLine());
            }

    }

    //Standard while loop construction
    @Override
    public void visit(WhileNode node) {
        sb.append(javaE.WHILE.getValue()).append("(");
        visitChild(node.getChildren().get(0));
        sb.append(")");
        visitChild(node.getChildren().get(1));
        sb.append("\n");
        codeOutput.add(getLine());
    }

    @Override
    public void visit(BoolCompareNode node){

    }
}
