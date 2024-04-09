package org.abcd.examples.ParLang.AstNodes;

public class BoolAndExpNode extends ExprNode{
    public BoolAndExpNode(AstNode l, AstNode r) {
        this.addChild(l);
        this.addChild(r);
    }

}
