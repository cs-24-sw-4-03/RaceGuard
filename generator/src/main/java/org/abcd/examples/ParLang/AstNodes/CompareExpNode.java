package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class CompareExpNode extends BoolExpNode {
    private final String operator;

    public CompareExpNode(String operator, AstNode leftOperand, AstNode rightOperand) {
        this.operator = operator;
        this.addChild(leftOperand);
        this.addChild(rightOperand);
    }
    public String getOperator() {
        return operator;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }



}

