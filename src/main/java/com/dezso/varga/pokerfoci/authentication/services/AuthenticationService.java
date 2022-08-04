package com.dezso.varga.pokerfoci.authentication.services;

import com.dezso.varga.pokerfoci.authentication.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.authentication.repository.AccountRepository;
import com.dezso.varga.pokerfoci.authentication.exeptions.AuthExeption;
import com.dezso.varga.pokerfoci.authentication.exeptions.BgException;
import com.dezso.varga.pokerfoci.authentication.exeptions.UserAlreadyExistsException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private AccountRepository accountRepository;

    public URL generateConfirmationLink(String confirmToken) throws MalformedURLException {
        return new URL("http://localhost:8081/account/register/confirm/"+confirmToken);
    }

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

    public String getConfirmationToken(RegisterRequestDto registerRequestDto) throws Exception{
        if (registerRequestDto == null
                  || registerRequestDto.getAccountDto().getUsername() == null
                  || registerRequestDto.getAccountDto().getUsername().trim().isEmpty()
                || registerRequestDto.getAccountDto().getEmail() == null
                || registerRequestDto.getAccountDto().getEmail().trim().isEmpty()
                || registerRequestDto.getAccountDto().getPassword() == null
                || registerRequestDto.getAccountDto().getPassword().trim().isEmpty()) {
            throw new BgException("Missing or invalid mandatory fields at registration",
                    HttpStatus.PRECONDITION_FAILED.value());
        }
        Account existingAccount = accountRepository.findByEmail(registerRequestDto.getAccountDto().getEmail().trim());
        if (existingAccount != null) {
            throw new UserAlreadyExistsException("Account already exists", HttpStatus.CONFLICT.value());
        }

        return AuthUtils.generateRegisterConfirmationToken(registerRequestDto);
    }

    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
