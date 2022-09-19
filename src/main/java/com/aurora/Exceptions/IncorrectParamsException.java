package com.aurora.Exceptions;

public class IncorrectParamsException extends RuntimeException {
    public IncorrectParamsException(){
        super();
    }
    public IncorrectParamsException(String message){
        super(message);
    }
}
