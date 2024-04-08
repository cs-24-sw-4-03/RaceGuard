package org.abcd.examples.ParLang.AstNodes;

public abstract class IterationNode {

    AstNode condition;
    AstNode body;


    public IterationNode(AstNode condition, AstNode body) {
        this.condition = condition;
        this.body = body;
    }

    public AstNode getCondition() {
        return condition;
    }

    public AstNode getBody() {
        return body;
    }
}
