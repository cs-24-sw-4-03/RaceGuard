package org.abcd.examples.ParLang.Exceptions;
import org.antlr.v4.runtime.RecognitionException;

public class DuplicateActorTypeException extends RuntimeException{
    public DuplicateActorTypeException(String errorMessage) {
        super(errorMessage);
    }
}
