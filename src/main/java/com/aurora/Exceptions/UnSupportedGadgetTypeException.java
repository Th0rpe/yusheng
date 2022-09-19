package com.aurora.Exceptions;

public class UnSupportedGadgetTypeException extends RuntimeException {
    public UnSupportedGadgetTypeException(){ super();}
    public UnSupportedGadgetTypeException(String message){
        super(message);
    }
}
