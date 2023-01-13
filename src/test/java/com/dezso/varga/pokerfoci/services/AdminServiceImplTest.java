package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.AccountConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AdminServiceImplTest {

    @MockBean
    private AccountRepository accountRepository;

    @Autowired
    private AccountConverter accountConverter;

    @Autowired
    private AdminService adminService;
    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void listAccounts() {
        Account account = new Account(1L,
                "username",
                "firstname",
                "lastName",
                "email",
                "password",
                true,
                Set.of(new Role( "ROLE_ADMIN")));
        Account anotherAccount = new Account(2L,
                "username",
                "firstname",
                "lastName",
                "email",
                "password",
                false,
                Set.of(new Role( "ROLE_USER")));

        Mockito.when(accountRepository.findAll()).thenReturn(List.of(account, anotherAccount));
        List<AccountForAdminDto> accountList = adminService.listAccounts();
        Assert.assertFalse(accountList.isEmpty());
        assertTrue(accountList.get(0).isAdmin());
        assertTrue(accountList.get(0).isActive());
        assertTrue(accountList.get(0).getId() != 0);

        assertFalse(accountList.get(1).isAdmin());
        assertFalse(accountList.get(1).isActive());
        assertTrue(accountList.get(1).getId() != 0);


    }
}