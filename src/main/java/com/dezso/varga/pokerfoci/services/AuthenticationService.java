package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.dto.ChangePasswordRequestDto;
import com.dezso.varga.pokerfoci.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.exeptions.BgException;

import java.net.MalformedURLException;
import java.net.URL;

public interface AuthenticationService {

    URL generateConfirmationLink(String confirmToken) throws MalformedURLException;

    Account login(String authHeader) throws Exception;

    Account saveAccount(String confirmToken) throws Exception;

    void validateRegistrationRequest(RegisterRequestDto registerRequestDto) throws Exception;

    String getConfirmationToken(RegisterRequestDto registerRequestDto) throws Exception;

    boolean changePassword(ChangePasswordRequestDto changePasswordRequestDto) throws BgException;
}
