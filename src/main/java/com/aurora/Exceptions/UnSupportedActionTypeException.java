package com.aurora.Exceptions;

public class UnSupportedActionTypeException extends RuntimeException{
    public UnSupportedActionTypeException(){
        super();
    }
    public UnSupportedActionTypeException(String message){
        super(message);
    }
}
