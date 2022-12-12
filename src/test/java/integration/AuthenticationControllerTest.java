package integration;

import com.dezso.varga.pokerfoci.authentication.PokerfociAuthMain;
import com.dezso.varga.pokerfoci.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.domain.Role;
import com.dezso.varga.pokerfoci.authentication.dto.AccountDto;
import com.dezso.varga.pokerfoci.authentication.dto.ChangePasswordRequestDto;
import com.dezso.varga.pokerfoci.authentication.dto.TokenInfoResponseDto;
import com.dezso.varga.pokerfoci.authentication.dto.RegisterRequestDto;
import com.dezso.varga.pokerfoci.authentication.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static integration.ApiWrapper.LOGIN_PATH;
import static junit.framework.TestCase.*;

/**
 * Created by dezso on 13.12.2017.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PokerfociAuthMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AccountRepository accountRepository;

    ObjectMapper mapper = new ObjectMapper();

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    ApiWrapper apiWrapper = new ApiWrapper();

    private static final String BASIC = "Basic ";

    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8")
    );

    String randomEmail = RandomStringUtils.randomAlphabetic(10)+"@varga.com";
    String password = "password";
    AccountDto accountDto = AccountDto.builder()
            .username("username")
            .email(randomEmail)
            .password(password)
            .confirmPassword(password)
            .build();

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

    private String registerAccount(AccountDto accountDto) throws Exception{
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
