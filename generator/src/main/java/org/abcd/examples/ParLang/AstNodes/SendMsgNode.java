package org.abcd.examples.ParLang.AstNodes;

public class SendMsgNode extends AstNode{

    private final String msgName;
    private final String receiver;

    public SendMsgNode(String receiver, String msgName){
        this.receiver = receiver;
        this.msgName = msgName;
    }

    public String getMsgName(){
        return this.msgName;
    }

    public String getReceiver(){
        return this.receiver;
    }
}
