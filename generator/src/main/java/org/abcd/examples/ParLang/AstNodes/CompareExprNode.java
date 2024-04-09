package org.abcd.examples.ParLang.AstNodes;

public class CompareExprNode extends AstNode {
    private AstNode leftOperand;
    private AstNode rightOperand;
    private String operator;

    public CompareExprNode(String operator, AstNode leftOperand, AstNode rightOperand) {
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

    public AstNode getLeftOperand() {
        return leftOperand;
    }

    public AstNode getRightOperand() {
        return rightOperand;
    }

    public String getOperator() {
        return operator;
    }


    // Additional methods for pretty printing, visitor pattern, etc. can be added here
}

