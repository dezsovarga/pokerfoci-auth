package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AddNewAccountDto;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

public class AdminControllerTest extends BaseControllerTest {

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    public void getListOfAccountsForAdminPage() throws Exception {
        Account account = aTestAccount("ROLE_ADMIN");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");
        ResponseEntity<String> response = apiWrapper.getAccountsForAdmin(port, bearerToken);

        List<AccountForAdminDto> accountForAdminDtoList =
                mapper.readValue(response.getBody(), new TypeReference<>(){});

        assertFalse(accountForAdminDtoList.isEmpty());
        assertEquals(account.getEmail(), accountForAdminDtoList.get(0).getEmail());
        assertEquals(account.getUsername(), accountForAdminDtoList.get(0).getUsername());
        assertEquals(account.isActive(), accountForAdminDtoList.get(0).isActive());
        assertTrue(accountForAdminDtoList.get(0).isAdmin());
    }

    @Test
    public void getListOfAccountsForAdminPageWithNoAdminRole() throws Exception {
        Account account = aTestAccount("ROLE_USER");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");
        ResponseEntity<String> response = apiWrapper.getAccountsForAdmin(port, bearerToken);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void addNewAccount() throws Exception {
        Account account = aTestAccount("ROLE_ADMIN");
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( "email@varga.com","password");

        AddNewAccountDto newAccountDto = anAccountToBeAdded();
        ResponseEntity<String> response = apiWrapper.addNewAccountForAdmin(port, bearerToken, newAccountDto);

        Map responseAccount = mapper.readValue(response.getBody(), Map.class);

        Account savedAccount = accountRepository.findByEmail(responseAccount.get("email").toString());
        assertEquals(responseAccount.get("email"), savedAccount.getEmail());
    }

    private Account aTestAccount(String role) {
        return new Account(1L,
                "username",
                "email@varga.com",
                passwordEncoder.encode("password"),
                true,
                Set.of(new Role( role)));
    }

    private AddNewAccountDto anAccountToBeAdded() {
        return AddNewAccountDto.builder().username("username")
                .email("email@mail.com")
                .password("password")
                .confirmPassword("password")
                .skill(65)
                .build();
    }
}
