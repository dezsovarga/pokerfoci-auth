package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.PokerfociAuthMain;
import com.dezso.varga.pokerfoci.controller.ApiWrapper;
import com.dezso.varga.pokerfoci.dto.AccountDto;
import com.dezso.varga.pokerfoci.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.dto.TokenInfoResponseDto;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import com.dezso.varga.pokerfoci.repository.RoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static junit.framework.TestCase.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PokerfociAuthMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseControllerTest {

    @LocalServerPort
    protected int port;

    @Autowired
    protected AccountRepository accountRepository;

    @Autowired
    protected RoleRepository roleRepository;

    ObjectMapper mapper = new ObjectMapper();

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    ApiWrapper apiWrapper = new ApiWrapper();

    protected static final String BASIC = "Basic ";

    protected String randomEmail = RandomStringUtils.randomAlphabetic(10)+"@varga.com";
    protected String password = "password";

    protected AccountDto accountDto = AccountDto.builder()
            .username("username")
            .email(randomEmail)
            .password(password)
            .confirmPassword(password)
            .build();

    @Test
    public void initTest(){

    }

    protected String registerAccount(AccountDto accountDto) throws Exception{
        RegisterRequestDto requestBody = RegisterRequestDto.builder().accountDto(accountDto).build();
        String jsonBody = mapper.writeValueAsString(requestBody);

        String confirmToken = apiWrapper.registerUser("/account/register", port, jsonBody);
        assertNotNull(confirmToken);

        //User confirmation
        ResponseEntity<String> confirmResponse = apiWrapper.confirmUser("/account/register/confirm/", port, confirmToken);
        String bearerToken = mapper.readValue(confirmResponse.getBody(), TokenInfoResponseDto.class).getBearerToken();
        String decodedConfirmResponse = new String(Base64.decodeBase64(bearerToken));
        String authToken = mapper.readValue(decodedConfirmResponse, Map.class).get("token").toString();
        return authToken;
    }

    protected String generateBearerToken(String userEmail, String password) throws Exception{
        //Login user
        String basicAuthToken = BASIC + new String(Base64.encodeBase64((userEmail + ":" + password).getBytes()));
        return apiWrapper.loginUser(port, basicAuthToken);
    }
}
