package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public class MethodDclNode extends ScriptMethodNode{
    public MethodDclNode(String id, String returnType, String methodType) {
        super(id, returnType, methodType);
    }

    public ParametersNode getParametersNode(){
        ParametersNode node=(ParametersNode) this.getChildren().get(0);//First child is always the ParametersNode
        if (node !=null){
            return node;
        }else{
            throw new RuntimeException("Tried accessing child of MethodDclNode which was null");
        }
    }

    public AstNode getBodyNode(){
        AstNode node= this.getChildren().get(1);//Second child is either a BodyNode or LocalMethodBodyNode
        if (node!=null){
            return node;
        }else{
            throw new RuntimeException("Tried accessing child of MethodDclNode which was null");
        }
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
