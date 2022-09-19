package com.aurora.Exceptions;

public class NullMethodArgsException extends IllegalArgumentException {
    public NullMethodArgsException(){
        super();
    }
    public NullMethodArgsException(String message){
        super(message);
    }
}
