package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AST.ArithExpression;
import org.abcd.examples.ParLang.AST.AstNode;
import org.abcd.examples.ParLang.AST.DoubleNode;
import org.abcd.examples.ParLang.AST.IntegerNode;

public class AstPrintVisitor {
    public void visit(int localIndent, AstNode node){
        if(node != null){
            switch (node.getClass().toString()){
                case "class org.abcd.examples.ParLang.AST.ArithExpression":
                    this.print(localIndent, node.getClass() + " of type: " + ((ArithExpression) node).getType());
                    break;
                case "class org.abcd.examples.ParLang.AST.DoubleNode":
                    this.print(localIndent, node.getClass() + " with the value: " + ((DoubleNode)node).getValue());
                    break;
                case "class org.abcd.examples.ParLang.AST.IntegerNode":
                    this.print(localIndent, node.getClass() + " with the value: " + ((IntegerNode)node).getValue());
                    break;
                default:
                    this.print(localIndent, node.getClass().toString());
                    break;
            }

            for (AstNode childNode : node.getChildren()){
                this.visit(localIndent + 1, childNode);
            }
        }else{
            System.out.println("A node is null");
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
