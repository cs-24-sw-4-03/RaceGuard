package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.NodeVisitor;

public class IntegerNode extends LiteralNode<Integer>{
    public IntegerNode(int value){super(value);};
    @Override
    public AstNode accept(NodeVisitor visitor) {
        return  visitor.visit(this);
    }
}
