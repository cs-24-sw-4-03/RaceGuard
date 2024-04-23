package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.NodeVisitor;

public class ScriptDclNode extends DclNode{

    public ScriptDclNode(String id) {
        super(id);
    }

    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
