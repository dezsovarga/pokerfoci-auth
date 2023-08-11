package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

class AdminControllerTest extends BaseControllerTest {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void getListOfAccountsForAdminPage() throws Exception {
        Account account = aTestAccountWithRole("ROLE_ADMIN");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");
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
        Account account = aTestAccountWithRole("ROLE_USER");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");
        ResponseEntity<String> response = apiWrapper.getAccountsForAdmin(port, bearerToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getListOfEventsForAdminPageWithNoAdminRole() throws Exception {
        Account account = aTestAccountWithRole("ROLE_USER");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");
        ResponseEntity<String> response = apiWrapper.getEventsForAdmin(port, bearerToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void addNewAccount() throws Exception {
        Account account = aTestAccountWithRole("ROLE_ADMIN");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");

        AccountDto newAccountDto = anAccountToBeAdded();
        ResponseEntity<String> response = apiWrapper.addNewAccountForAdmin(port, bearerToken, newAccountDto);

        Map responseAccount = mapper.readValue(response.getBody(), Map.class);

        Account savedAccount = accountRepository.findByEmail(responseAccount.get("email").toString());
        assertEquals(responseAccount.get("email"), savedAccount.getEmail());
    }

    @Test
    void updateAccount() throws Exception {
        Account account = aTestAccountWithRole("ROLE_ADMIN");

        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");

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
        Account account = aTestAccountWithRole("ROLE_ADMIN");
        accountRepository.save(account);
        Account account1 = aTestAccountWithUsername("szury", 11L);
        accountRepository.save(account1);
        Account account2 = aTestAccountWithUsername("dezsovarga", 12L);
        accountRepository.save(account2);

        String bearerToken = this.generateBearerToken( "email@varga.com","password");

        CreateEventDto createEventDto = aCreateEventDto(Arrays.asList("szury","dezsovarga"));
        ResponseEntity<String> response = apiWrapper.addNewEvent(port, bearerToken, createEventDto);

        Map responseEvent = mapper.readValue(response.getBody(), Map.class);

        Event savedEvent = eventRepository.findById(Long.parseLong(responseEvent.get("id").toString())).get();
        assertEquals(Long.valueOf(responseEvent.get("id").toString()), savedEvent.getId());
        assertEquals("INITIATED", savedEvent.getStatus().name());

    }

    @Test
    void getListOfEventsForAdminPage() throws Exception {
        Account account = aTestAccountWithRole("ROLE_ADMIN");
        accountRepository.save(account);
        Account account1 = aTestAccountWithUsername("szury", 11L);
        accountRepository.save(account1);
        Account account2 = aTestAccountWithUsername("dezsovarga", 12L);
        accountRepository.save(account2);
        Account account3 = aTestAccountWithUsername("csabesz", 13L);
        accountRepository.save(account3);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");

        CreateEventDto createEventDto1 = aCreateEventDto(Arrays.asList("szury","dezsovarga"));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto1);

        CreateEventDto createEventDto2 = aCreateEventDto(Arrays.asList("szury","csabesz"));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto2);

        CreateEventDto createEventDto3 = aCreateEventDto(Arrays.asList("dezsovarga","csabesz"));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto3);

        ResponseEntity<String> response = apiWrapper.getEventsForAdmin(port, bearerToken);
        List<EventResponseDto> eventForAdminDtoList =
                mapper.readValue(response.getBody(), new TypeReference<>() {
                } );

        assertFalse(eventForAdminDtoList.isEmpty());
        assertEquals("INITIATED", eventForAdminDtoList.get(0).getStatus().name());
        assertFalse(eventForAdminDtoList.get(0).getRegisteredPlayers().isEmpty());
    }

    private CreateEventDto aCreateEventDto(List<String> registeredPlayers) {
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = LocalDateTime.now().atZone(zoneId).toEpochSecond()*1000;
        return CreateEventDto.builder()
                .eventDateEpoch(epoch)
                .registeredPlayers(registeredPlayers)
                .build();
    }

    private Account aTestAccountWithRole(String role) {
        return new Account(1L,
                "username",
                "email@varga.com",
                passwordEncoder.encode("password"),
                true,
                Set.of(new Role( role)));
    }

    private Account aTestAccountWithUsername(String username, Long id) {
        return new Account(id,
                username,
                username+"@varga.com",
                passwordEncoder.encode("password"),
                true,
                Set.of(new Role( "ROLE_USER")));
    }

    private AccountDto anAccountToBeAdded() {
        return AccountDto.builder().username("username")
                .email("email@mail.com")
                .password("password")
                .confirmPassword("password")
                .skill(65)
                .build();
    }
}
