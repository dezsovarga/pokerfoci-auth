package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class EventConverterImplTest {

    @Autowired
    private EventConverter eventConverter;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void fromCreateEventDtoToEvent() {

        Account account1 = aTestAccount("szury", 1L);
        accountRepository.save(account1);
        Account account2 = aTestAccount("dezsovarga", 2L);
        accountRepository.save(account2);

        CreateEventDto createEventDto = aCreateEventDto();
        Event event = eventConverter.fromCreateEventDtoToEvent(createEventDto);

        assertEquals(createEventDto.getEventDate(), event.getDate());
        assertEquals(createEventDto.getRegisteredPlayers().size(), event.getRegisteredPlayers().size());
        assertTrue(event.getRegisteredPlayers().stream().map(player -> player.getUsername())
                .collect(Collectors.toList()).containsAll(Arrays.asList("szury", "dezsovarga")));

    }

    private CreateEventDto aCreateEventDto() {
        return CreateEventDto.builder()
                .eventDate(LocalDate.now())
                .registeredPlayers(Arrays.asList("szury","dezsovarga"))
                .build();
    }

    private Account aTestAccount(String username, Long id) {
        return new Account(id,
                username,
                username+"@varga.com",
                passwordEncoder.encode("password"),
                true,
                Set.of(new Role( "ROLE_USER")));
    }
}