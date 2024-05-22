package org.RaceGuard.Exceptions;

public class DuplicateScriptTypeException extends RuntimeException{
    public DuplicateScriptTypeException(String errorMessage) {
        super(errorMessage);
    }
}
