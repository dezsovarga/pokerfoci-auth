package com.dezso.varga.pokerfoci.authentication.exeptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Created by dezso on 25.06.2017.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler()
    @ResponseStatus(value=HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public Map<String, String> handleMissingFieldsException(BgException ex) {
        return ex.getErrorBody();
    }

    @ExceptionHandler(AuthExeption.class)
    @ResponseStatus(value=HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Map<String, String> handleAuthException(AuthExeption ex) {
        return ex.getErrorBody();
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseStatus(value=HttpStatus.CONFLICT)
    @ResponseBody
    public Map<String, String> handleUserAlreadyExistsException(BgException ex) {
        return ex.getErrorBody();
    }

    @ExceptionHandler(ConfirmTokenExpiredException.class)
    @ResponseStatus(value=HttpStatus.PRECONDITION_FAILED)
    @ResponseBody
    public Map<String, String> handleConfirmTokenExpiredException(BgException ex) {
        return ex.getErrorBody();
    }

}

