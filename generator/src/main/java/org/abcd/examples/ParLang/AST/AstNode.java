package org.abcd.examples.ParLang.AST;

import org.abcd.examples.ParLang.AstVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class AstNode {
    public List<AstNode> children = new ArrayList<>();

    public abstract AstNode accept(AstVisitor visitor);
}
