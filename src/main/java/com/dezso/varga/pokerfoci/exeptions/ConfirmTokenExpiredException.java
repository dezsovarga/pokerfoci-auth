package com.dezso.varga.pokerfoci.exeptions;

/**
 * Created by dezso on 30.11.2017.
 */
public class ConfirmTokenExpiredException extends GlobalException {

    int statusCode;

    public ConfirmTokenExpiredException(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
