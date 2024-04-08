package org.abcd.examples.ParLang.AstNodes;

public class ForNode extends IterationNode{
    AstNode init;
    AstNode update;

    public ForNode(AstNode init, AstNode condition, AstNode update, AstNode body) {
        super(condition, body);
        this.init = init;
        this.update = update;
    }

    public AstNode getInit() {
        return init;
    }

    public AstNode getUpdate() {
        return update;
    }

    public void setInit(AstNode init) {
        this.init = init;
    }

    public void setUpdate(AstNode update) {
        this.update = update;
    }
}
