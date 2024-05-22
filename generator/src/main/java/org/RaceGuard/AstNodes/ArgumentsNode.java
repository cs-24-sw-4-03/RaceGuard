package org.RaceGuard.AstNodes;
import org.RaceGuard.NodeVisitor;

public class ArgumentsNode extends AstNode{

    public ArgumentsNode(){
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
