package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.NodeVisitor;

public class MainFuncDcl extends AstNode {


    public AstNode accept(NodeVisitor visitor) {
        return visitor.visit(this);
    }
}
