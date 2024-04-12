package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.LanguageType;
import org.abcd.examples.ParLang.NodeVisitor;

public class VarDclNode extends DclNode{
    private final String type;
    public VarDclNode(String id, String type) {
        super(id);
        this.type=type;
    }
    public String getType() {
        return type;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
