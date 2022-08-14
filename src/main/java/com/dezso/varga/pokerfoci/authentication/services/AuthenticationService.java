package com.dezso.varga.pokerfoci.authentication.services;

import com.dezso.varga.pokerfoci.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.dto.RegisterRequestDto;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.MalformedURLException;
import java.net.URL;

public interface AuthenticationService {

    URL generateConfirmationLink(String confirmToken) throws MalformedURLException;

    Account login(String authHeader) throws Exception;

    Account saveAccount(String confirmToken) throws Exception;

    void validateRegistrationRequest(RegisterRequestDto registerRequestDto) throws Exception;

    String getConfirmationToken(RegisterRequestDto registerRequestDto) throws Exception;

    BCryptPasswordEncoder getPasswordEncoder();
}
