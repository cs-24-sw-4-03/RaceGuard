package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.ParLangAstVisitor;

public abstract class AstNode {
    public abstract AstNode accept(ParLangAstVisitor visitor);
}
