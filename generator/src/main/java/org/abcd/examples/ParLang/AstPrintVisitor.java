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
                case "ArithExpNode":
                    this.print(localIndent, className + " : " + ((ArithExpNode) node).getOpType() + " with type: " + node.getType());
                    break;
                case "UnaryExpNode":
                    if (((UnaryExpNode) node).isNegative()){
                        this.print(localIndent, className + " negated");
                    }else{
                        this.print(localIndent, className);
                    }
                    break;
                case "DoubleNode":
                    this.print(localIndent, className + " : " + ((DoubleNode)node).getValue() + " with type: " + node.getType() );
                    break;
                case "IntegerNode":
                    this.print(localIndent, className + " : " + ((IntegerNode)node).getValue() + " with type: " + node.getType());
                    break;
                case "ParametersNode":
                    this.print(localIndent, className + " : "+  ((ParametersNode)node).getNumberOfIdentifiers());
                    break;
                case "IdentifierNode":
                    this.print(localIndent, className + " " + ((IdentifierNode)node).getName() + " type: " + ((IdentifierNode)node).getType());
                    break;
                case "SpawnActorNode":
                    this.print(localIndent, className + " : " + ((SpawnActorNode)node).getType() + " with type: " + node.getType());
                    break;
                case "StringNode":
                    this.print(localIndent, className + " : " + ((StringNode)node).getValue() + " with type: " + node.getType());
                    break;
                case "ActorDclNode":
                    this.print(localIndent, className + " id: " + ((ActorDclNode)node).getId() + " with type: " + node.getType());
                    break;
                case "MethodDclNode":
                    if (((MethodDclNode)node).getMethodType().equals(parLangE.ON.getValue())){
                        this.print(localIndent, className + " " + ((MethodDclNode)node).getMethodType() + " id: " + ((MethodDclNode)node).getId());
                    }
                    else {
                        this.print(localIndent, className + " " + ((MethodDclNode) node).getMethodType() + " id: " + ((MethodDclNode) node).getId() + " returns type: " + ((MethodDclNode) node).getType());
                    }
                    break;
                case "ScriptMethodNode":
                    if (((ScriptMethodNode)node).getMethodType().equals(parLangE.ON.getValue())) {
                        this.print(localIndent, className + " " + ((ScriptMethodNode) node).getMethodType() + " with id: " + ((ScriptMethodNode) node).getId());
                    }else{
                        this.print(localIndent, className + " " + ((ScriptMethodNode) node).getMethodType() + " with id: " + ((ScriptMethodNode) node).getId() + " return type: " + ((ScriptMethodNode) node).getType());
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
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " with type: " + ((AccessNode)node).getType() );
                    break;
                case "ActorAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " with type: " + ((AccessNode)node).getType() );
                    break;
                case "StateAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " with type: " + ((AccessNode)node).getType() );
                    break;
                case "KnowsAccessNode":
                    this.print( localIndent, className + " access id " + ((AccessNode)node).getAccessIdentifier() + " with type: " + ((AccessNode)node).getType() );
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
                    this.print(localIndent, className + " with type" + node.getType());
                    break;
                case "BoolAndExpNode":
                    this.print(localIndent, className + " with type" + node.getType());
                    break;
                case "CompareExpNode":
                    this.print(localIndent, className + " with operator: " + ((CompareExpNode)node).getOperator() + " with type: " + node.getType());
                    break;
                case "NegatedBoolNode":
                    this.print(localIndent, className);
                    break;
                case "BoolNode":
                    this.print(localIndent, className + " with value: " + ((BoolNode)node).getValue());
                    break;
                case "VarDclNode":
                    this.print(localIndent, className + " id: " + ((VarDclNode)node).getId() + " with type: " + node.getType());
                    break;
                case "InitializationNode":
                    this.print(localIndent, className + " with type: " + node.getType());
                    break;
                case "ListNode":
                    this.print(localIndent, className + " with type: " + node.getType());
                    break;
                case "ReturnStatementNode":
                    this.print(localIndent, className + " with type: " + node.getType());
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
