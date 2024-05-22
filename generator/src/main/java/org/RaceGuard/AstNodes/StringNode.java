package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class StringNode extends LiteralNode<String>{
    public StringNode(String value) {
        super(value);
    }
    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }

}
