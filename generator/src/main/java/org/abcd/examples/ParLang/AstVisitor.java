package org.abcd.examples.ParLang;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.tree.ParseTree;
import org.abcd.examples.ParLang.AstNodes.*;
import  org.abcd.examples.ParLang.Exceptions.*;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AstVisitor extends ParLangBaseVisitor<AstNode> {
    //Holds all recognized Types, Is extended when actors or Scripts are declared. see visitActor.
    private TypeContainer typeContainer;

    public AstVisitor(TypeContainer typeContainer) {
        super();
        this.typeContainer = typeContainer;
    }
    @Override public AstNode visitInit(ParLangParser.InitContext ctx) {
        //Init is the root of the AST
        InitNode initNode=new InitNode();
        //visit all children of the init node, which can be MainFunc, Actor, or Script
        return childVisitor(initNode,ctx.children);
    }

    private AstNode childVisitor(AstNode node, List<ParseTree> children){
        //visit all children of a node and add them to the node
        for(ParseTree c:children){
            if(c instanceof TerminalNode){
                continue; //skip if child is a terminal node
            }

            node.addChild(visit(c)); //visit the child and add it to the node
        }
        return node; //return the node with all children added
    }

    @Override public AstNode visitMainFunc(ParLangParser.MainFuncContext ctx) {
        //Main function is the entry point of the program
        MainDclNode main= new MainDclNode(ctx.MAIN().getText());
        if(!ctx.parameters().getText().equals("()")){ //If there are parameters
            main.addChild(visit(ctx.parameters())); //add parameters as children to the main node
        }
        if(ctx.body()!=null){ //If there is a body
            main.addChild(visit(ctx.body())); //visit the body and add it as a child to the main node
        }
        return main;
    }

    @Override public AstNode visitMethodCall(ParLangParser.MethodCallContext ctx) {
        MethodCallNode methodCallNode = new MethodCallNode(ctx.identifier().getText());
        if(ctx.arguments()!=null){ //If there are arguments
            //visit the arguments and add them as children to the methodCallNode
            return childVisitor(methodCallNode,ctx.children);
        }
            return methodCallNode; //return the methodCallNode if no arguments
    }

    @Override public AstNode visitSendMsg(ParLangParser.SendMsgContext ctx) {
        //SendMsg has the structure: [IDENTIFIER,SEND_MSG,IDENTIFIER,LPAREN,ARGUMENTS,RPAREN]
        SendMsgNode sendMsgNode = new SendMsgNode(ctx.getChild(0).getText(), ctx.getChild(2).getText());
        sendMsgNode.addChild(visit(ctx.arguments())); //add arguments as children
        return sendMsgNode;
    }

    @Override public AstNode visitParameters(ParLangParser.ParametersContext ctx){
        int numOfChildren=ctx.getChildCount();
        ParametersNode params = new ParametersNode();
        if (numOfChildren != 2){ //there are minimum 2 children, the parentheses
            //If there are more than 2 children, there are parameters
            for (int i = 1; i < numOfChildren; i+=3){ //skip the commas
                params.addChild(new IdentifierNode(ctx.getChild(i+1).getText(), ctx.getChild(i).getText()));
            } //add the parameters as children to the parametersNode
        }
        return params; //return the parametersNode with all parameters added as children
    }
    @Override public AstNode visitArguments(ParLangParser.ArgumentsContext ctx){
        ArgumentsNode args = new ArgumentsNode();
        //visit all children of the arguments node and add them as children to the argumentsNode
        return childVisitor(args,ctx.children); //return the argumentsNode with all arguments added as children
    }

    @Override public AstNode visitScript(ParLangParser.ScriptContext ctx) {
        try {
            String scriptName = ctx.identifier().getText();
            ScriptDclNode node = new ScriptDclNode(scriptName);
            if (typeContainer.hasType(scriptName)) {//if another actor is declared with the same name we may have conflicting types.
                throw new DuplicateScriptTypeException("Actor with name " + scriptName + " already defined");
            } else {//extend the typeContainer list with new types
                typeContainer.addType(scriptName);
            }
            List<ParseTree> children = new ArrayList<ParseTree>(ctx.children);
            children.remove(1);//remove identifier from list of children
            return childVisitor(node, children);
        } catch (DuplicateScriptTypeException e) {
            System.out.println(e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override public AstNode visitScriptMethod(ParLangParser.ScriptMethodContext ctx) {
        String methodType = ctx.getChild(0).getText(); //methodType is either "on" or "local"
        String returnType; //returnType is either "void" or the type of the method
        switch (methodType) {
            case "on": //on-methods always return void
                returnType = "void";
                break;
            case "local": //local-methods can return any type
                returnType = ctx.allTypes().getText();
                break;
            default: //should never happen
                returnType = null;
        }
        ScriptMethodNode node = new ScriptMethodNode(ctx.identifier().getText(), returnType, methodType);
        if(!ctx.parameters().getText().equals("()")){ //If there are parameters
            node.addChild(visit(ctx.parameters())); //add parameters as children
        }
        return node; //return the node
    }

    @Override public AstNode visitActor(ParLangParser.ActorContext ctx) {
        try {
            String actorName = ctx.identifier().getText(); //get the name of the actorType
            if (typeContainer.hasType(actorName)) {
                //if another actor is declared with the same name we have conflicting types.
                throw new DuplicateActorTypeException("Actor with name " + actorName + " already defined");
            } else {//extend the typeContainer list with new types
                typeContainer.addType(actorName); //add the actorType to the typeContainer
            }
            ActorDclNode node = new ActorDclNode(ctx.identifier().getText());
            List<ParseTree> children = new ArrayList<ParseTree>(ctx.children);
            children.remove(1);//remove identifier from list of children
            //visit all children of the actor node and add them as children to the actorNode
            return childVisitor(node, children);
        }catch (DuplicateActorTypeException e){ //if error just return null to continue visiting
            System.out.println(e.getMessage());
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override public AstNode visitFollow(ParLangParser.FollowContext ctx) {
        FollowsNode followNode = new FollowsNode();
        List<ParseTree> children = new ArrayList<ParseTree>(ctx.children);
        children.remove(0);//remove "follows" from list of children
        return childVisitor(followNode,children); //visit all children of the follow node and add them as children to the followNode
    }

    @Override public AstNode visitActorState(ParLangParser.ActorStateContext ctx) {
        StateNode node= new StateNode(ctx.STATE().getText());
        //visit all children of the actorState node and add them as children to the actorStateNode
        return childVisitor(node,ctx.children);
    }

    @Override public AstNode visitActorKnows(ParLangParser.ActorKnowsContext ctx) {
        int numOfChildren=ctx.getChildCount();
        for (ParseTree c:ctx.children){
            System.out.println(c.getText());
        }
        KnowsNode knowsNode= new KnowsNode(ctx.KNOWS().getText());
        if (numOfChildren != 3){ //there are minimum 3 children, the parentheses and "knows" token
            //If there are more than 3 children, there are known actors
            for (int i = 2; i < numOfChildren-1; i+=3){ //skip the semicolons
                knowsNode.addChild(new IdentifierNode(ctx.getChild(i+1).getText(), ctx.getChild(i).getText()));
            } //add the known actors as children to the knowsNode
        }
        return knowsNode; //return the knowsNode with all known actors added as children
    }

    @Override public AstNode visitSpawn(ParLangParser.SpawnContext ctx) {
        SpawnDclNode node= new SpawnDclNode();
        if(!ctx.parameters().getText().equals("()")){//If there are parameters
            node.addChild(visit(ctx.parameters())); //visit and add parameters as children to the spawnNode
        }
        if(ctx.body()!=null){//If there is a body
            node.addChild(visit(ctx.body()));//visit the body and add it as a child to the spawnNode
        }
        return node;//return the spawnNode with parameters and body added as children
    }

    @Override public AstNode visitSpawnActor(ParLangParser.SpawnActorContext ctx) {
        SpawnActorNode spawnNode = new SpawnActorNode(ctx.identifier().getText());
        if(ctx.arguments() != null){//If there are arguments
            spawnNode.addChild(visit(ctx.arguments())); //visit and add arguments as children to the spawnNode
        }
        return spawnNode;//return the spawnNode with arguments added as children
    }

    @Override public AstNode visitStateAccess(ParLangParser.StateAccessContext ctx) {
        //We can access Sate within an Actor; Structure:[STATE,DOT,IDENTIFIER]
        //Need to know: Identifier of what we want to access and the type of the value the identifier points to
        String accessType = "EMPTY"; //Until type-checker is implemented
        if (ctx.IDENTIFIER() != null) { //If the access is a simple identifier
            return new StateAccessNode(accessType, ctx.IDENTIFIER().getText()); //return a StateAccessNode with the accessType and accessIdentifier
        } //If the access is an array access

        AstNode child = visit(ctx.getChild(2)); //visit the array access
        StateAccessNode node = new StateAccessNode(accessType, ((ArrayAccessNode)child).getAccessIdentifier());
        node.addChild(child); //add the array access as a child
        return node;
    }

    @Override public AstNode visitKnowsAccess(ParLangParser.KnowsAccessContext ctx){
        //We can access Knows within an Actor; Structure:[KNOWS,DOT,IDENTIFIER];
        String accessIdentifier = ctx.IDENTIFIER().getText();
        String accessType = "EMPTY"; //Until type-checker is implemented
        return new KnowsAccessNode(accessType,accessIdentifier);//return a KnowsAccessNode with the accessIdentifier and accessType
    }

    @Override public AstNode visitOnMethod(ParLangParser.OnMethodContext ctx) {
        MethodDclNode node= new MethodDclNode(ctx.identifier().getText(),"void",ctx.ON_METHOD().getText());
        if (ctx.parameters() != null) {//If there are parameters
            node.addChild(visit(ctx.parameters())); //visit and add parameters as children to the methodNode
        }
        if(ctx.body()!=null){ //If there is a body
            node.addChild(visit(ctx.body())); //visit the body and add it as a child to the methodNode
        }
        return node; //return the methodNode with parameters and body added as children
    }

    @Override public AstNode visitLocalMethod(ParLangParser.LocalMethodContext ctx) {
        MethodDclNode node= new MethodDclNode(ctx.identifier().getText(),ctx.allTypes().getText(),ctx.LOCAL_METHOD().getText());
        if (ctx.parameters() != null) { //If there are parameters
            node.addChild(visit(ctx.parameters())); //visit and add parameters as children to the methodNode
        }
        if(ctx.localMethodBody()!=null){ //If there is a body
            node.addChild(visit(ctx.localMethodBody())); //visit the body and add it as a child to the methodNode
        }
        return node; //return the methodNode with parameters and body added as children
    }

    @Override public AstNode visitBody(ParLangParser.BodyContext ctx) {
        BodyNode bodyNode =new BodyNode();
        return childVisitor(bodyNode,ctx.children); //visit all children of the body node and add them as children to the bodyNode
    }

    @Override public AstNode visitPrintCall(ParLangParser.PrintCallContext ctx) {
        //PrintCall has the structure: [PRINT,LPAREN,PRINT_BODY,RPAREN]
        PrintCallNode printCallNode = new PrintCallNode();
        if(ctx.printBody()!=null){ //If there is a printBody
            printCallHelper(printCallNode,ctx.printBody()); //visit the printBody and add it as a child to the printCallNode
        }
        return printCallNode; //return the printCallNode with the printBody added as a child
    }

    private AstNode printCallHelper(AstNode parent, ParLangParser.PrintBodyContext ctx){
        //helper method to visit all children of the printBody node and add them as children to the parent
        int childCount=ctx.getChildCount();
        for (int i = 0; i < childCount; i+=2){
            if (ctx.getChild(i).getText().contains("\"")) {
                parent.addChild(new StringNode(ctx.getChild(i).getText()));
            } else {
                parent.addChild(visit(ctx.getChild(i)));
            }
        }
        return parent; //return the parent
    }

    @Override
    public AstNode visitDeclaration(ParLangParser.DeclarationContext ctx) {
        VarDclNode dclNode=new VarDclNode(ctx.identifier().getText(),ctx.allTypes().getText()); //ctx.allTypes().getText() is e.g. "int[]" if int[] a={2,2} is visited
        IdentifierNode idNode=new IdentifierNode(ctx.identifier().getText(),ctx.allTypes().getText());//ctx.allTypes().getText() is e.g. "int[]" if int[] a={2,2} is visited
        dclNode.addChild(idNode); //add identifier as child
        ParLangParser.InitializationContext init=ctx.initialization(); //get the initialization value
        if(init!=null){//variable is initialized
            InitializationNode initializationNode=new InitializationNode();
            initializationNode.addChild(visit(init.getChild(1)));//child with index 1 is the initialization value (value can also be a list).
            dclNode.addChild(initializationNode); //add initializationNode as child
        }
        return dclNode; //return the dclNode with identifier and initialization added as children
    }

    @Override
    public AstNode visitList(ParLangParser.ListContext ctx){
        return childVisitor(new ListNode(),ctx.children);//return a listNode with the list elements as children.
    }

    @Override
    public AstNode visitAssignment(ParLangParser.AssignmentContext ctx) {
        AssignNode assignNode = new AssignNode();

        AstNode varNode=visit(ctx.getChild(0)); //visit the variable
        AstNode valueNode=visit(ctx.getChild(2)); //visit the value

        assignNode.addChild(varNode); //add the variable as child
        assignNode.addChild(valueNode); //add the value as child

        return assignNode; //return the assignNode with variable and value added as children
    }

    @Override
    public AstNode visitIdentifier(ParLangParser.IdentifierContext ctx) {
        AstNode IdNode = null; //initialize the IdNode
        if(ctx.IDENTIFIER() != null){ //If the identifier is a simple identifier
            IdNode = new IdentifierNode(ctx.IDENTIFIER().getText());
        }
        if(ctx.actorAccess() != null){ //If the identifier is an actorAccess
            IdNode = visit(ctx.actorAccess());
        }
        return IdNode; //return the IdNode
    }

    @Override public AstNode visitStatement(ParLangParser.StatementContext ctx) {
        return visit(ctx.getChild(0));//if statement has more than one child, the second one is ";". We just visit the  child always.
    }

    @Override public AstNode visitArithExp(ParLangParser.ArithExpContext ctx) {
        if(ctx.getChildCount()==1){ //If there is only one child,
            return visit(ctx.term(0)); //visit the term
        }else{ //If there are more than one child
            //helper method to visit all children of the arithExp node
            return visitArithExpChild(ctx.getChild(1),ctx,1);
        }
    }

    private AstNode visitArithExpChild(ParseTree child, ParLangParser.ArithExpContext parent, int operatorIndex){
        int termIndex=(operatorIndex-1)/2; //index of first term in a list of just the terms (not including operators).
        int nextOperator=operatorIndex+2; //index of next operator in the list of children

        ArithExpNode.OpType operator=getArithmeticBinaryOperator(child.getText()); //get the operatorType
        AstNode leftChild= visit(parent.term(termIndex)); //visit the left child (term)
        AstNode rightChild; //initialize the right child

        if(parent.getChild(nextOperator)!= null){ //Are there more operators in the tree?
            //If there are more operators, visit the next operator and add it as right child
            rightChild=visitArithExpChild(parent.getChild(nextOperator),parent,nextOperator);
        }else { //If there are no more operators
            rightChild=visit(parent.term(termIndex+1)); //visit the right child (term)
        }
        return new ArithExpNode(operator,leftChild,rightChild); //return a new ArithExprNode with operator, leftChild, and rightChild
    }

    @Override public AstNode visitTerm(ParLangParser.TermContext ctx) {
        int childCount= ctx.getChildCount();
        if(childCount==1){ //If there is only one child
            return visit(ctx.factor(0)); //visit the factor
        }else{ //If there are more than one child
            //helper method to visit all children of the term node
            return visitTermChild(ctx.getChild(1),ctx,1);
        }
    }

    private AstNode visitTermChild(ParseTree child, ParLangParser.TermContext parent, int operatorIndex){
        int factorIndex=(operatorIndex-1)/2; //index of first factor in a list of just the factors
        int nextOperator=operatorIndex+2; //index of next operator in the list of children

        ArithExpNode.OpType operator=getArithmeticBinaryOperator(child.getText()); //get the operatorType
        AstNode leftChild=visit(parent.factor(factorIndex)); //add left child (factor)
        AstNode rightChild; //initialize right child

        if(parent.getChild(nextOperator)!=null){ //Are there more operators?
            rightChild=visitTermChild( parent.getChild(nextOperator),parent,nextOperator); //add right child (operator)
        }else { //If there are no more operators
            rightChild=visit(parent.factor(factorIndex+1)); //add right child (factor)
        }
        return new ArithExpNode(operator,leftChild,rightChild); //return a new ArithExprNode with operator, leftChild, and rightChild
    }

    @Override public AstNode visitFactor(ParLangParser.FactorContext ctx) {
        if (ctx.getChild(0).getText().equals("(")) {
            return visit(ctx.arithExp());//If first child is a parentheses, treat the node as arithmetic expression
        }
        return visit(ctx.getChild(0)); //visit the child
    }

    @Override public AstNode visitUnaryExp(ParLangParser.UnaryExpContext ctx) {
        UnaryExpNode unaryExpNode = new UnaryExpNode();
        if(ctx.getChild(0).getText().equals("-")){ //If the first child is a negation token
            unaryExpNode.setIsNegated(true); //set the node as negated
        }
        List<ParseTree> children=new ArrayList<ParseTree>(ctx.children);
        children.remove(0);//remove the negation token
        return childVisitor(unaryExpNode,children); //visit all children of the unaryExp node and add them as children to the unaryExpNode
    }

    @Override public AstNode visitNumber(ParLangParser.NumberContext ctx) {
        if(ctx.getText().contains(".")){ //If the number is a double
            return new DoubleNode(Double.parseDouble(ctx.getText())); //parse the number as a double
        }else { //If the number is an integer
            return new IntegerNode(Integer.parseInt(ctx.getText())); //parse the number as an integer
        }
    }

    @Override public AstNode visitValue(ParLangParser.ValueContext ctx){
        if(ctx.SELF()!=null){//If ValueContext has the terminal node SELF as a chile
            return new SelfNode(); //return a SelfNode
        }else{//else child is a non-terminal
            return visitChildren(ctx);// visit the non-terminal child
        }
    }

    private static ArithExpNode.OpType getArithmeticBinaryOperator(String operator) {
        try {
            switch (operator) { //return the operatorType based on the operator
                case "+":
                    return ArithExpNode.OpType.PLUS;
                case "-":
                    return ArithExpNode.OpType.MINUS;
                case "*":
                    return ArithExpNode.OpType.MULTIPLY;
                case "/":
                    return ArithExpNode.OpType.DIVIDE;
                case "%":
                    return ArithExpNode.OpType.MODULO;
                default: //If the operator is not recognized
                    throw new UnsupportedOperationException("Unsupported operator: " + operator);
            }
        } catch (UnsupportedOperationException e) { //if exception return unknown to continue visiting
            System.out.println(e.getMessage());
            return ArithExpNode.OpType.UNKNOWN;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ArithExpNode.OpType.UNKNOWN;
        }
    }

    @Override public AstNode visitWhileLoop(ParLangParser.WhileLoopContext ctx) {
        WhileNode whileNode=new WhileNode();
        return childVisitor(whileNode,ctx.children); //visit all children of the whileLoop node and add them as children to the whileNode
    }

    @Override public AstNode visitForLoop(ParLangParser.ForLoopContext ctx) {
        ForNode forNode=new ForNode();
        return childVisitor(forNode,ctx.children); //visit all children of the forLoop node and add them as children to the forNode
    }
    @Override public AstNode visitPrimitive(ParLangParser.PrimitiveContext ctx){
        //Primitives can be: INT, DOUBLE, STRING, and BOOL
        //In case the primitive is a STRING
        if(ctx.STRING() != null) {
            return new StringNode(ctx.getText());
        }
        return visitChildren(ctx); //visit all children of the primitive node
    }
    @Override public AstNode visitBoolExp(ParLangParser.BoolExpContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount == 1) { // If there is only one child
            return visit(ctx.getChild(0)); // Visit the child
        }
        BoolExpNode boolExpNode = new BoolExpNode();
        for (int i = 0; i < childCount; i += 2) { // Visit all children skipping the operators
            boolExpNode.addChild(visit(ctx.getChild(i))); // Add the child as a child to the boolExprNode
        }
        return boolExpNode; // Return the boolExprNode
    }

    @Override public AstNode visitBoolAndExp(ParLangParser.BoolAndExpContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount == 1) { // If there is only one child
            return visit(ctx.getChild(0)); // Visit the child
        }
        BoolAndExpNode boolAndExpNode = new BoolAndExpNode();
        for (int i = 0; i < childCount; i += 2) { // Visit all children skipping the operators
            boolAndExpNode.addChild(visit(ctx.getChild(i))); // Add the child as a child to the boolAndExpNode
        }
        return boolAndExpNode; // Return the boolAndExpNode
    }

    @Override
    public AstNode visitBoolTerm(ParLangParser.BoolTermContext ctx) {
        if (ctx.negatedBool() != null) { // !
            return visit(ctx.negatedBool()); // Visit the negated boolean
        }
        if (ctx.compareExp() != null) { // <, >, <=, >=, !=, ==
            return visit(ctx.compareExp()); // Visit the comparison expression
        }
        if (ctx.PARAN_OPEN() != null) { // Visit the nested expression
            return visit(ctx.boolExp());
        }
        if (ctx.boolLiteral() != null) { // TRUE or FALSE
            return visit(ctx.boolLiteral()); // Visit the boolean literal
        }
        throw new RuntimeException("Unrecognized BoolTerm"); // If the boolean term is not recognized
    }

    @Override
    public AstNode visitNegatedBool(ParLangParser.NegatedBoolContext ctx) {
        NegatedBoolNode boolNode = new NegatedBoolNode();
        if (ctx.getChild(1).getText().equals("(")) { // !(boolExp)
            boolNode.addChild(visit(ctx.getChild(2))); // Visit the boolean expression skipping the parentheses
        } else { // !(boolTerm)
            boolNode.addChild(visit(ctx.getChild(1))); // Visit the boolean term
        }
        return boolNode; // Return the negated boolean node with the child added
    }

    @Override
    public AstNode visitBoolLiteral(ParLangParser.BoolLiteralContext ctx) {
        // contains either 'TRUE' or 'FALSE'
        boolean value = ctx.getText().equals("TRUE"); // Set the value to true if the text is 'TRUE'
        return new BoolNode(value);
    }
    @Override
    public AstNode visitCompareExp(ParLangParser.CompareExpContext ctx) {
        // Visit the left-hand side of the comparison
        AstNode leftOperand = visit(ctx.arithExp(0));

        // Visit the right-hand side of the comparison
        AstNode rightOperand = visit(ctx.arithExp(1));

        // Extract the comparison operator as a string
        String operator = ctx.compareOperator().getText();

        // Create a new node representing the comparison operation
        return new CompareExpNode(operator, leftOperand, rightOperand);
    }
    @Override public AstNode visitArrayAccess(ParLangParser.ArrayAccessContext ctx){
        //An array access
        String accessIdentifier = ctx.identifier().getText();
        return new ArrayAccessNode("", accessIdentifier);
    }
    @Override public AstNode visitLocalMethodBody(ParLangParser.LocalMethodBodyContext ctx){
        LocalMethodBodyNode methodBodyNode = new LocalMethodBodyNode();
        return childVisitor(methodBodyNode,ctx.children); //visit all children of the localMethodBody node and add them as children to the methodBodyNode
    }

    @Override public AstNode visitReturnStatement(ParLangParser.ReturnStatementContext ctx){
        ReturnStatementNode returnStatementNode = new ReturnStatementNode();
        if(ctx.returnType() != null){ //If there is a return value
            returnStatementNode.addChild(visit(ctx.returnType()));
        }
        return returnStatementNode; //return the returnStatementNode with the return value added as a child
    }

    @Override
    public AstNode visitSelection(ParLangParser.SelectionContext ctx) {
        SelectionNode selectionNode = new SelectionNode();
        childVisitor(selectionNode, ctx.children); //visit all children of the selection node and add them as children to the selectionNode
        return selectionNode; //return the selectionNode
    }
}