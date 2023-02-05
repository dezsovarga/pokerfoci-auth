package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.dto.ChangePasswordRequestDto;
import com.dezso.varga.pokerfoci.dto.TokenInfoResponseDto;
import com.dezso.varga.pokerfoci.dto.RegisterRequestDto;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

import static com.dezso.varga.pokerfoci.controller.ApiWrapper.LOGIN_PATH;
import static junit.framework.TestCase.*;

/**
 * Created by dezso on 13.12.2017.
 */

public class AuthenticationControllerTest extends BaseControllerTest {

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    @Test
    public void testUserLogin() throws Exception {

        this.registerAccount(accountDto);

        //Login user
        String basicAuthToken = BASIC + new String(Base64.encodeBase64((randomEmail + ":" + password).getBytes()));
        String authToken = apiWrapper.loginUser(port, basicAuthToken);
        assertNotNull(authToken);

    }

    @Test
    public void testLoginTokenResponseContainsRoles() throws Exception {

        this.registerAccount(accountDto);

        //Login user
        String basicAuthToken = BASIC + new String(Base64.encodeBase64((randomEmail + ":" + password).getBytes()));

        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, basicAuthToken);
        ResponseEntity<String> authTokenResponse = apiWrapper.callApi(LOGIN_PATH, port, headers, null, HttpMethod.POST);
        Set<String> roles = mapper.readValue(authTokenResponse.getBody(), TokenInfoResponseDto.class).getRoles();

        assertEquals(Set.of("ROLE_USER"), roles);

    }

    @Test
    public void testUserInvalidLogin() {
//        invalid login
        randomEmail = randomEmail + "invalidPart";
        String invalidBasicAuthToken = BASIC + new String(Base64.encodeBase64((randomEmail+":"+password).getBytes()));
        headers.clear();
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.AUTHORIZATION, invalidBasicAuthToken);
        ResponseEntity<String> response = apiWrapper.callApi("/account/login", port, headers, null, HttpMethod.POST);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testUserConfirmation() throws Exception {

        //User registration

        RegisterRequestDto requestBody = RegisterRequestDto.builder().accountDto(accountDto).build();
        String jsonBody = mapper.writeValueAsString(requestBody);

        String confirmToken = apiWrapper.registerUser("/account/register", port, jsonBody);
        assertNotNull(confirmToken);

        //User confirmation
        ResponseEntity<String> confirmResponse = apiWrapper.confirmUser("/account/register/confirm/", port, confirmToken);
        String bearerToken = mapper.readValue(confirmResponse.getBody(), TokenInfoResponseDto.class).getBearerToken();
        String decodedConfirmResponse = new String(Base64.decodeBase64(bearerToken));
        String authToken = mapper.readValue(decodedConfirmResponse, Map.class).get("token").toString();
        assertNotNull(authToken);

        //User already confirmed
        confirmResponse = apiWrapper.confirmUser("/account/register/confirm/", port, confirmToken);
        Map<String, String> responseMap = mapper.readValue(confirmResponse.getBody(), Map.class);
        assertEquals("User already verified", responseMap.get("reason"));
        assertEquals(HttpStatus.CONFLICT, confirmResponse.getStatusCode());

        //confirmation token expired or invalid
        confirmResponse = apiWrapper.confirmUser("/account/register/confirm/",
                port, "invalidasdfdsfdsjfhsdkfhskfksdfjskfhsk");
        responseMap = mapper.readValue(confirmResponse.getBody(), Map.class);
        assertEquals("Confirmation token expired or invalid", responseMap.get("reason"));
        assertEquals(HttpStatus.PRECONDITION_FAILED, confirmResponse.getStatusCode());
    }

    @Test
    public void testChangePassword() throws Exception {

        this.registerAccount(accountDto);

        //Login user
        String basicAuthToken = BASIC + new String(Base64.encodeBase64((randomEmail + ":" + password).getBytes()));
        String bearerToken = apiWrapper.loginUser(port, basicAuthToken);

        ChangePasswordRequestDto changePasswordRequestDto =
                ChangePasswordRequestDto.builder().email(randomEmail).oldPassword(password).newPassword("newPassword").build();
        String changePasswordRequestBody = mapper.writeValueAsString(changePasswordRequestDto);

        //change password
        ResponseEntity<String> response = apiWrapper.changePassword(port, bearerToken, changePasswordRequestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Account account = accountRepository.findByEmail(randomEmail);
        assertTrue(new BCryptPasswordEncoder().matches("newPassword", account.getPassword()));

        //change with invalid password
        changePasswordRequestDto.setOldPassword("invalidOldPassword");
        response = apiWrapper.changePassword(port, bearerToken, changePasswordRequestBody);

        Map<String, String> responseMap = mapper.readValue(response.getBody(), Map.class);
        assertEquals("Invalid request. Authorization failed for changing password", responseMap.get("reason"));
        assertEquals(HttpStatus.PRECONDITION_FAILED, response.getStatusCode());
    }
}
