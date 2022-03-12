package com.dezso.varga.pokerfoci.authentication.exeptions;

/**
 * Created by dezso on 30.11.2017.
 */
public class ConfirmTokenExpiredException extends BgException{

    int statusCode;

    public ConfirmTokenExpiredException(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
