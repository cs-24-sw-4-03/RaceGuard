package org.abcd.examples.ParLang.AstNodes;

public class CompareExpNode extends AstNode {
    private final String operator;

    public CompareExpNode(String operator, AstNode leftOperand, AstNode rightOperand) {
        this.operator = operator;
        this.addChild(leftOperand);
        this.addChild(rightOperand);
    }
    public String getOperator() {
        return operator;
    }

}

