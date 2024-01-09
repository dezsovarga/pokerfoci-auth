package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import com.dezso.varga.pokerfoci.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.slf4j.LoggerFactory.getLogger;

class AdminControllerTest extends BaseControllerTest {

    @Autowired
    private EventRepository eventRepository;

    private static final Logger LOG = getLogger(AdminControllerTest.class);

    @Test
    void getListOfAccountsForAdminPage() throws Exception {
        String username = RandomStringUtils.random(10, true, false);
        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");
        ResponseEntity<String> response = apiWrapper.getAccountsForAdmin(port, bearerToken);

        List<AccountForAdminDto> accountForAdminDtoList =
                mapper.readValue(response.getBody(), new TypeReference<>(){});

        assertFalse(accountForAdminDtoList.isEmpty());
        assertEquals(account.getEmail(), accountForAdminDtoList.get(0).getEmail());
        assertEquals(account.getUsername(), accountForAdminDtoList.get(0).getUsername());
        assertEquals(account.isActive(), accountForAdminDtoList.get(0).getIsActive());
        assertTrue(accountForAdminDtoList.get(0).getIsAdmin());
    }

    @Test
    void getListOfAccountsForAdminPageWithNoAdminRole() throws Exception {
        String username = RandomStringUtils.random(10, true, false);
        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_USER", username, passwordEncoder.encode("password"));
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");
        ResponseEntity<String> response = apiWrapper.getAccountsForAdmin(port, bearerToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getListOfEventsForAdminPageWithNoAdminRole() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_USER", username, passwordEncoder.encode("password"));
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");
        ResponseEntity<String> response = apiWrapper.getEventsForAdmin(port, bearerToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void addNewAccount() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        AccountDto newAccountDto = anAccountToBeAdded();
        ResponseEntity<String> response = apiWrapper.addNewAccountForAdmin(port, bearerToken, newAccountDto);

        Map responseAccount = mapper.readValue(response.getBody(), Map.class);

        Account savedAccount = accountRepository.findByEmail(responseAccount.get("email").toString());
        assertEquals(responseAccount.get("email"), savedAccount.getEmail());
    }

    @Test
    void updateAccount() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));

        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        AccountDto newAccountDto = anAccountToBeAdded();
        ResponseEntity<String> response = apiWrapper.addNewAccountForAdmin(port, bearerToken, newAccountDto);

        Map responseAccount = mapper.readValue(response.getBody(), Map.class);
        Account savedAccount = accountRepository.findByEmail(responseAccount.get("email").toString());
        assertEquals(responseAccount.get("email"), savedAccount.getEmail());
        assertEquals(responseAccount.get("isAdmin"), false);

        //update account to be admin
        AccountForAdminDto accountUpdateData = AccountForAdminDto.builder().id(savedAccount.getId()).isAdmin(true).build();
        ResponseEntity<String> updateAccountResponse = apiWrapper.updateAccount(port, bearerToken, accountUpdateData);
        Account updatedAccount = accountRepository.findByEmail(responseAccount.get("email").toString());
        Map updateAccountResponseMap = mapper.readValue(updateAccountResponse.getBody(), Map.class);
        assertEquals(updateAccountResponseMap.get("email"), updatedAccount.getEmail());
        assertEquals(updateAccountResponseMap.get("isAdmin"), true);

        //update account, remove admin role
        accountUpdateData = AccountForAdminDto.builder().id(savedAccount.getId()).isAdmin(false).build();
        updateAccountResponse = apiWrapper.updateAccount(port, bearerToken, accountUpdateData);
        updatedAccount = accountRepository.findByEmail(responseAccount.get("email").toString());
        updateAccountResponseMap = mapper.readValue(updateAccountResponse.getBody(), Map.class);
        assertEquals(updateAccountResponseMap.get("isAdmin"), false);

        //update account, deactivate account
        accountUpdateData = AccountForAdminDto.builder().id(savedAccount.getId()).isActive(false).build();
        updateAccountResponse = apiWrapper.updateAccount(port, bearerToken, accountUpdateData);
        updateAccountResponseMap = mapper.readValue(updateAccountResponse.getBody(), Map.class);
        assertEquals(updateAccountResponseMap.get("isActive"), false);

        //update account, reactivate account
        accountUpdateData = AccountForAdminDto.builder().id(savedAccount.getId()).isActive(true).build();
        updateAccountResponse = apiWrapper.updateAccount(port, bearerToken, accountUpdateData);
        updateAccountResponseMap = mapper.readValue(updateAccountResponse.getBody(), Map.class);
        assertEquals(updateAccountResponseMap.get("isActive"), true);
    }

    @Test
    void addNewEvent() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
//        account.setSkill(50);
        accountRepository.save(account);
        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithUsername(username1, 11L,  passwordEncoder.encode("password"));
        account1.setSkill(51);
        accountRepository.save(account1);

        String username2 = RandomStringUtils.random(10, true, false);
        Account account2 = Utils.aTestAccountWithUsername(username2, 12L,  passwordEncoder.encode("password"));
        account2.setSkill(52);
        accountRepository.save(account2);

        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        CreateEventDto createEventDto = Utils.aCreateEventDto(Arrays.asList(username1,username2));
        ResponseEntity<String> response = apiWrapper.addNewEvent(port, bearerToken, createEventDto);

        Map responseEvent = mapper.readValue(response.getBody(), Map.class);

        Event savedEvent = eventRepository.findById(Long.parseLong(responseEvent.get("id").toString())).get();
        assertEquals(Long.valueOf(responseEvent.get("id").toString()), savedEvent.getId());
        assertEquals("INITIATED", savedEvent.getStatus().name());

    }

    @Test
    void getListOfEventsForAdminPage() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithUsername(username1, 31L, passwordEncoder.encode("password"));
        account1.setSkill(61);
        accountRepository.save(account1);

        String username2 = RandomStringUtils.random(10, true, false);

        Account account2 = Utils.aTestAccountWithUsername(username2, 32L, passwordEncoder.encode("password"));
        account2.setSkill(62);
        accountRepository.save(account2);

        String username3 = RandomStringUtils.random(10, true, false);
        Account account3 = Utils.aTestAccountWithUsername(username3, 33L, passwordEncoder.encode("password"));
        account3.setSkill(63);
        accountRepository.save(account3);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        CreateEventDto createEventDto1 = Utils.aCreateEventDto(Arrays.asList(username1,username2));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto1);

        CreateEventDto createEventDto2 = Utils.aCreateEventDto(Arrays.asList(username1,username3));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto2);

        CreateEventDto createEventDto3 = Utils.aCreateEventDto(Arrays.asList(username2,username3));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto3);

        ResponseEntity<String> response = apiWrapper.getEventsForAdmin(port, bearerToken);
        List<EventResponseDto> eventForAdminDtoList =
                mapper.readValue(response.getBody(), new TypeReference<>() {
                } );

        assertFalse(eventForAdminDtoList.isEmpty());
        assertEquals("INITIATED", eventForAdminDtoList.get(0).getStatus().name());
        assertFalse(eventForAdminDtoList.get(0).getRegisteredPlayers().isEmpty());
    }

    private AccountDto anAccountToBeAdded() {
        String username = RandomStringUtils.random(10, true, false);
        return AccountDto.builder().username(username)
                .email(username+"@mail.com")
                .password("password")
                .confirmPassword("password")
                .skill(65)
                .build();
    }
}
