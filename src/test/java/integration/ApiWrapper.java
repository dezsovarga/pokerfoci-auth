package integration;

import com.dezso.varga.pokerfoci.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.dto.TokenInfoResponseDto;
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

    public String registerUser(String path, int port, String jsonBody) throws Exception{
        headers.clear();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<String> registerResponse = callApi(path, port, headers, jsonBody, HttpMethod.POST);
        return mapper.readValue(registerResponse.getBody(), RegisterRequestDto.class).getConfirmToken();
    }

    public ResponseEntity<String> callApi(String path, int port, HttpHeaders headers, String jsonBody, HttpMethod httpMethod) {

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + path, httpMethod, entity, String.class);

        return response;
    }

    public ResponseEntity<String> confirmUser(String path, int port, String confirmToken) throws Exception {
        headers.clear();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<String> confirmResponse = callApi(path+confirmToken, port, headers, null, HttpMethod.GET);
        return confirmResponse;
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
        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        ResponseEntity<String> changePasswordResponse =
                callApi(CHANGE_PASSWORD_PATH, port, headers, changePasswordRequestBody, HttpMethod.POST);
        return changePasswordResponse;

    }

    public ResponseEntity<String> getAccountsForAdmin(int port, String bearerToken) {
        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken);
        ResponseEntity<String> accountListResponse =
                callApi(LIST_ACCOUNTS_FOR_ADMIN_PATH, port, headers, null, HttpMethod.GET);
        return accountListResponse;
    }
}
