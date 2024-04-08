package org.abcd.examples.ParLang.AstNodes;

import org.abcd.examples.ParLang.LanguageType;

public class VarDclNode extends DclNode{
    private final LanguageType type;
    public VarDclNode(String id, LanguageType type) {
        super(id);
        this.type=type;
    }
    public LanguageType getType() {
        return type;
    }
}
