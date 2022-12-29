package com.dezso.varga.pokerfoci.exeptions;

public class InvalidCredentialsException extends BgException{

    int statusCode;

    public InvalidCredentialsException(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
