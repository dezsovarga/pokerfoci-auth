package com.dezso.varga.pokerfoci.exeptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dezso on 25.06.2017.
 */
public class GlobalException extends Exception {

    int statusCode;

    public GlobalException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GlobalException(String message) {
        super(message);
    }

    public Map<String, String> getErrorBody() {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("reason", getMessage());
//        errorBody.put("message", message);
        errorBody.put("code", String.valueOf(statusCode));
        return  errorBody;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
