package com.dezso.varga.pokerfoci.authentication.exeptions;

/**
 * Created by dezso on 18.11.2017.
 */
public class AuthExeption extends BgException{

    int statusCode;

    public AuthExeption(String message, int statusCode) {
        super(message, statusCode);
        this.statusCode = statusCode;
    }
}
