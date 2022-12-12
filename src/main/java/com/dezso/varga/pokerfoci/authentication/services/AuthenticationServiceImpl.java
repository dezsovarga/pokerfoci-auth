package com.dezso.varga.pokerfoci.authentication.services;

import com.dezso.varga.pokerfoci.authentication.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.dto.ChangePasswordRequestDto;
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
public class AuthenticationServiceImpl implements AuthenticationService{

    private AccountRepository accountRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public URL generateConfirmationLink(String confirmToken) throws MalformedURLException {
        return new URL("http://localhost:3000/activate-account/"+confirmToken);
    }

    @Override
    public Account login(String authHeader) throws Exception {
        Account credentials = AuthUtils.extractAccountFromBasicToken(authHeader);
        Account existingAccount = accountRepository.findByEmail(credentials.getEmail());
        if (existingAccount == null || !bCryptPasswordEncoder.matches(credentials.getPassword(), existingAccount.getPassword())) {
            throw new AuthExeption("Invalid credentials. Please check your email and password.",
                    HttpStatus.UNAUTHORIZED.value());
        }
        return existingAccount;
    }

    @Override
    public Account saveAccount(String confirmToken) throws Exception{
        Account account = AuthUtils.validateConfirmToken(confirmToken);
        Account existingAccount = accountRepository.findByEmail(account.getEmail());
        if (existingAccount == null) {
            String hashedPassword = bCryptPasswordEncoder.encode(account.getPassword());
            account.setPassword(hashedPassword);
            accountRepository.save(account);
        } else {
            throw new UserAlreadyExistsException("User already verified", HttpStatus.CONFLICT.value());
        }
        return account;
    }

    @Override
    public void validateRegistrationRequest(RegisterRequestDto registerRequestDto) throws Exception{
        if (registerRequestDto == null
                || registerRequestDto.getAccountDto().getUsername() == null
                || registerRequestDto.getAccountDto().getUsername().trim().isEmpty()
                || registerRequestDto.getAccountDto().getEmail() == null
                || registerRequestDto.getAccountDto().getEmail().trim().isEmpty()
                || registerRequestDto.getAccountDto().getPassword() == null
                || registerRequestDto.getAccountDto().getPassword().trim().isEmpty()
                || registerRequestDto.getAccountDto().getConfirmPassword() == null
                || registerRequestDto.getAccountDto().getConfirmPassword().trim().isEmpty()) {
            throw new BgException("Missing or invalid mandatory fields at registration",
                    HttpStatus.PRECONDITION_FAILED.value());
        }
        Account existingAccount = accountRepository.findByEmail(registerRequestDto.getAccountDto().getEmail().trim());
        if (existingAccount != null) {
            throw new UserAlreadyExistsException("Account already exists", HttpStatus.CONFLICT.value());
        }
        if (!registerRequestDto.getAccountDto().getPassword().equals(registerRequestDto.getAccountDto().getConfirmPassword())){
            throw new BgException("Password and confirmPassword fields do not match",
                    HttpStatus.PRECONDITION_FAILED.value());
        }
    }

    @Override
    public String getConfirmationToken(RegisterRequestDto registerRequestDto) throws Exception{
        validateRegistrationRequest(registerRequestDto);
        return AuthUtils.generateRegisterConfirmationToken(registerRequestDto);
    }

    @Override
    public boolean changePassword(ChangePasswordRequestDto changePasswordRequestDto) throws BgException {
        Account existingAccount = accountRepository.findByEmail(changePasswordRequestDto.getEmail());
        boolean isValidAccount = bCryptPasswordEncoder.matches(changePasswordRequestDto.getOldPassword(), existingAccount.getPassword());
        if (isValidAccount) {
            existingAccount.setPassword(bCryptPasswordEncoder.encode(changePasswordRequestDto.getNewPassword()));
            accountRepository.save(existingAccount);
            return true;
        } else {
            throw new BgException("Invalid request. Authorization failed for changing password",
                    HttpStatus.PRECONDITION_FAILED.value());
        }
    }
}
