package org.abcd.examples.ParLang.Exceptions;

public class SendMsgException extends RuntimeException{
    public SendMsgException(String message){
        super(message);
    }
}
