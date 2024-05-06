package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class VarDclNode extends DclNode{
    public VarDclNode(String id, String type) {
        super(id);
        setType(type);
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
