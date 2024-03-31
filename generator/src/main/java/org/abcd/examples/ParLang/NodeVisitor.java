package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AST.*;

public interface NodeVisitor {
    AstNode visit(AstNode node);
    AstNode visit(ArithExpression node);
    AstNode visit(DoubleNode node);
    AstNode visit(Integer node);
    AstNode visit(MainFuncDcl node);
    AstNode visit(Body node);
}
