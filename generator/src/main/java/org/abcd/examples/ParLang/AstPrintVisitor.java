package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

public class AstPrintVisitor {
    public void visit(int localIndent, AstNode node){
        if(node != null){
            String className=node.getClass().getSimpleName();
            if(node.getParent()!=null){
                String parent=node.getParent().getClass().getSimpleName();
                className+=" (parent: "+parent+") ";
            }
            switch (className){
                case "ArithExprNode":
                    this.print(localIndent, className + " : " + ((ArithExprNode) node).getOpType());
                    break;
                case "UnaryExpNode":
                    if (((UnaryExpNode) node).isNegative()){
                        this.print(localIndent, className + " with negation");
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
                    this.print(localIndent, className + " : "+  ((ParametersNode)node).getNumberOfIdentifiers() + " identifier(s)");
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
                    this.print(localIndent, className + ((MethodDclNode)node).getMethodType()+ " method with" + " id: " + ((MethodDclNode)node).getId()+ " and return type: "+((MethodDclNode)node).getReturnType());
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
                default:
                    this.print(localIndent, className);
                    break;
            }

            for (AstNode childNode : node.getChildren()){
                this.visit(localIndent + 1, childNode);
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
