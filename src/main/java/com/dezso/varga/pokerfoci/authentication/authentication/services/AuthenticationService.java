package com.dezso.varga.pokerfoci.authentication.authentication.services;

import com.dezso.varga.pokerfoci.authentication.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.authentication.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.authentication.domain.RegisterRequest;
import com.dezso.varga.pokerfoci.authentication.authentication.repositories.AccountRepository;
import com.dezso.varga.pokerfoci.authentication.exeptions.AuthExeption;
import com.dezso.varga.pokerfoci.authentication.exeptions.BgException;
import com.dezso.varga.pokerfoci.authentication.exeptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private AccountRepository accountRepository;

    public Account login(String authHeader) throws Exception {
        Account credentials = AuthUtils.extractAccountFromBasicToken(authHeader);
        Account existingAccount = accountRepository.findByEmail(credentials.getEmail());
        if (existingAccount == null || !getPasswordEncoder().matches(credentials.getPassword(), existingAccount.getPassword())) {
            throw new AuthExeption("Invalid credentials. Please check your email and password.",
                    HttpStatus.UNAUTHORIZED.value());
        }
        return existingAccount;
    }

    public Account saveAccount(String confirmToken) throws Exception{
        Account account = AuthUtils.validateConfirmToken(confirmToken);
        Account existingAccount = accountRepository.findByEmail(account.getEmail());
        if (existingAccount == null) {
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = bCryptPasswordEncoder.encode(account.getPassword());
            account.setPassword(hashedPassword);
            accountRepository.save(account);
        } else {
            throw new UserAlreadyExistsException("User already verified", HttpStatus.CONFLICT.value());
        }
        return account;
    }

    public String getConfirmationToken(RegisterRequest registerRequest) throws Exception{
        if (registerRequest == null
                || registerRequest.getAccount().getFirstName() == null
                || registerRequest.getAccount().getFirstName().trim().isEmpty()
                || registerRequest.getAccount().getLastName() == null
                || registerRequest.getAccount().getLastName().trim().isEmpty()
                || registerRequest.getAccount().getEmail() == null
                || registerRequest.getAccount().getEmail().trim().isEmpty()
                || registerRequest.getAccount().getPassword() == null
                || registerRequest.getAccount().getPassword().trim().isEmpty()) {
            throw new BgException("Missing or invalid mandatory fields at registration",
                    HttpStatus.PRECONDITION_FAILED.value());
        }
        Account existingAccount = accountRepository.findByEmail(registerRequest.getAccount().getEmail().trim());
        if (existingAccount != null) {
            throw new UserAlreadyExistsException("Account already exists", HttpStatus.CONFLICT.value());
        }

        return AuthUtils.generateRegisterConfirmationToken(registerRequest);
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
