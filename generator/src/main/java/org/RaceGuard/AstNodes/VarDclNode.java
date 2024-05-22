package org.RaceGuard.AstNodes;

import org.RaceGuard.NodeVisitor;

public class VarDclNode extends DclNode{
    private boolean isInitialized = false;
    public VarDclNode(String id, String type) {
        super(id);
        setType(type);
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        isInitialized = initialized;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
