package org.abcd.examples.ParLang.Exceptions;

public class DuplicateScriptTypeException extends RuntimeException{
    public DuplicateScriptTypeException(String errorMessage) {
        super(errorMessage);
    }
}
