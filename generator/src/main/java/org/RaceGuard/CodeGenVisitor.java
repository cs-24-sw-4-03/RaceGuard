package org.RaceGuard;

import org.RaceGuard.AstNodes.*;
import org.RaceGuard.symbols.Attributes;
import org.RaceGuard.symbols.Scope;
import org.RaceGuard.symbols.SymbolTable;
import org.RaceGuard.Exceptions.ArgumentsException;
import org.RaceGuard.Exceptions.SendMsgException;

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
     * Generates correct indentation for the StringBuilder. 4 spaces per indent.
     * @return the current string from StringBuilder with the correct indentation.
     *  getLine() is used throughout CodeVisitor like this: "codeOutput.add(getLine());". This gets the current string in the stringBuilder (current line) with indentation given by localIndent at this moment, resets stringBuilder, and adds the line to codeOutput.
     */
    private String getLine() {
        String line = stringBuilder.toString().indent(localIndent* 4);
        stringBuilder.setLength(0); // Resets string builder
        return line;
    }

    /**
     * Resets the stringBuilder, codeOutput and sets indent to 0.
     * Most commonly used before generating a new file.
     */
    private void resetStringBuilder(){
        stringBuilder.setLength(0);
        codeOutput.clear();
        localIndent = 0;
    }

    /**
     * <p>Writes the generated code to a file. </p>
     * The method creates a new file with the given name in the specified directory (dirPath).
     * Each string in codeOutput is written as a new line in the file.
     * If the file already exists, it will be overwritten.
     *
     * @param fileName the name of the file to be created (e.g. "FactorialMain")
     * @param codeOutput an ArrayList of Strings containing the generated code
     */
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


    public void visitChild(AstNode node){
        node.accept(this);
    }

    /**
     * <p>Visits all children of a given AST node</p>
     * Used to traverse the AST by iterating over all children of a node and accepts each one.
     * @param node the AST node whose children are to visit.
     */
    @Override
    public void visitChildren(AstNode node) {
        for (AstNode childNode : node.getChildren()) {
            childNode.accept(this);
        }
    }

    /**
     * Overloaded version of visitChildren, where the String before and after is appended before and after the result
     * of visiting the child node. Also casts the actual parameters of the child node to the correct type if necessary.
     * @param node the AST node whose children are to visit.
     * @param before the string to append before the result of visiting the child node.
     * @param after the string to append after the result of visiting the child node.
     * @param parameterTypes If children are actual parameters in a call ect, we need formal parameter types to determine if we need to cast the value
     */
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

    /**
     * Helper method, to convert int and double to correct type for Akka
     * @param type int, double or empty string
     * @return the correct type for Akka
     */
    private String determineValue(String type){
        return switch (type) {
            case "int" -> "Long.valueOf(";
            case "double" -> "Double.valueOf(";
            default -> "";
        };
    }

    @Override
    public void visit(ArrayAccessNode node){
        String name = removeBeforeDotAddSelf(node.getAccessIdentifier());
        stringBuilder.append(name);
        if(node.getChildren().size() == 1){
            stringBuilder.append("[");
            typeCastArrayAccessNode(node,0);
            stringBuilder.append("]");

        } else if (node.getChildren().size()==2){
            stringBuilder.append("[");
            typeCastArrayAccessNode(node,0);
            stringBuilder.append("]");

            stringBuilder.append("[");
            typeCastArrayAccessNode(node,1);
            stringBuilder.append("]");
        }
    }
    /**
     * Helper method, that deletes everything before the "." and replaces it with "this", and returns the string.
     * There might not be a "." in the string, in which case the string is returned as is.
     * @param methodName the string to modify
     */
    private String removeBeforeDotAddSelf(String methodName) {
        if(methodName.contains(".")){
            return "this" + methodName.substring(methodName.indexOf("."));
        }
        return methodName;
    }
    /**
     * Helper method, to cast index of array to an int. index cannot be a long.
     * @param node the ArrayAccessNode
     * @param childIndex the index of the child node (typically 0 or 1)
     */
    private void typeCastArrayAccessNode(ArrayAccessNode node, int childIndex){
        AstNode nodeType = node.getChildren().get(childIndex);
        if (nodeType instanceof IdentifierNode) {
            stringBuilder.append("(int) ");
            visitChild(node.getChildren().get(childIndex));
        }
        else if (nodeType instanceof ArithExpNode) {
            stringBuilder.append("(int) (");
            visitChild(node.getChildren().get(childIndex));
            stringBuilder.append(")");
        }
        else if (nodeType instanceof ArrayAccessNode) {
            visitChild(node.getChildren().get(childIndex));
            stringBuilder.append(".intValue()");
        }
        else { // IntegerNode is included here.
            visitChild(node.getChildren().get(childIndex));
        }
    }

    @Override
    public void visit(ActorDclNode node) {
        this.symbolTable.enterScope(node.getId());
        resetStringBuilder();

        appendPackage(javaE.PACKAGE_NAME.getValue());

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
        appendImport("java.lang.reflect","Array");

        appendClassDefinition(javaE.PUBLIC.getValue(), node.getId(),"UntypedAbstractActor");

        //append the body of the actor class
        appendBodyOpen(node);
        appendOnReceive(node);
        stringBuilder.append("private LoggingAdapter log = Logging.getLogger(getContext().system(), this);\n");
        codeOutput.add(getLine());
        appendCloneArray();
        appendBodyClose();
        writeToFile(node.getId(), codeOutput);//Write the actor class to a separate file.
        this.symbolTable.leaveScope();
    }

    /***
     * Appends onReceive() method to the body of an Actor.
     * @param node The ActorDclNode in the AST which is used to produce the body of the actor in the target code.
     */
    private void appendOnReceive(ActorDclNode node){
        String actorName=node.getId();
        Scope actorScope=symbolTable.lookUpScope(actorName);//Get the scope of the actor.
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
            className = getClassName(node, methodName);//Get protocol class. If the method is in a script, this class will be the static protocol class in the Script-class.
            Set<String> params=symbolTable.lookUpScope(methodName+actorName).getParams().keySet();
            appendIfElseChainLink("if", getOnReceiveIfCondition(className, methodName), getOnReceiveIfBody(methodName,params.iterator()));
            if(className.contains(".")){//If first protocol class is from a script (i.e. GreeterScript.Greet), then we also add a receiver for the protocol class in the actor (i.e "Greet")
                appendIfElseChainLink("else if",getOnReceiveIfCondition(methodName, methodName),getOnReceiveIfBody(methodName,params.iterator()));
            }
            while (onMethods.hasNext()) {//The remaining on-methods results in if-else statements
                methodName = onMethods.next();
                className = getClassName(node, methodName);
                params=symbolTable.lookUpScope(methodName+actorName).getParams().keySet();
                appendIfElseChainLink("else if", getOnReceiveIfCondition(className, methodName), getOnReceiveIfBody(methodName,params.iterator()));
                if(className.contains(".")){
                    appendIfElseChainLink("else if",getOnReceiveIfCondition(methodName,methodName),getOnReceiveIfBody(methodName,params.iterator()));
                }
            }
            appendElse(javaE.UNHANDLED.getValue());//There is always and else statement in the end of the chain handling yet unhandled messages.
        } else {
            stringBuilder.append(javaE.UNHANDLED.getValue());
            codeOutput.add(getLine());
        }
        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLine()); //get line and add to codeOutput since indentation might change after calling this method.
    }

    @Override
    public void visit(ArgumentsNode node) {
        ArrayList<String> formalParameters = findFormalParameters(node);
        if (node.getParent() instanceof SpawnActorNode ) {
            visitChildren(node, ", ", "", formalParameters);
        } else if(node.getParent() instanceof MethodCallNode || node.getParent() instanceof SendMsgNode ) {
            visitChildren(node, "", ",", formalParameters);
            if (node.getChildren().size() > 0) {
                stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
        } else {
            throw  new RuntimeException("Parent of ArgumentsNode is not SpawnActorNode, MethodCallNode or SendMsgNode");
        }
    }

    /**
     * Finds the formal parameters of the method that the actual parameters are used in.
     * @param node The ArgumentsNode
     * @return A list of the formal parameter types for the method that the actual parameters are used in.
     */
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
            } else if(receiverNode instanceof ArrayAccessNode) {
                String arrayName = ((ArrayAccessNode) receiverNode).getAccessIdentifier();
                attributes = symbolTable.lookUpSymbol(arrayName);
            }

            if(receiverNode instanceof SelfNode){
                Scope methodScope=symbolTable.lookUpScope(methodName+symbolTable.findActorParent(receiverNode));
                params = methodScope.getParams();
            }else {
                if (attributes != null) {
                    Scope methodScope = symbolTable.lookUpScope(methodName + attributes.getVariableType().split("\\[")[0]);
                    params = methodScope.getParams();
                }else{
                    throw new SendMsgException("Attributes of receiver: " + receiverName + " could not be found");
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

    @Override
    public void visit(ArithExpNode node) {
            if(node.getIsParenthesized()) {
                stringBuilder.append("(");
            }

            visitChild(node.getChildren().get(0));
            stringBuilder.append(" ").append(node.getOpType().getValue()).append(" ");
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
            codeOutput.add(getLine());
            appendBodyClose();
        } else {
            appendBody(node);
        }
    }

    /**
     * Appends the creation of the reaper. (used in e.g. MainDclNode)
     */
    private void appendSpawnReaper(){
        appendInlineComment("Create Reaper Actor");
        stringBuilder.append("system.actorOf(Props.create(Reaper.class),\"reaper\");\n");
    }

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

    /**
     * Helper method for boolean functions. Used for both BoolAndExpNode and BoolExpNode.
     * It appends the correct string between the children of the node.
     * @param node the ExpNode to visit
     * @param logicOperator the operator to append between the children
     */
    public void booleanFunction(ExpNode node, String logicOperator){
       for(int i = 0; i < node.getChildren().size(); i ++){
            if(node.getIsParenthesized()) {
                if (i == 0) {
                    stringBuilder.append("(");
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(logicOperator);
                    visitChild(node.getChildren().get(i));
                    if(node.getChildren().size()-1 == node.getChildren().indexOf(node.getChildren().get(i))){ //if last element
                        stringBuilder.append(")");
                    }
                }
            }
            else { //if not parenthesized
                if (i == 0) {
                    visitChild(node.getChildren().get(i));
                } else {
                    stringBuilder.append(logicOperator);
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
        this.symbolTable.enterScope(node.getNodeHash());
        stringBuilder.append(javaE.FOR.getValue()).append("(");
        //check if the second child is a compare expression and the third child is an assign node
        if (node.getChildren().get(1) instanceof CompareExpNode && node.getChildren().get(2) instanceof AssignNode) {
            visitChild(node.getChildren().get(0));
            stringBuilder.append(javaE.SEMICOLON.getValue());
            visitChild(node.getChildren().get(1));
            stringBuilder.append(javaE.SEMICOLON.getValue());
            visitChild(node.getChildren().get(2));
            stringBuilder.append(")");
            visitChild(node.getChildren().get(3));
        } else { //check if the first child is a compare expression
            if (node.getChildren().get(0) instanceof CompareExpNode) {
                stringBuilder.append(javaE.SEMICOLON.getValue());
                visitChild(node.getChildren().get(0));
                stringBuilder.append(" ;"); //Cannot use javaE.SEMICOLON.getValue() here, because of the space
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
        symbolTable.leaveScope();
    }

    @Override
    public void visit(IdentifierNode node) {
        if (node.getParent() instanceof KnowsNode) { //Knows nodes become private instance fields
            stringBuilder
                    .append(javaE.PRIVATE.getValue())
                    .append(javaE.ACTORREF.getValue())
                    .append(node.getName())
                    .append(javaE.SEMICOLON.getValue());
            codeOutput.add(getLine());
        } else if(node.getParent() instanceof PrintCallNode){
            if(isArray(node)){
                stringBuilder
                        .append("Arrays.deepToString(")
                        .append(node.getName())
                        .append(")");
            }else if(symbolTable.findActorParent(node)==null){//If print call is in main we use System.out.println() => The identifier name can be directly inserted.
                    stringBuilder.append(node.getName());
            } else { //If print call is in actor we use log.info() meaning that we have to use pattern matching for the identifier. (see akka log docs)
                    stringBuilder.append("\"{}\"");
            }
        } else if(node.getType()!= null && node.getParent() instanceof VarDclNode){
            if (isArray(node) ) {
                /*We have to do almost the same if the array is initialized or not.
                Initialized e.g.: "ActorRef[] army = new ActorRef[] {ryan, bo, gorm};" ("{ryan, bo, gorm}" is handled in visit(ListNode node))
                No initialized e.g.: "ActorRef[] largerArmy = new ActorRef[5]; (On the right hand side of "=" the "[5]" comes from visit(IntegerNode node) and the "[]" in "ActorRef[]" is removed ).*/

                //find out if array is initialized.
                boolean isInitialized;
                if (node.getParent().getChildren().size() > 1) {
                    isInitialized = node.getParent().getChildren().get(1) instanceof InitializationNode;
                } else {
                    isInitialized = false;
                }

                //Append declaration
                stringBuilder
                        .append(VarTypeConverter(node.getType(),true,false)) //Append array type
                        .append(node.getName())//Append name
                        .append(" = ")
                        .append(javaE.NEW.getValue())
                        .append(VarTypeConverter(node.getType(),true,!isInitialized));//Remove brackets in the array type if it is not initialized.
                if(!isInitialized ){ //If array is initialized there is an unnecessary space
                    stringBuilder.deleteCharAt(stringBuilder.length()-1);//remove space
                }
            }else if(isParrentArray(node)) {//In this case the IdentifierNode is of type int and used to set size of array. E.g. "int n=10; int[n] arr;"
                stringBuilder
                        .append("[")
                        .append("(int) ")
                        .append(node.getName())
                        .append("]");
            }else{
                stringBuilder
                        .append(VarTypeConverter(node.getType(),true,false))
                        .append(node.getName());
            }
        }  else if(node.getType()!= null && node.getParent() instanceof ParametersNode){
            stringBuilder
                    .append(VarTypeConverter(node.getType(),true,false))
                    .append(node.getName());
        } else if (node.getParent() instanceof ArgumentsNode && isArray(node)) { //We need to clone arrays
                if (!(node.getParent().getParent() instanceof SpawnActorNode)) { //the constructor in Akka is only happy with object array. It breaks if we try to cast to the specific array type.
                    stringBuilder
                            .append("(")
                            .append(VarTypeConverter(node.getType(), true, false));
                    stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");//remove space
                    stringBuilder
                            .append(") ");
                }
            stringBuilder.append("cloneArray(")
                    .append(node.getName())
                    .append(")");
        } else {
            stringBuilder.append(node.getName());
        }
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

    /**
     * creates a seperate file named "Reaper" containing the Reaper actor class.
     */
    private void createReaper(){
        resetStringBuilder();

        appendPackage(javaE.PACKAGE_NAME.getValue());
        //imports necessary for most akka actor classes
        appendImports("akka.actor", "*");
        appendImports("akka.event", "Logging", "LoggingAdapter");
        appendImports("java.util","Set","HashSet");

        String actorName= RaceGuardE.REAPER.getValue();
        appendClassDefinition(javaE.PUBLIC.getValue(),actorName, "UntypedAbstractActor");
        stringBuilder.append( " {\n");
        codeOutput.add(getLine() );
        localIndent++;
        appendSendWatchMeMessage();//a static method used by other actors for sending a WatchMe message to the reaper

        stringBuilder.append("private final Set<ActorRef> watches = new HashSet<>();\n");//The set of actors the reaper watches. when they have all terminated, the reaper kills the system.

       //Protocol class for the WatchMe message.
        appendStaticFinalClassDef(javaE.PUBLIC.getValue(), "WatchMe");
        stringBuilder.append("{}\n");
        codeOutput.add(getLine());

        //private onWatchMe method which is executed when the reaper receives a WatchMe message
        stringBuilder.append("private void onwatchMe(){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("if(this.watches.add(getSender())){\n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder.append("this.getContext().watch(getSender());\n");
        codeOutput.add(getLine());
        appendBodyClose();
        appendBodyClose();

        //We use protocol class Terminated from akka.actor package

        //private onTerminated method which is executed when the reaper receives a Terminated message
        stringBuilder.append("private void onterminated(){\n");
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

    /**
     * A static method used by other actors for sending a WatchMe message to the reaper
     */
    private void appendSendWatchMeMessage(){
        stringBuilder.append("public static void sendWatchMeMessage(UntypedAbstractActor actor) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("ActorSelection reaper = actor.getContext().getSystem().actorSelection(\"/user/\" + \"reaper\");\n")
                .append("reaper.tell(new WatchMe(), actor.getSelf());\n");
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
            boolean isInitializationOfArray=node.getParent().getParent() instanceof InitializationNode // One dimensional array
                    || node.getParent().getParent().getParent() instanceof InitializationNode // Two dimensional array
                    && isArray(node.getParent().getParent());// e.g. int[5] a = {1L, 2L, 3L, 4L, 5L} or int[2][2] a = {{0L, 1L}, {2L, 3L}}

            boolean isAssignedToArrayElement=node.getParent() instanceof AssignNode  && node.getParent().getChildren().getFirst() instanceof ArrayAccessNode;// e.g. a[2] = 10;
            stringBuilder.append(node.getValue());
            if(isInitializationOfArray||isAssignedToArrayElement){
                stringBuilder.append("L");//append L when Integer is in an array. Necessary since array would be of type Long[] or Long[][].
            }
        }
    }

    @Override
    public void visit(IterationNode node) { // Abstract Class
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

    /**
     * Separate the elements in the list by a comma
     */
    private void separateElementsList(ListNode node) {
        for(int i = 0; i < node.getChildren().size(); i++){
            visitChild(node.getChildren().get(i));
            if(i != node.getChildren().size()-1){
                stringBuilder.append(javaE.COMMA.getValue());
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

        appendPackage(javaE.PACKAGE_NAME.getValue());
        appendImports("akka.actor",
                "ActorSystem",
                "ActorRef",
                "Props",
                "UntypedAbstractActor");
        appendImports("akka.event","Logging","LoggingAdapter");
        appendImports("java.util","Arrays", "UUID");
        appendImport("java.lang.reflect","Array");

        appendClassDefinition(javaE.PUBLIC.getValue(), RaceGuardE.MAIN.getValue(),null);
        stringBuilder.append(javaE.CURLY_OPEN.getValue());
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(javaE.STATIC.getValue())
                .append(javaE.VOID.getValue())
                .append("main(String[] args)");
        visitChildren(node);
        appendCloneArray();
        appendBodyClose();
        writeToFile(capitalizeFirstLetter(node.getId()), codeOutput);
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(MethodCallNode node) {
        visit((IdentifierNode) node.getChildren().getFirst());
        stringBuilder.append("(");
        if(node.getChildren().size()>1){
            visit((ArgumentsNode) node.getChildren().get(1));//ArgumentsNode
        }
        stringBuilder.append(")");
        if (!(node.getParent() instanceof ArithExpNode || node.getParent() instanceof InitializationNode)) {
            stringBuilder.append(javaE.SEMICOLON.getValue());
            codeOutput.add(getLine());
        }
    }

    @Override
    public void visit(MethodDclNode node) {
        this.symbolTable.enterScope(node.getId() + symbolTable.findActorParent(node));
        if(node.getMethodType().equals(RaceGuardE.ON.getValue())){
            appendInlineComment(RaceGuardE.ON.getValue()," method:"," ",node.getId());
            appendProtocolClass(node);
            appendBehaviour(node);
        } else if (node.getMethodType().equals(RaceGuardE.LOCAL.getValue())) {
            appendMethodDefinition(javaE.PRIVATE.getValue(), VarTypeConverter(node.getType(),true,false),node.getId());
            visit(node.getParametersNode());//append parameters in target code
            visit((LocalMethodBodyNode) node.getBodyNode()); //append the method's body in the target code.
        }
        this.symbolTable.leaveScope();
    }

    /**
     * Appends the protocol class for the on-method
     * @param node The MethodDclNode
     */
    private void appendProtocolClass(MethodDclNode node){
        String className = node.getId();
        appendStaticFinalClassDef(javaE.PUBLIC.getValue(),className);//It is important that it is public since other actors must be able to access it.
        String fieldDclProlog=javaE.PUBLIC.getValue()+javaE.FINAL.getValue();
        appendBodyOpen(node.getChildren().getFirst(),fieldDclProlog,javaE.SEMICOLON.getValue());
        appendConstructor(className,(List<IdentifierNode>)(List<?>) node.getChildren().get(0).getChildren());
        appendBodyClose();
    }

    /**
     * Appends behaviour class for the on-method (e.g. private void onXXX(xx,xx) )
     * @param node MethodDclNode
     */
    private void appendBehaviour(MethodDclNode node){
        String name= RaceGuardE.ON.getValue()+node.getId();
        appendMethodDefinition(javaE.PRIVATE.getValue(),javaE.VOID.getValue(),name);
        appendParameters((ParametersNode) node.getChildren().getFirst());
        appendBody(node.getChildren().get(1));
    }

    @Override
    public void visit(NegatedBoolNode node) {
        stringBuilder.append("!");
        visitChild(node.getChildren().get(0));
    }

    @Override
    public void visit(PrintCallNode node) {
        boolean calledInMain;
        if(calledInMain=symbolTable.findActorParent(node)==null){
            stringBuilder.append("System.out.println(");
        }else{
            stringBuilder.append("log.info(");
        }

        visitPrintChildrenFromChildOne(node,calledInMain);
        stringBuilder.append(")").append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
    }

    /**
     * Visit all the children of the print call node except the first one
     * @param node PrintCallNode
     * @param calledInMain If not called in main append variables
     */
    private void visitPrintChildrenFromChildOne(PrintCallNode node, boolean calledInMain) {
        String variables="";
        int size=node.getChildren().size();
            for(int i = 0; i < size; i++){
                AstNode child= node.getChildren().get(i);
                if(!isArray(child) && child instanceof IdentifierNode idChild){
                        variables+=", "+ idChild.getName();
                }
                stringBuilder.append("String.valueOf(");
                visitChild(node.getChildren().get(i));
                stringBuilder.append(")");

                if(!(i==size-1)){
                    stringBuilder.append(" + ");
                }
            }

        if(!variables.isEmpty()&&!calledInMain){
            stringBuilder.append(variables);
        }
    }

    @Override
    public void visit(ReturnStatementNode node) {
        stringBuilder.append(javaE.RETURN.getValue());
        AstNode returnee=node.getReturnee();//get the expression which is returned (return <returnee>;)
        if(returnee instanceof IdentifierNode) {
            visit((IdentifierNode) returnee);
        } else if(returnee instanceof ArithExpNode) {
            visit((ArithExpNode) returnee);
        } else if(returnee instanceof BoolExpNode) {
            visit((BoolExpNode) returnee);
        } else if(returnee instanceof StateAccessNode) {
            visit((StateAccessNode) returnee);
        } else if(returnee instanceof KnowsAccessNode){
            visit((KnowsAccessNode) returnee);
        } else if(returnee instanceof LiteralNode){
            stringBuilder.append(((LiteralNode<?>) returnee).getValue());
        } else if(returnee==null) {//If nothing is returned, delete extra space after "return".
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(javaE.SEMICOLON.getValue());
        codeOutput.add(getLine());
    }

    @Override
    public void visit(ScriptDclNode node) {
        this.symbolTable.enterScope(node.getId());
        resetStringBuilder();

        appendPackage(javaE.PACKAGE_NAME.getValue());

        //Create a public class for the script with the same name as the script.
        appendImports("akka.actor","ActorRef");
        appendClassDefinition(javaE.PUBLIC.getValue(),node.getId(),null);
        appendBody(node);//The body of the class has a static class for each on-method declared in the script.

        writeToFile(node.getId(),codeOutput); //The class is written to a separate file.
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(ScriptMethodNode node) {
        if(node.getMethodType().equals(RaceGuardE.ON.getValue())){//local methods declared in a script does not need to be handled here.
            String className=node.getId();
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
        } else if(node.getParent() instanceof ScriptMethodNode){
            //If the method is declared in a script, the parameters are mapped to fields in the static class representing the method
            localIndent++;
            visitChildren(node, javaE.PUBLIC.getValue(),javaE.SEMICOLON.getValue(), null); //Insterts the parameters ad public fields in the method's static class
            localIndent--;
            codeOutput.add(getLine() );
        } else {
            throw new RuntimeException("ParametersNode not instance of MethodDclNode, SpawnDclNode or SrciptMethodNode");
        }
    }

    /**
     * Appends the parameters of a method to the StringBuilder. Also separates args by comma.
     * @param node The ParametersNode
     */
    private void appendParameters(ParametersNode node){
        stringBuilder.append("(");
        visitChildren(node,"",", ", null);//appends list of parameters. There is a surplus comma after last parameter: "int p1, int p2,". Cannot use javaE.COMMA.getValue() here because of the space.
        if(node.getChildren().size()>0){
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);//delete the surplus comma and space
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(")");
    }

    //Standard selection node construction with if and else statements
    @Override
    public void visit(SelectionNode node) {
        this.symbolTable.enterScope(node.getNodeHash());
        stringBuilder.append(javaE.IF.getValue()).append("(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
        visitChild(node.getChildren().get(1));

        //check if there is an else statement
        if(node.getChildren().size() > 2){
            stringBuilder.append(javaE.ELSE.getValue());
            visitChild(node.getChildren().get(2));
        }
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(SendMsgNode node) {
        appendTellOpen(node); // ".tell("
        appendProtocolArg(node); // e.g. "new FibCalculator.Calculate(Long.valueOf(number)),"
        appendTellClose(node); // e.g. "getSelf());"
    }

    /**
     * Appends the opening of the tell method with the receiver of the message. e.g. .tell(
     * @param node SendMsgNode
     */
    private void appendTellOpen(SendMsgNode node){
        visitReceiver(node);
        stringBuilder
                .append(".")
                .append(javaE.TELL.getValue())
                .append("(");
    }

    /**
     * Appends the closing of the tell method with the sender of the message.
     * if being sent from main, there's no sender.
     * @param node SendMsgNode
     */
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

    /**
     * Appends the protocol argument of the tell method. (e.g. new FibCalculator.Calculate(Long.valueOf(number)),
     * @param node SendMsgNode
     */
    private void appendProtocolArg(SendMsgNode node){
        String protocolClass=node.getMsgName();
        stringBuilder
                .append(javaE.NEW.getValue())
                .append(node.getChildren().getFirst().getType())
                .append(".")
                .append(protocolClass)
                .append("(");
        visit((ArgumentsNode) node.getChildren().getLast());//visit the ArgumentsNode
        stringBuilder.append("),");
    }

    /**
     * Visits the receiver of the message.
     * @param node SendMsgNode
     * @throws RuntimeException if the receiver is not an IdentifierNode, KnowsAccessNode or SelfNode
     */
    private void visitReceiver(SendMsgNode node){
        AstNode firstChild=node.getChildren().getFirst();
        if(firstChild instanceof IdentifierNode){
            visit((IdentifierNode) firstChild); //visit the IdentifierNode
        }else if (firstChild instanceof  KnowsAccessNode){
            visit((KnowsAccessNode) firstChild); //visit the KnowsAccessNode
        }else if(firstChild instanceof ArrayAccessNode) {
            visit((ArrayAccessNode) firstChild); //visit the ArrayAccessNode
        }
        else if (node.getReceiver().equals("self")){ //receiver is self
            visit((SelfNode) firstChild);
        } else {
            throw new RuntimeException("Receiver of message is not an IdentifierNode, KnowsAccessNode or selfnode");
        }
    }

    /**
     * Appends a unique ID to the actor.
     * Current implementation is using UUID to generate a random ID for actors.
     * There's no guarantee that these will not be the same, but it's very unlikely.
     */
    private void getNextUniqueActor() {
        stringBuilder.append("UUID.randomUUID().toString()");
    }

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
        stringBuilder.append(")").append(javaE.COMMA.getValue());
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
    public void visit(ExpNode node) { //abstract class
    }

    @Override
    public void visit(StateAccessNode node) {
        if ((node.getParent() instanceof ArgumentsNode) && isArray(node)) {
            if (!(node.getParent().getParent() instanceof SpawnActorNode)) { //the constructor in Akka is only happy with object array. It breaks if we try to cast to the specific array type.
                stringBuilder
                        .append("(")
                        .append(VarTypeConverter(node.getType(), true, false));
                stringBuilder.replace(stringBuilder.length() - 1, stringBuilder.length(), "");//remove surplus space
                stringBuilder
                        .append(") ");
            }
            stringBuilder
                    .append(" cloneArray(")
                    .append(javaE.THIS.getValue())
                    .append(".")
                    .append(node.getAccessIdentifier())
                    .append(")");
        } else {
            stringBuilder
                    .append(javaE.THIS.getValue())
                    .append(".")
                    .append(node.getAccessIdentifier());
        }
    }

    /**
     * Appends the CloneArray method to the target code.
     */
    private void appendCloneArray() {
        localIndent = 1;
        stringBuilder
                .append("private static Object cloneArray(Object array) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("if (array == null) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("return null; \n");
        codeOutput.add(getLine());
        appendBodyClose();
        stringBuilder
                .append("Class<?> arrayClass = array.getClass(); \n")
                .append("if (!arrayClass.isArray()) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("throw new IllegalArgumentException(\"Input is not an array\"); \n");
        codeOutput.add(getLine());
        appendBodyClose();
        stringBuilder
                .append("Class<?> componentType = arrayClass.getComponentType(); \n")
                .append("if (componentType.isArray()) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("// 2D array case or higher dimensions \n")
                .append("int length = Array.getLength(array); \n")
                .append("Object newArray = Array.newInstance(componentType, length); \n")
                .append("for (int i = 0; i < length; i++) { \n");
        codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("Array.set(newArray, i, cloneArray(Array.get(array, i))); \n");
        codeOutput.add(getLine());
        appendBodyClose();
        stringBuilder.append("return newArray; \n");
        codeOutput.add(getLine());
        appendBodyClose();
        stringBuilder
                .append("else { \n");
                codeOutput.add(getLine());
        localIndent++;
        stringBuilder
                .append("// 1D array case \n")
                .append("return ((Object[]) array).clone(); \n");
        codeOutput.add(getLine());

        appendBodyClose();
        appendBodyClose();

    }


    @Override
    public void visit(StringNode node) {
        stringBuilder.append(node.getValue());
    }

    @Override
    public void visit(VarDclNode node) {
        if(node.getParent() instanceof StateNode) {
            stringBuilder.append(javaE.PRIVATE.getValue());
        }
        if(isArray(node) && node.getChildren().get(1) instanceof InitializationNode) {
            visitChild(node.getChildren().get(0));
            visitChild(node.getChildren().get(1));

        } else {
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
        this.symbolTable.enterScope(node.getNodeHash());
        stringBuilder.append(javaE.WHILE.getValue()).append("(");
        visitChild(node.getChildren().get(0));
        stringBuilder.append(")");
        visitChild(node.getChildren().get(1));
        stringBuilder.append("\n");
        codeOutput.add(getLine());
        this.symbolTable.leaveScope();
    }

    @Override
    public void visit(BoolCompareNode node){ //TODO AJ Bør denne ikke udfyldes?
    }

    @Override
    public void visit(KillNode node) {
        stringBuilder
                .append("getContext().stop(getSelf());\n");//The actor kills itself.
                //Because the Reaper watches the actor, it informs the reaper that it is dead. And sends Terminated message to the reaper.
        codeOutput.add(getLine());
    }

    /* ------------------- -------------------
     *              HELPER METHODS
     * ------------------- ------------------- */

    /**
     *
     * @param RaceGuardType The RaceGuard type to be converted
     * @param useActorRef Actor types are converted to ActorRef if true
     * @param removeBrackets Brackets are removed from array types if true.
     * @return The java type to be used in the target code.
     * */
    private String VarTypeConverter(String RaceGuardType, boolean useActorRef, boolean removeBrackets){
        String javaType;
        switch (RaceGuardType) {
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
                if(useActorRef){ //If actor type in RaceGuard should be converted to "ActorRef"
                    String[] substrings=RaceGuardType.split("(?=\\[)");//split at empty string where next string is "[". e.g "myActor[]" is split to "myActor" and "[]"
                    if(substrings.length>1){ //if RaceGuardType is an array (substrings[1] is "[]" or "[][]").
                        String actorRefNoSpace=javaE.ACTORREF.getValue().split(" ")[0];//Delete the space in "ActorRef "
                        if (substrings.length>2) {
                        javaType=actorRefNoSpace+substrings[1]+substrings[2]+" "; //Two dimensional array
                        } else {
                            javaType = actorRefNoSpace + substrings[1] + " ";//The java type is the array-part of paralngType appended to "ActorRef"
                        }
                    }else{
                        javaType=javaE.ACTORREF.getValue();  //If paralangType is not an array, the java type is "ActorRef "
                    }
                }else {
                    javaType=RaceGuardType+" ";
                }
        }
        if(removeBrackets){
            javaType=javaType.replaceAll("\\[", "").replaceAll("\\]","");
        }
        return javaType;
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

    /***
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
     * @param node the BodyNode
     */
    private void appendBodyOpen(AstNode node){
        appendBodyOpen(node,"","");
    }

    /***
     * Allows for appending given strings before and after visiting each child-node of the body
     * @param node the BodyNode
     * @param before string appended before each child
     * @param after string appended after each child
     */
    private void appendBodyOpen(AstNode node,String before,String after){
        stringBuilder.append( " {\n");
        codeOutput.add(getLine() );//gets current line with indentation given by localIndent at this moment, resets stringBuilder, and adds the line to codeOutput.
        localIndent++;
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

    /**
     * Appends package naming to be able to use the classes in the same package.
     * @param packageName The name of the package (e.g. "output")
     */
    private void appendPackage(String packageName){
        stringBuilder.append("package ")
                .append(packageName)
                .append("; \n \n");
    }

    /***
     * appends a simple constructor where the instance fields are set to be equal to the input parameters.
     * @param className
     * @param params The input parameters of the constructor.
     */
    private void appendConstructor(String className,List<IdentifierNode> params){
        stringBuilder
                .append(javaE.PUBLIC.getValue())
                .append(className)
                .append("(");
        if(!params.isEmpty()){//append parameters list
            for(IdentifierNode param:params){
                stringBuilder
                        .append(VarTypeConverter(param.getType(),true,false))
                        .append(param.getName())
                        .append(", ");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);//Remove the surplus ", " from the end of the parameters list
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        stringBuilder.append(") {\n");
        codeOutput.add(getLine());
        localIndent++;
        for(IdentifierNode param:params){//assign values to the instance fields
            stringBuilder
                    .append(javaE.THIS.getValue())
                    .append(".")
                    .append(param.getName())
                    .append(javaE.EQUALS.getValue())
                    .append(param.getName())
                    .append(";\n");
        }
        codeOutput.add(getLine());
        localIndent--;
        stringBuilder.append("}\n");
        codeOutput.add(getLine());
    }

    /***
     *  (e.g. "GreeterScript.Greet")
     * @param node parent actor of the on method
     * @param methodName
     * @return class name of protocol class of an on-method. If the method is in a follows script, this class will be the static protocol class in the Script-class.
     * If methodName is "greet", then "Greet" is returned if greet() is not in a followed script. However, if greet() is in a follows script, e.g. GreeterScript, then "GreeterScript.Greet" is returned.
     */
    private String getClassName(ActorDclNode node,String methodName){
        MethodDclNode methodNode=null;
        String className = methodName;
        for (AstNode childNode: node.getChildren()){//find the on-method's MethodDclNode in the parent actor
            if(childNode instanceof MethodDclNode && ((MethodDclNode) childNode).getId().equals(methodName)){
                methodNode=(MethodDclNode) childNode;
                break;
            }
        }
        if( methodNode!=null){
            String scriptName=getFollowedScriptName(methodNode);//is null if method is not in followed script
            if(scriptName!=null){
                className=scriptName+"."+className;
            }
        }
        return className;
    }

    /***
     * @param node MethodDclNode
     * @return Script name if the method is in a followed script. Else returns null.
     */
    private String getFollowedScriptName(MethodDclNode node){
        //MethodDclNode's parent is always an actor.
        // If the actor follows a script, a FollowsNode is the first child of this actor
        AstNode firstChildOfActor=node.getParent().getChildren().getFirst();
        if(firstChildOfActor instanceof FollowsNode followsNode){
            List<IdentifierNode> followedScripts=(List<IdentifierNode>)(List<?>) followsNode.getChildren();//Casting through intermediate wildcard type in or to be able to cast the list.
            for(IdentifierNode script:followedScripts){ //For each followed script we get all on-methods in it.
                HashMap<String, Attributes> scriptOnMethods=symbolTable.lookUpScope(script.getName()).getDeclaredOnMethods();
                if(scriptOnMethods.containsKey(node.getId())){ //if one of the on-methods match the name of the input MethodDclNode
                    return script.getName();
                }
            }
        }
        return null;
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
                .append(RaceGuardE.ON.getValue())
                .append(methodName)
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

    /**
     * Appends one line comment to the stringBuilder
     * @param strings the strings to append to the comment. Can be multiple strings.
     */
    private void appendInlineComment(String... strings){
        stringBuilder.append(javaE.INLINE_COMMENT.getValue());
        for(String s:strings){
            stringBuilder.append(s);
        }
        stringBuilder.append("\n");
    }
}


