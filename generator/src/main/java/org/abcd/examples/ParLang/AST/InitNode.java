package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.NodeVisitor;

public class InitNode extends AstNode {
    public AstNode accept(NodeVisitor visitor){
        return visitor.visit(this);
    };
}
