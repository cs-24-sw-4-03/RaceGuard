package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

public class AstPrintVisitor {
    public static final String ANSI_RED = "\u001B[31m"; //include in string to start printing red text to the terminal
    public static final String ANSI_RESET = "\u001B[0m"; //include in string to stop printing in special color.
    /***
     *
     * @param localIndent is the number of "\t" before information about the AstNode is printed. Shows the depth of the AstNode in the AST.
     * @param node is an AstNode in the AST.
     * @param printParentField is a string given to main as argument. If it is "parents", then information about the AstNode assigned to node's parent field is printed.
     */
    public void visit(int localIndent, AstNode node, String printParentField){

        if(node != null){
            String className=node.getClass().getSimpleName();

            if(printParentField.equals("parents")){
                className+=" "+ANSI_RED+node.getNodeHash()+ANSI_RESET;// print hashcode for each node in red in order to compare with hash code of the AstNode-object in the parent fields of the children.
                if(node.getParent()!=null){//only attempt to getClass if parent is not null (parent should only be null for the InitNode)
                    AstNode parent=node.getParent();//get the AstNode stored in the parent field of node.
                    className+=" (parent: "+parent.getClass().getSimpleName()+" "+parent.getNodeHash()+") ";//concatanate parent's className and hashCode to the string printed for node
                }
            }

            switch (className){
                case "ArithExprNode":
                    this.print(localIndent, className + " : " + ((ArithExprNode) node).getOpType());
                    break;
                case "UnaryExpNode":
                    if (((UnaryExpNode) node).isNegative()){
                        this.print(localIndent, className + " negated");
                    }else{
                        this.print(localIndent, className);
                    }
                    break;
                case "DoubleNode":
                    this.print(localIndent, className + " : " + ((DoubleNode)node).getValue());
                    break;
                case "IntegerNode":
                    this.print(localIndent, className + " : " + ((IntegerNode)node).getValue());
                    break;
                case "ParametersNode":
                    this.print(localIndent, className + " : "+  ((ParametersNode)node).getNumberOfIdentifiers());
                    break;
                case "IdentifierNode":
                    if(((IdentifierNode)node).getType()!=null){
                        this.print(localIndent, className + " type: " + ((IdentifierNode)node).getType().toString() + " id: " + ((IdentifierNode)node).getName());
                    }else{
                        this.print(localIndent, className + ": " + ((IdentifierNode)node).getName());
                    }
                    break;
                case "ActorIdentifierNode":
                    this.print(localIndent, className + " type: " + ((ActorIdentifierNode)node).getActorType()+ " id: " + ((ActorIdentifierNode)node).getName());
                    break;
                case "SpawnActorNode":
                    this.print(localIndent, className + " : " + ((SpawnActorNode)node).getActorType());
                    break;
                case "StringNode":
                    this.print(localIndent, className + " : " + ((StringNode)node).getValue());
                    break;
                case "ActorDclNode":
                    this.print(localIndent, className + " id: " + ((ActorDclNode)node).getId());
                    break;
                case "MethodDclNode":
                    this.print(localIndent, className + " " +((MethodDclNode)node).getMethodType()+ " id: " + ((MethodDclNode)node).getId()+ " return type: "+((MethodDclNode)node).getReturnType());
                    break;
                case "ScriptMethodNode":
                    if (((ScriptMethodNode)node).getMethodType().equals("on")) {
                        this.print(localIndent, className + " " + ((ScriptMethodNode) node).getMethodType() + " with id: " + ((ScriptMethodNode) node).getId());
                    }else{
                        this.print(localIndent, className + " " + ((ScriptMethodNode) node).getMethodType() + " with id: " + ((ScriptMethodNode) node).getId() + " return type: " + ((ScriptMethodNode) node).getReturnType());
                    }
                    break;
                case "ScriptDclNode":
                    this.print(localIndent, className + " " +((ScriptDclNode)node).getId());
                    break;
                case "WhileNode":
                    this.print(localIndent, className );
                    break;
                case "ForNode":
                    this.print(localIndent, className);
                    break;
                case "ArrayAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " of type: " + ((AccessNode)node).getAccessType() );
                    break;
                case "ActorAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " of type: " + ((AccessNode)node).getAccessType() );
                    break;
                case "StateAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " of type: " + ((AccessNode)node).getAccessType() );
                    break;
                case "KnowsAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " of type: " + ((AccessNode)node).getAccessType() );
                    break;
                case "ArgumentsNode":
                    if (((ArgumentsNode)node).getChildren().size() > 0){
                        this.print(localIndent, className);
                    }
                    break;
                case "SendMsgNode":
                    this.print( localIndent, className + " " + ((SendMsgNode)node).getMsgName() + " to: " + ((SendMsgNode)node).getReceiver() );
                    break;
                case "BoolExprNode":
                    this.print(localIndent, className);
                    break;
                case "BoolAndExpNode":
                    this.print(localIndent, className);
                    break;
                case "CompareExpNode":
                    this.print(localIndent, className + " with operator: " + ((CompareExpNode)node).getOperator());
                    break;
                case "NegatedBoolNode":
                    this.print(localIndent, className);
                    break;
                case "BoolNode":
                    this.print(localIndent, className + " with value: " + ((BoolNode)node).getValue());
                    break;
                case "BodyNode":
                    this.print(localIndent, className);
                    break;
                default:
                    this.print(localIndent, className);
                    break;
            }

            for (AstNode childNode : node.getChildren()){
                this.visit(localIndent + 1, childNode, printParentField);
            }
        }else{
            System.out.println("A null-node");
        }
    }

    private void print(int indent, String string){
        StringBuilder output = new StringBuilder();
        output.append("\t".repeat(Math.max(0, indent)));

        if(string != null){
            output = new StringBuilder(output.toString().concat(string));
        } else{
            output = new StringBuilder(output.toString().concat("null"));
        }
        System.out.println(output);
    }
}
