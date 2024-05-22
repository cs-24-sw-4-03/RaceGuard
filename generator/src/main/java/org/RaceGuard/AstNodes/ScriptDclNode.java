package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class ScriptDclNode extends DclNode{

    public ScriptDclNode(String id) {
        super(id);
    }

    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
