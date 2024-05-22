package org.RaceGuard.Exceptions;

public class DuplicateActorTypeException extends RuntimeException{
    public DuplicateActorTypeException(String errorMessage) {
        super(errorMessage);
    }
}
