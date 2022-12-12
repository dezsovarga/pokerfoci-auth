package com.dezso.varga.pokerfoci.authentication.services;

import com.dezso.varga.pokerfoci.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.dto.AccountDto;
import com.dezso.varga.pokerfoci.authentication.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.authentication.exeptions.BgException;

import com.dezso.varga.pokerfoci.authentication.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.ArgumentMatchers.anyString;

public class AuthenticationServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private AutoCloseable closeable;

    @InjectMocks
    private AuthenticationService authenticationService = new AuthenticationServiceImpl(accountRepository, bCryptPasswordEncoder);

    private RegisterRequestDto registerRequestDto = createARegisterRequestDto();

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }


    @Test
    public void testEmptyEmailAtRegistration() {

        registerRequestDto.getAccountDto().setEmail("");
        BgException thrown = Assertions.assertThrows (BgException.class, () -> {
            authenticationService.validateRegistrationRequest(registerRequestDto);
        }, "BgException was expected");

        Assertions.assertEquals("Missing or invalid mandatory fields at registration", thrown.getMessage());
        Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED.value(), thrown.getStatusCode());
    }

    @Test
    public void testPasswordsNotMatchingAtRegistration() {

        Mockito.when(accountRepository.findByEmail(anyString())).thenReturn(null);
        registerRequestDto.getAccountDto().setConfirmPassword("randomPassword");
        BgException thrown = Assertions.assertThrows (BgException.class, () -> {
            authenticationService.validateRegistrationRequest(registerRequestDto);
        }, "BgException was expected");

        Assertions.assertEquals("Password and confirmPassword fields do not match", thrown.getMessage());
        Assertions.assertEquals(HttpStatus.PRECONDITION_FAILED.value(), thrown.getStatusCode());
    }

    @Test
    public void testAccountAlreadyExistsAtRegistration() {

        Mockito.when(accountRepository.findByEmail(anyString())).thenReturn(new Account());
        BgException thrown = Assertions.assertThrows (BgException.class, () -> {
            authenticationService.validateRegistrationRequest(registerRequestDto);
        }, "BgException was expected");

        Assertions.assertEquals("Account already exists", thrown.getMessage());
        Assertions.assertEquals(HttpStatus.CONFLICT.value(), thrown.getStatusCode());
    }

    private RegisterRequestDto createARegisterRequestDto() {
        AccountDto accountDto = AccountDto.builder().username("username")
                .email("email@email.com")
                .password("password")
                .confirmPassword("password")
                .build();
        return RegisterRequestDto.builder().accountDto(accountDto).build();
    }
}