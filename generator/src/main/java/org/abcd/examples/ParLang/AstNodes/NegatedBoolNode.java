package org.abcd.examples.ParLang.AstNodes;

public class NegatedBoolNode extends BoolExprNode {

        public NegatedBoolNode(AstNode c) {
            super(true, c);
        }
}
