package org.abcd.examples.ParLang.AstNodes;
import org.abcd.examples.ParLang.NodeVisitor;

public class MethodDclNode extends ScriptMethodNode{
    public MethodDclNode(String id, String returnType, String methodType) {
        super(id, returnType, methodType);
    }

    public ParametersNode getParametersNode(){
        ParametersNode node=(ParametersNode) this.getChildren().get(0);
        if (node !=null){
            return node;
        }else{
            throw new RuntimeException("Tried accessing child of MethodDclNode which was null");
        }
    }

    public AstNode getBodyNode(){
        AstNode node= this.getChildren().get(1);//Can either be of type BodyNode or LocalMethodBodyNode
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
