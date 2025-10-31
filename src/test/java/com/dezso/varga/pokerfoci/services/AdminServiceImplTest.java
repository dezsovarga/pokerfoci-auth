package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.AccountConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.domain.Participation;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import com.dezso.varga.pokerfoci.repository.EventRepository;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AdminServiceImplTest {

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private EventRepository eventRepository;

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
                "email",
                "password",
                true,
                Set.of(new Role( "ROLE_ADMIN")));
        Account anotherAccount = new Account(2L,
                "username",
                "email",
                "password",
                false,
                Set.of(new Role( "ROLE_USER")));

        Mockito.when(accountRepository.findAll()).thenReturn(List.of(account, anotherAccount));
        List<AccountForAdminDto> accountList = adminService.listAccounts();
        Assert.assertFalse(accountList.isEmpty());
        assertTrue(accountList.get(0).getIsAdmin());
        assertTrue(accountList.get(0).getIsActive());
        assertTrue(accountList.get(0).getId() != 0);

        assertFalse(accountList.get(1).getIsAdmin());
        assertFalse(accountList.get(1).getIsActive());
        assertTrue(accountList.get(1).getId() != 0);
    }

    @Test
    void listEvents() {
        Account account1 = Account.builder().id(1L).skill(60).email("email1").username("user1").build();
        Account account2 = Account.builder().id(2L).skill(65).email("email2").username("user2").build();
        Account account3 = Account.builder().id(3L).skill(62).email("email3").username("user3").build();

        Participation participation1 = Participation.builder().id(1L).account(account1).build();
        Participation participation2 = Participation.builder().id(2L).account(account2).build();
        Participation participation3 = Participation.builder().id(3L).account(account3).build();

        Event event1 = Event.builder()
                .id(1L)
                .date(LocalDateTime.now())
                .status(EventStatus.INITIATED)
                .participationList(Arrays.asList(participation1, participation2))
                .teamVariations(new ArrayList<>())
                .voteList(new ArrayList<>())
                .eventLogList(new ArrayList<>())
                .build();
        Event event2 = Event.builder()
                .id(2L)
                .date(LocalDateTime.now().plusDays(7))
                .status(EventStatus.INITIATED)
                .participationList(Arrays.asList(participation1, participation3))
                .teamVariations(new ArrayList<>())
                .voteList(new ArrayList<>())
                .eventLogList(new ArrayList<>())
                .build();

        Mockito.when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        List<EventResponseDto> eventList = adminService.listEvents();
        Assert.assertFalse(eventList.isEmpty());
        assertEquals(EventStatus.INITIATED, eventList.get(0).getStatus());
        List<String> firstEventRegisteredPlayers =
                eventList.get(0).getRegisteredPlayers().stream().map(AccountWithSkillDto::getUsername).collect(Collectors.toList());
        assertEquals(Arrays.asList("user1", "user3"),firstEventRegisteredPlayers);

        assertEquals(EventStatus.INITIATED, eventList.get(1).getStatus());
        List<String> secondEventRegisteredPlayers =
                eventList.get(1).getRegisteredPlayers().stream().map(AccountWithSkillDto::getUsername).collect(Collectors.toList());
        assertEquals(Arrays.asList("user1", "user2"),secondEventRegisteredPlayers);
    }
}