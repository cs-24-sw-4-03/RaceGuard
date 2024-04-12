package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public class ArgumentsNode extends AstNode{

    public ArgumentsNode(){
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
