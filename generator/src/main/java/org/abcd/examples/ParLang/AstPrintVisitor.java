package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

public class AstPrintVisitor {
    public void visit(int localIndent, AstNode node){
        if(node != null){
            switch (node.getClass().toString()){
                case "class org.abcd.examples.ParLang.AstNodes.ArithExpression":
                    this.print(localIndent, node.getClass() + " of type: " + ((ArithExprNode) node).getOpType());
                    break;
                case "class org.abcd.examples.ParLang.AstNodes.DoubleNode":
                    this.print(localIndent, node.getClass() + " with the value: " + ((DoubleNode)node).getValue());
                    break;
                case "class org.abcd.examples.ParLang.AstNodes.IntegerNode":
                    this.print(localIndent, node.getClass() + " with the value: " + ((IntegerNode)node).getValue());
                    break;
                case "class org.abcd.examples.ParLang.AstNodes.ParametersNode":
                    this.print(localIndent, node.getClass() + " with "+  ((ParametersNode)node).getNumberOfIdentifiers() + " identifiers");
                    break;
                case "class org.abcd.examples.ParLang.AstNodes.IdentifierNode":
                    this.print(localIndent, node.getClass() + " with " + ((IdentifierNode)node).getType().toString() + " identifier: " + ((IdentifierNode)node).getName());
                    break;
                case "class org.abcd.examples.ParLang.AstNodes.StringNode":
                    this.print(localIndent, node.getClass() + "with the value: " + ((StringNode)node).getValue());
                    break;
                default:
                    this.print(localIndent, node.getClass().toString());
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
