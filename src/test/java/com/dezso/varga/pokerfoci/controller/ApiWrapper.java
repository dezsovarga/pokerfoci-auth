package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.dto.TokenInfoResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.codec.binary.Base64;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

/**
 * Created by dezso on 17.12.2017.
 */

public class ApiWrapper {

    TestRestTemplate restTemplate = new TestRestTemplate();
    ObjectMapper mapper = new ObjectMapper();
    HttpHeaders headers = new HttpHeaders();

    public static final String LOGIN_PATH = "/account/login";
    public static final String CHANGE_PASSWORD_PATH = "/account/change-password";
    public static final String LIST_ACCOUNTS_FOR_ADMIN_PATH = "/admin/accounts";
    public static final String ADD_NEW_ACCOUNT_FOR_ADMIN_PATH = "/admin/account";
    public static final String UPDATE_ACCOUNT_FOR_ADMIN_PATH = "/admin/account";
    public static final String ADD_NEW_EVENT_PATH = "/admin/event";
    public static final String LIST_EVENTS_FOR_ADMIN_PATH = "/admin/events";


    public String registerUser(String path, int port, String jsonBody) throws Exception{
        headers.clear();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<String> registerResponse = callApi(path, port, headers, jsonBody, HttpMethod.POST);
        return mapper.readValue(registerResponse.getBody(), RegisterRequestDto.class).getConfirmToken();
    }

    public ResponseEntity<String> callApi(String path, int port, HttpHeaders headers, String jsonBody, HttpMethod httpMethod) {

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        return restTemplate.exchange(
                "http://localhost:" + port + path, httpMethod, entity, String.class);
    }

    public ResponseEntity<String> confirmUser(String path, int port, String confirmToken) throws Exception {
        headers.clear();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return callApi(path+confirmToken, port, headers, null, HttpMethod.GET);
    }

    public String loginUser(int port, String basicAuthToken) throws Exception{
        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, basicAuthToken);
        ResponseEntity<String> authTokenResponse = callApi(LOGIN_PATH, port, headers, null, HttpMethod.POST);
        String bearerToken = mapper.readValue(authTokenResponse.getBody(), TokenInfoResponseDto.class).getBearerToken();
        String decodedResponse = new String(Base64.decodeBase64(bearerToken));
        return mapper.readValue(decodedResponse, Map.class).get("token").toString();
    }

    public ResponseEntity<String> changePassword(int port, String bearerToken, String changePasswordRequestBody) throws Exception{

        return callApi(CHANGE_PASSWORD_PATH, port, createHeaders(bearerToken), changePasswordRequestBody, HttpMethod.POST);
    }

    public ResponseEntity<String> getAccountsForAdmin(int port, String bearerToken) {

        return callApi(LIST_ACCOUNTS_FOR_ADMIN_PATH, port, createHeaders(bearerToken), null, HttpMethod.GET);
    }

    public ResponseEntity<String> getEventsForAdmin(int port, String bearerToken) {

        return callApi(LIST_EVENTS_FOR_ADMIN_PATH, port, createHeaders(bearerToken), null, HttpMethod.GET);
    }

    public ResponseEntity<String> addNewAccountForAdmin(int port, String bearerToken, AccountDto newAccountDto) throws JsonProcessingException {

        String jsonBody = mapper.writeValueAsString(newAccountDto);

        return callApi(ADD_NEW_ACCOUNT_FOR_ADMIN_PATH, port, createHeaders(bearerToken), jsonBody, HttpMethod.POST);
    }

    public ResponseEntity<String> updateAccount(int port, String bearerToken, AccountForAdminDto accountDto) throws JsonProcessingException {

        String jsonBody = mapper.writeValueAsString(accountDto);

        return callApi(UPDATE_ACCOUNT_FOR_ADMIN_PATH, port, createHeaders(bearerToken), jsonBody, HttpMethod.PUT);
    }

    public ResponseEntity<String> addNewEvent(int port, String bearerToken, CreateEventDto newEventDto) throws JsonProcessingException {

        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String jsonBody = mapper.writeValueAsString(newEventDto);

        return callApi(ADD_NEW_EVENT_PATH, port, createHeaders(bearerToken), jsonBody, HttpMethod.POST);
    }

    private HttpHeaders createHeaders(String bearerToken) {
        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        return headers;
    }
}
