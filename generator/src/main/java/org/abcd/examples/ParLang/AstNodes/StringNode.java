package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class StringNode extends LiteralNode<String>{
    public StringNode(String value) {
        super(value);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}
