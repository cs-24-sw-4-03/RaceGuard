package org.abcd.examples.ParLang;

import org.abcd.examples.ParLang.AstNodes.*;

public interface NodeVisitor {
    void visitChildren(AstNode node);

    void visit(InitNode node);

    void visit(IdentifierNode node);
    void visit(ActorIdentifierNode node);
    void visit(ParametersNode node);
    void visit(ReturnStatementNode node);
    //void visit(SpawnCallNode node);
    void visit(MethodCallNode node);
    void visit(AssignNode node);


    void visit(VarDclNode node);
    //void visit(ArrayDclNode node);
    void visit(ActorDclNode node);
    void visit(ActorStateNode node);
    void visit(KnowsNode node);
    void visit(MethodDclNode node);
    void visit(MainDclNode node);
    void visit(SpawnDclNode node);


    void visit(IntegerNode node);
    //void visit(BoolNode node);
    void visit(DoubleNode node);
    void visit(StringNode node);

    //void visit(CompareExprNode node);
    //void visit(BoolExprNode node);
    void visit(ArithExprNode node);

    void visit(WhileNode node);
    void visit(ForNode node);
    //void visit(SelectNode);

    void visit(ArrayAccessNode node);
    void visit(StateAccessNode node);
    void visit(KnowsAccessNode node);
}
