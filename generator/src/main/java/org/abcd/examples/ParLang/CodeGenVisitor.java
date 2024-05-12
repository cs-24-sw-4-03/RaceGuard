package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;
import org.abcd.examples.ParLang.Exceptions.ArgumentsException;
import org.abcd.examples.ParLang.Exceptions.SendMsgException;
import org.abcd.examples.ParLang.symbols.Attributes;
import org.abcd.examples.ParLang.symbols.Scope;
import org.abcd.examples.ParLang.symbols.SymbolTable;

import java.io.*;
import java.util.*;


public class CodeGenVisitor implements NodeVisitor {

    SymbolTable symbolTable;

    public CodeGenVisitor(SymbolTable symbolTable){
        this.symbolTable=symbolTable;
    }

    private String parentDirPath = new File(System.getProperty("user.dir")).getParent();
    private String dirPath = parentDirPath + "/output/src/main/java/output";



    StringBuilder stringBuilder = new StringBuilder(); // Used to generate a single line of code. Ends with a \n
    ArrayList<String> codeOutput = new ArrayList<>(); // Used to store lines of code
    private int localIndent = 0; // indent for file generated. 4 spaces per indent

    /**
     * @return the current line with the correct indentation.
     */
    private String getLine() {
        String line = stringBuilder.toString().indent(localIndent* 4);
        stringBuilder.setLength(0); // Resets string builder
        return line;
    }

    private void resetStringBuilder(){
        resetStringBuilder(stringBuilder);
        resetCodeOutput(codeOutput);
        localIndent = 0;
    }

    private String VarTypeConverter(String parlangType, boolean useActorRef, boolean removeBrackets){
        String javaType;
        switch (parlangType) {
            case "int":
                javaType=javaE.LONG.getValue();
                break;
            case "int[]" :
                javaType=javaE.LONG_ARRAY.getValue();
                break;
            case "int[][]":
                javaType=javaE.LONG_ARRAY_2D.getValue();
                break;
            case "double":
                javaType=javaE.DOUBLE.getValue();
                break;
            case "double[]" :
                javaType=javaE.DOUBLE_ARRAY.getValue();
                break;
            case "double[][]":
                javaType=javaE.DOUBLE_ARRAY_2D.getValue();
                break;
            case "bool":
                javaType=javaE.BOOLEAN.getValue();
                break;
            case "bool[]" :
                javaType=javaE.BOOLEAN_ARRAY.getValue();
                break;
            case "bool[][]":
                javaType=javaE.BOOLEAN_ARRAY_2D.getValue();
                break;
            case "string":
                javaType=javaE.STRING.getValue();
                break;
            case "string[]" :
                javaType=javaE.STRING_ARRAY.getValue();
                break;
            case "string[][]":
                javaType=javaE.STRING_ARRAY_2D.getValue();
                break;
            case "void":
                javaType=javaE.VOID.getValue();
                break;
            default:
                if(useActorRef){
                    String[] substrings=parlangType.split("(?=\\[)");//split at empty string where next string is "[".
                    if(substrings.length>1){
                        javaType=javaE.ACTORREF.getValue()+substrings[1];
                    }else{
                        javaType=javaE.ACTORREF.getValue();
                    }
                }else {
                    javaType=parlangType;
                }
        }
        if(removeBrackets){
            javaType=javaType.replaceAll("\\[", "").replaceAll("\\]","");
        }
        return javaType;
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
    public void visitChildren(AstNode node, String before, String after, ArrayList<String> parameterTypes){
        int index = 0;
        for (AstNode childNode : node.getChildren()) {
            stringBuilder.append(before);

            //If the children are actual parameters in a call we need to add valueOf() in order for Akka types to work
            if(parameterTypes != null){
                stringBuilder.append(determineValue(parameterTypes.get(index)));
            }

            childNode.accept(this);

            //If we have parameters and the type is either Int or Double, otherwise determineValue returns empty string
            if(parameterTypes != null && !determineValue(parameterTypes.get(index)).isEmpty()){
                stringBuilder.append(")");
            }
            stringBuilder.append(after);
            index++;
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
     * Appends static final class definition string
     * @param access access modifier
     * @param name name of class
     */

    private void appendStaticFinalClassDef(String access, String name){
        stringBuilder
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
        stringBuilder
                .append(access)
                .append(returnType)
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
        stringBuilder.append( " {\n");
        codeOutput.add(getLine() );//gets current line with indentation given by localIndent at this moment, resets stringBuilder, and adds the line to codeOutput.
        localIndent++; //content of the body is indented
        visitChildren(node,before,after, null);//append the content of the body by visiting the children of @param node.
    }

    /**
     * Used to finish a body after appending what is needed.
     */

    private void appendBodyClose(){
        localIndent--;
        stringBuilder.append( "}\n");
        codeOutput.add(getLine() );
    }

    private void appendConstructor(String className,List<IdentifierNode> params){
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(className)
                .append("(");
        if(!params.isEmpty()){
            for(IdentifierNode param:params){
                String javaType;
                if(symbolTable.lookUpScope(param.getType())!=null){
                    javaType=javaE.ACTORREF.getValue();
                }else{
                    javaType= VarTypeConverter(param.getType(),false,false)+" ";
                }
                stringBuilder
                        .append(javaType)
                        .append(param.getName())
                        .append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        stringBuilder.append(") {\n");
        codeOutput.add(getLine());
        localIndent++;
        if(params!=null){
            for(IdentifierNode param:params){
                stringBuilder
                        .append(javaE.THIS.getValue())
                        .append(".")
                        .append(param.getName())
                        .append(javaE.EQUALS.getValue())
                        .append(param.getName())
                        .append(";\n");
            }
        }
        codeOutput.add(getLine());
        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLine());
    }

    /***
     * Appends and onReceive() method to the body of an Actor.
     * @param node The ActorDclNode in the AST which is used to produce the body of the actor in the target code.
     */
    private void appendOnReceive(ActorDclNode node){
        String actorName=node.getId();
        Scope actorScope=symbolTable.lookUpScope(actorName);//Get the scope of the actor.
        Set<String> onMethodsList= actorScope.getDeclaredOnMethods().keySet();
        Iterator<String> onMethods= actorScope.getDeclaredOnMethods().keySet().iterator();//get an iterator over the on methods of the actor.
        String methodName;
        String className;

        //append the method signature
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(javaE.VOID.getValue())
                .append(javaE.ONRECEIVE.getValue())//has value "onReceive(Object message) "
                .append("{\n");

        codeOutput.add(getLine());//get line and add to codeOutput before indentation changes.
        localIndent++;

        //The body is an if-els chain.
        if(onMethods.hasNext()) {//The first on-methods results in an if-statement.
            methodName = onMethods.next();
            className = getclassName(node, methodName);
            Iterator<String> params=symbolTable.lookUpScope(methodName+actorName).getParams().keySet().iterator();
            appendIfElseChainLink("if", getOnReceiveIfCondition(className, methodName), getOnReceiveIfBody(methodName,params));
            while (onMethods.hasNext()) {//The remaining on-methods results in if-else statements
                methodName = onMethods.next();
                className = getclassName(node, methodName);
                params=symbolTable.lookUpScope(methodName+actorName).getParams().keySet().iterator();
                appendIfElseChainLink("else if", getOnReceiveIfCondition(className, methodName), getOnReceiveIfBody(methodName,params));
            }
            appendElse(javaE.UNHANDLED.getValue());//There is always and else statement in the end of the chain handling yet unhandled messages.
        }else{
            stringBuilder.append(javaE.UNHANDLED.getValue());
            codeOutput.add(getLine());
        }
        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLine()); //get line and add to codeOutput since indentation might change after calling this method.
    }

    private String getclassName(ActorDclNode node,String methodName){
        MethodDclNode methodNode=null;
        String className=capitalizeFirstLetter(methodName);

        for (AstNode childNode: node.getChildren()){
            if(childNode instanceof MethodDclNode && ((MethodDclNode) childNode).getId().equals(methodName)){
                methodNode=(MethodDclNode) childNode;
                break;
            }
        }
        if( methodNode!=null){
            String scriptName=getMethodInFollowedScript(methodNode);
            if(scriptName!=null){
                className=scriptName+"."+className;
            }
        }
        return className;
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
        }else if(type.equals("else if")) {
            keyword=javaE.ELSEIF.getValue();
        }else{
            throw new IllegalArgumentException("argument type is not 'if' or 'if else'.");
        }
        stringBuilder
                .append(keyword)
                .append("(")
                .append(condition)
                .append(") {\n");
        codeOutput.add(getLine());//get line before indentation changes.
        localIndent++;
        stringBuilder.append(body);
        codeOutput.add(getLine());//get line before indentation changes.
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
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append(body);
        codeOutput.add(getLine());
        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLine());//get the line since indentation might change after calling this method.
    }

    /**
     * @param methodName Name of the on-method
     * @param className class name of the protocol class corresponding to the on-method.
     * @return A condition for checking if incoming message is of the message type corresponding to the on-method.
     */
    private String getOnReceiveIfCondition(String className,String methodName){
        return "message "+javaE.INSTANCEOF.getValue()+className+" "+methodName+"Msg";
    }

    /***
     * @param methodName Name of the on-method
     * @return A statement which calls a private-method in the actor. This method has the functionality to be executed when the message corresponding to the on-method is received.
     */
    private String getOnReceiveIfBody(String methodName, Iterator<String> params){
        StringBuilder localStringBuilder=new StringBuilder();
        localStringBuilder
                .append(parLangE.ON.getValue())
                .append(capitalizeFirstLetter(methodName))
                .append("(");
        while (params.hasNext()){
            localStringBuilder
                    .append(methodName)
                    .append("Msg.")
                    .append(params.next());
            if(params.hasNext()){
                localStringBuilder.append(", ");
            }
        }
        localStringBuilder.append(");");
        return localStringBuilder.toString();
    }

    private String capitalizeFirstLetter(String input) {
        if (input != null && !input.isEmpty()) {
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        } else {
            throw new IllegalArgumentException("Input string is null or empty");
        }
    }




    @Override
    public void visit(ArrayAccessNode node){
        stringBuilder.append(node.getAccessIdentifier());
        if(accessArrayDimensions(node) == 1){
                stringBuilder.append("[");
                if(node.getChildren().get(0) instanceof IdentifierNode){
                    typeCastArrayAccessNode(node,0);
                }
                visitChild(node.getChildren().get(0));
                stringBuilder.append("]");
            }
            else{
                stringBuilder.append("[");
                if(node.getChildren().get(0) instanceof IdentifierNode){
                    typeCastArrayAccessNode(node,0);
                }
                visitChild(node.getChildren().get(0));
                stringBuilder.append("]");
                stringBuilder.append("[");
                if(node.getChildren().get(1) instanceof IdentifierNode){
                    typeCastArrayAccessNode(node,1);
                }
                visitChild(node.getChildren().get(1));
                stringBuilder.append("]");
            }
    }
    private void typeCastArrayAccessNode(ArrayAccessNode node, int childIndex){
        if(node.getChildren().get(childIndex) instanceof IdentifierNode){
            stringBuilder.append("(int) ");
        }
    }

    //Check if the array access node is a 1D array or 2D array
    private int accessArrayDimensions(AstNode node) {
      if (node.getParent().getChildren().get(0).getChildren().size() == 1) {
            return 1;
        } else {
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
        this.symbolTable.enterScope(node.getId());
        resetStringBuilder();

        appendPackage();

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
        appendImports("java.util","Arrays","UUID");

        appendClassDefinition(javaE.PUBLIC.getValue(), node.getId(),"UntypedAbstractActor");

        //append the body of the actor class
        appendBodyOpen(node);
        appendOnReceive(node);
        appendBodyClose();

        writeToFile(node.getId(), codeOutput);//Write the actor class to a separate file.
        this.symbolTable.leaveScope();
    }

    private void appendPackage(){
        stringBuilder.append("package output;\n \n");
    }

    //Can either be:
    //value : (primitive | arithExp | boolExp | actorAccess | arrayAccess | SELF | identifier)
    @Override
    public void visit(ArgumentsNode node) {
        ArrayList<String> formalParameters = findFormalParameters(node);
        if (node.getParent() instanceof SpawnActorNode ) {
            visitChildren(node, ", ", "", formalParameters);
        }else if(node.getParent() instanceof MethodCallNode || node.getParent() instanceof SendMsgNode ) {
            visitChildren(node, "", ",", formalParameters);
            if (node.getChildren().size() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        }else {
            throw  new RuntimeException("Parent of ArgumentsNode is not SpawnActorNode, MethodCallNode or SendMsgNode");
        }
        //if instance is method call
    }

    private ArrayList<String> findFormalParameters(ArgumentsNode node){
        LinkedHashMap<String, Attributes> params = null;
        ArrayList<String> formalParameterTypes = new ArrayList<>();
        AstNode parent = node.getParent();

        if (parent instanceof MethodCallNode methodCallNode) {
            String actorType = symbolTable.findActorParent(node);
            String methodName = methodCallNode.getMethodName();
            Scope methodScope = symbolTable.lookUpScope(methodName + actorType);
            params = methodScope.getParams();
        } else if (parent instanceof SpawnActorNode spawnActorNode) {
            //We can call SpawnActor from any scope, hence we have to find the Actor scope where the Spawn we are calling is declared
            Scope ActorScope = symbolTable.lookUpScope(spawnActorNode.getType());
            //Within the Actor Scope we enter the spawn scope to get the parameters associated with Spawn
            Scope SpawnScope = ActorScope.children.get(0);
            params = SpawnScope.getParams();
        } else if (parent instanceof SendMsgNode sendMsgNode) {
            //The first child of SendMsgNode is always a receiver node
            AstNode receiverNode = sendMsgNode.getChildren().get(0);
            String receiverName = sendMsgNode.getReceiver();
            //Method name is used to find the parameters to check the arguments up against
            String methodName = ((SendMsgNode) parent).getMsgName();
            Attributes attributes = null; //The attributes are used to get the correct method scope

            //The receiver can be: IdentifierNode, StateAccessNode, KnowsAccessNode or SelfNode
            if(receiverNode instanceof StateAccessNode){
                attributes = symbolTable.lookUpStateSymbol(receiverName.replaceAll("State\\.",""));
            }else if(receiverNode instanceof KnowsAccessNode){
                attributes = symbolTable.lookUpKnowsSymbol(receiverName.replaceAll("Knows\\.",""));
            }else if(receiverNode instanceof SelfNode){
                String actorName = symbolTable.findActorParent(receiverNode);
                attributes = symbolTable.lookUpSymbol(actorName);
            }else if(receiverNode instanceof IdentifierNode){
                attributes = symbolTable.lookUpSymbol(receiverName);
            }

            if(receiverNode instanceof SelfNode){
                Scope methodScope=symbolTable.lookUpScope(methodName+symbolTable.findActorParent(receiverNode));
                params = methodScope.getParams();
            }else {
                if (attributes != null) {
                    Scope methodScope = symbolTable.lookUpScope(methodName + attributes.getVariableType());
                    params = methodScope.getParams();
                }else{
                    throw new SendMsgException("Attributes of receiver: " + receiverName + "could not be found");
                }
            }
        }else {
            throw new ArgumentsException("Arguments node parent is not a method call, spawn actor or send message node");
        }

        if(params != null){
            SequencedCollection<Attributes> formalParameterAttributes = params.sequencedValues();
            for(Attributes attribute : formalParameterAttributes){
                formalParameterTypes.add(attribute.getVariableType());
            }
        }
        return formalParameterTypes;
    }

    private String determineValue(String type){
        return switch (type) {
            case "int" -> "Long.valueOf(";
            case "double" -> "Double.valueOf(";
            default -> "";
        };
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
        if(!(node.getChildren().get(1) instanceof ListNode)){
                visitChild(node.getChildren().get(0));
                stringBuilder.append(" = ");
                visitChild(node.getChildren().get(1));
        } else {
            visitChild(node.getChildren().get(0));
            visitChild(node.getChildren().get(1));
        }
        if(!(node.getParent() instanceof ForNode)){ //if the parent is not a for node, add a semicolon, else don't
            stringBuilder.append(javaE.SEMICOLON.getValue());
            codeOutput.add(getLine());
            }

    }

    @Override
    public void visit(BodyNode node) {
        if (node.getParent() instanceof MainDclNode) {
            stringBuilder
                    .append(javaE.CURLY_OPEN.getValue());
            codeOutput.add(getLine());
            localIndent++;
            stringBuilder
                    .append("ActorSystem system = ActorSystem.create(\"system\")")
                    .append(javaE.SEMICOLON.getValue());
            appendSpawnReaper();//appends code that spawn the reaper Actor.
            visitChildren(node);
            appendBodyClose();
        } else if (node.getParent() instanceof SpawnDclNode) {
            appendBodyOpen(node);
            stringBuilder.append("Reaper.sendWatchMeMessage(this);\n");//All actors register themselves at the reaper when they spawn.
            appendBodyClose();
        } else {
            appendBody(node);
        }
    }

    private void appendSpawnReaper(){
        appendInlineComment("Create Reaper Actor");
        stringBuilder.append("system.actorOf(Props.create(Reaper.class),\"reaper\");\n");

    }

    //In FactorialHelper this is: private final int currentValue;
    @Override
    public void visit(StateNode node) {
        visitChildren(node);
    }

    @Override
    public void visit(BoolNode node) {
        if (!node.getChildren().isEmpty()) {
            visitChild(node.getChildren().get(0));
        }
        else {
            stringBuilder.append(node.getValue());
        }
    }

    @Override
    public void visit(BoolAndExpNode node) {
        booleanFunction(node, " && ");
    }

    @Override
    public void visit(BoolExpNode node) {
        booleanFunction(node, " || ");
    }

    public void booleanFunction(ExpNode node, String string){
       for(int i = 0; i < node.getChildren().size(); i ++){
            if(node.getIsParenthesized()) {
                if (i == 0) {
                    stringBuilder.append("(");
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(string);
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
                    stringBuilder.append(string);
                    visitChild(node.getChildren().get(i));
                }
            }
        }
    }

    @Override
    public void visit(SelfNode node) {
        stringBuilder.append(javaE.GET_SELF.getValue());
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
        //this.symbolTable.enterScope(node.getNodeHash());
        stringBuilder.append(javaE.FOR.getValue()).append("(");
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
       // this.symbolTable.leaveScope();
    }

    //HashMap to convert the type of the array to the type of the arraylist

    private boolean isArray(AstNode node){
        return node.getType().contains("[");
    }
    private boolean isParrentArray(AstNode node) {
        if(node.getParent().getType()!=null){
            return node.getParent().getType().contains("[");
        }else{
            return false;
        }
    }

    @Override
    public void visit(IdentifierNode node) {
        if (node.getParent() instanceof KnowsNode) {
            stringBuilder
                    .append(javaE.PRIVATE.getValue())
                    .append(javaE.ACTORREF.getValue())
                    .append(node.getName())
                    .append(javaE.SEMICOLON.getValue());
            codeOutput.add(getLine());
        } else if (isArray(node) && !(node.getParent() instanceof PrintCallNode)) {
                if(!(node.getParent().getChildren().get(1) instanceof InitializationNode)) {
                    stringBuilder.append(VarTypeConverter(node.getType(),true,false))
                            .append(" ")
                            .append(node.getName());
                    stringBuilder
                            .append(" = ")
                            .append(javaE.NEW.getValue())
                            .append(VarTypeConverter(node.getType(),true,true));
                    /*
                            .append("[")
                            .append(((IntegerNode) node.getParent().getChildren().get(1)).getValue())
                            .append("]");
                    if(node.getParent().getChildren().size()==3){
                        stringBuilder
                                .append("[")
                                .append(((IntegerNode) node.getParent().getChildren().get(1)).getValue())
                                .append("]");
                    }
                    */


                } else {
                    stringBuilder.append(VarTypeConverter(node.getType(),true,false))
                            .append(" ")
                            .append(node.getName())
                            .append(" = new ").append(VarTypeConverter(node.getType(),true, false));
                }
        } else if(node.getType()!= null && isChildOfVarDclOrParameters(node)){
            String type;
            if(symbolTable.lookUpScope(node.getType())!=null) {//If there is a scope with the same name as the IdentierfierNode's type, then the type is an actor
                type=javaE.ACTORREF.getValue();
            }else{
                type= VarTypeConverter(node.getType(),false,false)+" ";
            }
            stringBuilder
                    .append(type)
                    .append(node.getName());
        }  else{
            stringBuilder.append(node.getName());
        }
    }

    /***
     *
     * @param node parent must be SendMsgNode
     */
    private boolean isChildOfVarDclOrParameters(IdentifierNode node){
        return (node.getParent() instanceof VarDclNode || node.getParent() instanceof ParametersNode);
    }

    @Override
    public void visit(InitializationNode node) {
        if(!isParrentArray(node)){
            stringBuilder.append(" = ");
        }
        visitChildren(node);
    }

    @Override
    public void visit(InitNode node) {
        createReaper();//creates Reaper actor responsible for terminating the actor system once all actors have been killed.
        visitChildren(node);
    }

    /***
     * creates a file named "Reaper" containing the Reaper actor class.
     */
    private void createReaper(){
        resetStringBuilder();

        appendPackage();
        //imports necessary for most akka actor classes
        appendImports("akka.actor", "ActorRef", "ActorSystem", "Props", "UntypedAbstractActor", "ActorSelection");
        appendImports("akka.event", "Logging", "LoggingAdapter");
        appendImports("java.util","Arrays","Set","HashSet","UUID");

        String actorName=parLangE.REAPER.getValue();
        appendClassDefinition(javaE.PUBLIC.getValue(),actorName, "UntypedAbstractActor");
        stringBuilder.append( " {\n");
        codeOutput.add(getLine() );
        localIndent++;
        appendSendWatchMeMessage();//a static method used by other actors for sending a WatchMe message to the reaper
        appendSendTerminatedMessage();/// a static method used by other actors for sending a Temrinated Message to the reaper

        stringBuilder.append("private final Set<ActorRef> watches = new HashSet<>();\n");//The set of actors the reaper watches. when they have all terminated, the reaper kills the system.

       //Protocol class for the WatchMe message.
        appendStaticFinalClassDef(javaE.PUBLIC.getValue(), "WatchMe");
        stringBuilder.append("{}\n");
        codeOutput.add(getLine());

        //private onWatchMe method which is executed when the reaper receives a WatchMe message
        stringBuilder.append("private void onWatchMe(){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("if(this.watches.add(getSender())){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("this.getContext().watch(getSender());\n");
        codeOutput.add(getLine());
        appendBodyClose();
        appendBodyClose();

        //Protocol class for the Terminated message
        appendStaticFinalClassDef(javaE.PUBLIC.getValue(), "Terminated");
        stringBuilder.append("{}\n");
        codeOutput.add(getLine());

        //private onTerminated method which is executed when the reaper receives a Terminated message
        stringBuilder.append("private void onTerminated(){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("if(this.watches.remove(getSender())){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("if (this.watches.isEmpty()){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("this.getContext().getSystem().terminate();");
        codeOutput.add(getLine());
        appendBodyClose();
        stringBuilder.append("else{\n");
        codeOutput.add(getLine());
        localIndent++;
        //stringBuilder.append("this.log().error(\"Got termination message from unwatched {}.\", sender);")
        appendBodyClose();
        appendBodyClose();
        appendBodyClose();

        //onReceive
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(javaE.VOID.getValue())
                .append(javaE.ONRECEIVE.getValue())//has value "onReceive(Object message) "
                .append("{\n");
        codeOutput.add(getLine());//get line and add to codeOutput before indentation changes.
        localIndent++;
        List<String> params=new ArrayList<>();//There are no parameters, but we mock an empty parameters list since  getOnReceiveIfBody() requires such a list.
        appendIfElseChainLink("if", getOnReceiveIfCondition("WatchMe", "watchMe"), getOnReceiveIfBody("watchMe",params.iterator()));
        appendIfElseChainLink("else if", getOnReceiveIfCondition("Terminated", "terminated"), getOnReceiveIfBody("terminated",params.iterator()));
        appendElse(javaE.UNHANDLED.getValue());
        appendBodyClose();
        appendBodyClose();

        writeToFile(actorName, codeOutput);//Write the actor class to a separate file.
    }

    private void appendSendWatchMeMessage(){
        stringBuilder.append("public static void sendWatchMeMessage(UntypedAbstractActor actor) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("ActorSelection reaper = actor.getContext().getSystem().actorSelection(\"/user/\" + \"reaper\");\n")
                .append("reaper.tell(new WatchMe(), actor.getSelf());\n");
        appendBodyClose();
    }

    private void appendSendTerminatedMessage(){
        stringBuilder.append("public static void sendTerminatedMessage(UntypedAbstractActor actor) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("ActorSelection reaper = actor.getContext().getSystem().actorSelection(\"/user/\" + \"reaper\");\n")
                .append("reaper.tell(new Terminated(), actor.getSelf());\n");
        appendBodyClose();
    }


    @Override
    public void visit(IntegerNode node) {
        if((isParrentArray(node) && node.getParent() instanceof VarDclNode)){
            stringBuilder.append("[")
                    .append(node.getValue())
                    .append("]");
        } else if (node.getParent() instanceof ArrayAccessNode) {
                stringBuilder.append(node.getValue());
        } else{
            stringBuilder
                    .append(node.getValue())
                    .append("L"); //java interprets integer literals as int by default. This converts it to long in the target code.
        }

    }

    @Override
    public void visit(IterationNode node) {

    }

    @Override
    public void visit(KnowsAccessNode node) {
        stringBuilder
                .append(javaE.THIS.getValue())
                .append(".")
                .append(node.getAccessIdentifier());
    }

    @Override
    public void visit(KnowsNode node) {
        visitChildren(node);
    }

    @Override
    public void visit(ListNode node) {
            stringBuilder.append("{");
            separateElementsList(node);
            stringBuilder.append("}");
    }
    //Separate the elements in the list by a comma
    private void separateElementsList(ListNode node) {
        for(int i = 0; i < node.getChildren().size(); i++){
            visitChild(node.getChildren().get(i));
            if(i != node.getChildren().size()-1){
                stringBuilder.append(", ");
            }
        }
    }

    @Override
    public void visit(LocalMethodBodyNode node) {
        appendBody(node);//Use the children of the LocalMethodBodyNode node to append the method's body in the target code.
    }

    @Override
    public void visit(MainDclNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        resetStringBuilder();

        appendPackage();
        //public class Main {
        appendImports("akka.actor",
                "ActorSystem",
                "ActorRef",
                "Props",
                "UntypedAbstractActor");
        appendImports("java.util","Arrays", "UUID");

        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(javaE.CLASS.getValue())
                .append("Main")
                .append(" {\n");
        codeOutput.add(getLine());
        localIndent++;
        //public static void main(String[] args) {
        //      ActorSystem system = ActorSystem.create("system");
        //} //end of main method
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(javaE.STATIC.getValue())
                .append(javaE.VOID.getValue())
                .append("main(String[] args)");
        //codeOutput.add(getLineBasic());
        visitChildren(node);
        appendBodyClose();
        writeToFile(capitalizeFirstLetter(node.getId()), codeOutput);
        this.symbolTable.leaveScope();
    }


    @Override
    public void visit(MethodCallNode node) {
        visit((IdentifierNode) node.getChildren().getFirst());
        stringBuilder.append("(");
        System.out.println(node.getChildren().size());
        if(node.getChildren().size()>1){
            visit((ArgumentsNode) node.getChildren().get(1));//ArgumentdNode
        }
        stringBuilder.append(")");
        stringBuilder.append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
    }

    @Override
    public void visit(MethodDclNode node) {
        this.symbolTable.enterScope(node.getId() + symbolTable.findActorParent(node));
        if(node.getMethodType().equals(parLangE.ON.getValue())){
            appendInlineComment(parLangE.ON.getValue()," method:"," ",node.getId());
            appendProtocolClass(node);
            appendBehvaiour(node);

            //To be done
        } else if (node.getMethodType().equals(parLangE.LOCAL.getValue())) {
            appendMethodDefinition(javaE.PRIVATE.getValue(), VarTypeConverter(node.getType(),true,false),node.getId());
            visit(node.getParametersNode());//append parameters in target code
            visit((LocalMethodBodyNode) node.getBodyNode()); //append the method's body in the target code.
        }
        this.symbolTable.leaveScope();
    }

    private void appendInlineComment(String... strings){
        stringBuilder.append(javaE.INLINE_COMMENT.getValue());
        for(String s:strings){
            stringBuilder.append(s);
        }
        stringBuilder.append("\n");

    }

    private void appendProtocolClass(MethodDclNode node){
        String className=capitalizeFirstLetter(node.getId());
        appendStaticFinalClassDef(javaE.PUBLIC.getValue(),className);//It is important that it is public since other actors must be able to access it.
        String fieldDclProlog=javaE.PUBLIC.getValue()+javaE.FINAL.getValue();
        appendBodyOpen(node.getChildren().getFirst(),fieldDclProlog,";\n");
        appendConstructor(className,(List<IdentifierNode>)(List<?>) node.getChildren().get(0).getChildren());
        appendBodyClose();
    }


    private void appendBehvaiour(MethodDclNode node){
        String name=parLangE.ON.getValue()+capitalizeFirstLetter(node.getId());
        appendMethodDefinition(javaE.PRIVATE.getValue(),javaE.VOID.getValue(),name);
        appendParameters((ParametersNode) node.getChildren().getFirst());
        appendBody(node.getChildren().get(1));
    }

    private String getMethodInFollowedScript(MethodDclNode node){
        //MethodDclNode's parent is always an actor.
        // If the actor follows a script, a FollowsNode is the first child of this actor
        AstNode firstChildOfActor=node.getParent().getChildren().getFirst();
        if(firstChildOfActor instanceof FollowsNode followsNode){
            List<IdentifierNode> followedScripts=(List<IdentifierNode>)(List<?>) followsNode.getChildren();//Casting through intermediate wildcard type in or to be able to cast the list.
            for(IdentifierNode script:followedScripts){
                HashMap<String, Attributes> scriptOnMethods=symbolTable.lookUpScope(script.getName()).getDeclaredOnMethods();
                if(scriptOnMethods.containsKey(node.getId())){
                    return script.getName();
                }
            }
        }
        return null;
    }

    @Override
    public void visit(NegatedBoolNode node) {
        stringBuilder.append("!");
        visitChild(node.getChildren().get(0));
    }

    @Override
    public void visit(PrintCallNode node) {
        stringBuilder.append("System.out.println(");
        if(isTwoDimensionalArray(node)){
            printTwoDimensionalArray(node);
        }
        else if(isOneDimensionalArray(node)){
            printOneDimensionalArray(node);
        }
        else{
            visitChild(node.getChildren().get(0));
        }
        visitPrintChildrenFromChildOne(node);
        stringBuilder.append(")").append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
    }
    //Check if the print call node is a one dimensional array
    private boolean isOneDimensionalArray(PrintCallNode node) {
       return node.getChildren().get(0).getType().contains("[]");
    }
    //Check if the print call node is a two dimensional array
    private boolean isTwoDimensionalArray(PrintCallNode node) {
        return node.getChildren().get(0).getType().contains("[][]");
    }
    //Print the one dimensional array
    private void printOneDimensionalArray(PrintCallNode node){
        stringBuilder.append("Arrays.toString(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
    }
    //Print the two dimensional array
    private void printTwoDimensionalArray(PrintCallNode node){
        stringBuilder.append("Arrays.deepToString(");
        visitChild(node.getChildren().get(0));
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
        } else if (returnee instanceof StateAccessNode) {
            visit((StateAccessNode) returnee);
        } else if(returnee instanceof KnowsAccessNode){
            visit((KnowsAccessNode) returnee);
        } else if (returnee instanceof LiteralNode){
            stringBuilder.append(((LiteralNode<?>) returnee).getValue());
        } else if (returnee==null) {//If nothing is returned, delete extra space after "return".
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
    }




    @Override
    public void visit(ScriptDclNode node) {
        this.symbolTable.enterScope(node.getId());
        resetStringBuilder();

        appendPackage();

        //We crate a public class for the script with the same name as the script.
        appendImports("akka.actor","ActorRef");
        appendClassDefinition(javaE.PUBLIC.getValue(),node.getId(),null);
        appendBody(node);//The body of the class has a static class for each on-method declared in the script.

        writeToFile(node.getId(),codeOutput); //The class is written to a separate file.
        this.symbolTable.leaveScope();
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
        if(node.getParent() instanceof MethodDclNode || node.getParent() instanceof SpawnDclNode) {
            //If parameters is part of method declaration in an actor we simply append them to the method declaration in the target code
            appendParameters(node);
        }else if (node.getParent() instanceof ScriptMethodNode){
            //If the method is declared in a script, the parameters are mapped to fields in the static class representing the method
            localIndent++;
            visitChildren(node, javaE.PUBLIC.getValue(),javaE.SEMICOLON.getValue(), null); //Insterts the parameters ad public fields in the method's static class
            localIndent--;
            codeOutput.add(getLine() );
        }else{
            throw new RuntimeException("ParametersNode not instance of MethodDclNode, SpawnDclNode or SrciptMethodNode");
        }
    }

    private void appendParameters(ParametersNode node){
        stringBuilder.append("(");
        visitChildren(node,"",",", null);//appends list of parameters. There is a surplus comma after last parameter: "int p1, int p2,"
        if(node.getChildren().size()>0){
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);//delete the surplus comma
        }
        stringBuilder.append(")");
    }



    //Standard selection node construction with if and else statements
    @Override
    public void visit(SelectionNode node) {
        stringBuilder.append(javaE.IF.getValue()).append("(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
        visitChild(node.getChildren().get(1));

        //check if there is an else statement
        if(node.getChildren().size() > 2){
            stringBuilder.append(javaE.ELSE.getValue());
            visitChild(node.getChildren().get(2));
        }
    }

    @Override
    public void visit(SendMsgNode node) {
        appendTellOpen(node);
        appendProtocolArg(node);
        appendTellClose(node);

    }

    private void appendTellOpen(SendMsgNode node){
        visitReceiver(node);
        stringBuilder
                .append(".")
                .append(javaE.TELL.getValue())
                .append("(");
    }

    private void appendTellClose(SendMsgNode node){
        String sender;
        if(node.getParent().getParent() instanceof MainDclNode){
            sender=javaE.NO_SENDER.getValue();
        }else {
            sender=javaE.GET_SELF.getValue();
        }
        stringBuilder
                .append(sender)
                .append(");");
        codeOutput.add(getLine());
    }

    private void appendProtocolArg(SendMsgNode node){
        String protocolClass=capitalizeFirstLetter(node.getMsgName());
        stringBuilder
                .append(javaE.NEW.getValue())
                .append(node.getChildren().getFirst().getType())
                .append(".")
                .append(protocolClass)
                .append("(");
        visit((ArgumentsNode) node.getChildren().getLast());//visit the ArgumentsNode
        stringBuilder.append("),");

    }

    private void visitReceiver(SendMsgNode node){
        AstNode firstChild=node.getChildren().getFirst();
        if(firstChild instanceof IdentifierNode){
            visit((IdentifierNode) firstChild); //visit the IdentifierNode
        }else if (firstChild instanceof  KnowsAccessNode){
            visit((KnowsAccessNode) firstChild); //visit the KnowsAccessNode
        }
        else if(node.getReceiver().equals("self")){ //receiver is self
            visit((SelfNode) firstChild);
        }else{
            throw new RuntimeException("Receiver of message is not an IdentifierNode, KnowsAccessNode or selfnode");
        }
    }
    
    private void getNextUniqueActor() {
        stringBuilder.append("UUID.randomUUID().toString()");
    }


    //					SpawnActorNode : HelloWorldMain with type: HelloWorldMain
    //						ArgumentsNode
    //							IntegerNode : 10 with type: int
    @Override
    public void visit(SpawnActorNode node) {
        String outerScopeName = symbolTable.findActorParent(node);
        if (outerScopeName != null) { //Actor or Script
            stringBuilder.append("getContext().actorOf(Props.create(");
        } else { //null means it's main
            stringBuilder.append("system.actorOf(Props.create(");
        }
        stringBuilder
                .append(node.getType())
                .append(".")
                .append(javaE.CLASS.getValue());
        visitChildren(node);
        stringBuilder.append("), ");
        getNextUniqueActor();
        stringBuilder.append(")");
        if (!(node.getParent() instanceof InitializationNode ||node.getParent() instanceof  AssignNode)) {
            stringBuilder.append(javaE.SEMICOLON.getValue());
        }
    }

    @Override
    public void visit(SpawnDclNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(symbolTable.findActorParent(node));
        if(node.getChildren().size()<2){//If there is no ParametersNode (only a body node)
            stringBuilder.append("()");
        }
        visitChildren(node);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(ExpNode node) {//abstract

    }

    @Override
    public void visit(StateAccessNode node) {
        stringBuilder
                .append(javaE.THIS.getValue())
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
            stringBuilder.append(javaE.PRIVATE.getValue());
        }
        if(isArray(node) && node.getChildren().get(1) instanceof InitializationNode) {
            visitChild(node.getChildren().get(0));
            visitChild(node.getChildren().get(1));

        } else{
            visitChildren(node);

        }


        //if the parent is not a for node, add a semicolon, else don't
        if(!(node.getParent() instanceof ForNode)){
                stringBuilder.append(javaE.SEMICOLON.getValue());
                codeOutput.add(getLine());
            }
    }

    //Standard while loop construction
    @Override
    public void visit(WhileNode node) {
        stringBuilder.append(javaE.WHILE.getValue()).append("(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
        visitChild(node.getChildren().get(1));
        stringBuilder.append("\n");
        codeOutput.add(getLine());
    }

    @Override
    public void visit(BoolCompareNode node){

    }

    @Override
    public void visit(KillNode node) {
        stringBuilder
                .append("Reaper.sendTerminatedMessage(this);\n")//The actor informs the reaper that it is dead.
                .append("getContext().stop(getSelf());\n");//The actor kills itself.
        codeOutput.add(getLine());
    }
}
