package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

public class AstPrintVisitor {
    public void visit(int localIndent, AstNode node){
        if(node != null){
            String className=node.getClass().getSimpleName();
            switch (className){
                case "ArithExprNode":
                    this.print(localIndent, className + " of type: " + ((ArithExprNode) node).getOpType());
                    break;
                case "DoubleNode":
                    this.print(localIndent, className + " with the value: " + ((DoubleNode)node).getValue());
                    break;
                case "IntegerNode":
                    this.print(localIndent, className + " with the value: " + ((IntegerNode)node).getValue());
                    break;
                case "ParametersNode":
                    this.print(localIndent, className + " with "+  ((ParametersNode)node).getNumberOfIdentifiers() + " identifier(s)");
                    break;
                case "IdentifierNode":
                    if(((IdentifierNode)node).getType()!=null){
                        this.print(localIndent, className + " with type: " + ((IdentifierNode)node).getType().toString() + " and identifier: " + ((IdentifierNode)node).getName());
                    }else{
                        this.print(localIndent, className + " with identifier: " + ((IdentifierNode)node).getName());
                    }
                    break;
                case "ActorIdentifierNode":
                    this.print(localIndent, className + " with type: " + ((ActorIdentifierNode)node).getActorType()+ " and identifier: " + ((ActorIdentifierNode)node).getName());
                    break;
                case "StringNode":
                    this.print(localIndent, className + " with the value: " + ((StringNode)node).getValue());
                    break;
                case "ActorDclNode":
                    this.print(localIndent, className + " with the id: " + ((ActorDclNode)node).getId());
                    break;

                case "MethodDclNode":
                    this.print(localIndent, className + " with id: " + ((MethodDclNode)node).getId()+ ", method type: "+ ((MethodDclNode)node).getMethodType()+" and return type: "+((MethodDclNode)node).getReturnType());
                    break;
                case "WhileNode":
                    this.print(localIndent, className + " with the value: " );
                    break;
                case "ForNode":
                    this.print(localIndent, className + " with the value: " );
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
