package com.dezso.varga.pokerfoci.exeptions;

/**
 * Created by dezso on 28.11.2017.
 */
public class UserAlreadyExistsException extends GlobalException {

    int statusCode;

    public UserAlreadyExistsException(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
