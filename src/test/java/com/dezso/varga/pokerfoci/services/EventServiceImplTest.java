package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.PokerfociAuthMain;
import com.dezso.varga.pokerfoci.converters.EventConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import com.dezso.varga.pokerfoci.repository.ParticipationRepository;
import com.dezso.varga.pokerfoci.utils.Utils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PokerfociAuthMain.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class EventServiceImplTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ParticipationRepository participationRepository;

    @Autowired
    private EventConverter eventConverter;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @Transactional
    void getLatestEvent() {

        Account account1 = Utils.aTestAccountWithUsername("szury", 21L, "password");
        accountRepository.save(account1);
        Account account2 = Utils.aTestAccountWithUsername("dezsovarga", 22L, "password");
        accountRepository.save(account2);
        Account account3 = Utils.aTestAccountWithUsername("csabesz", 23L, "password");
        accountRepository.save(account3);

        CreateEventDto createEventDto1 = Utils.aCreateEventDto(Arrays.asList("szury","dezsovarga"));
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = LocalDateTime.now().plusDays(7).atZone(zoneId).toEpochSecond()*1000;
        CreateEventDto createEventDto2 = Utils.aCreateEventDto(Arrays.asList("szury","csabesz"), epoch);

        addEvent(createEventDto1);
        addEvent(createEventDto2);

        Event event = eventRepository.findLatestEvent();
        assertNotNull(event);
        assertEquals(event.getDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), epoch);
    }

    private void addEvent(CreateEventDto createEventDto) {
        Event event = eventConverter.fromCreateEventDtoToEvent(createEventDto);
        event.getParticipationList().forEach(eventParticipation -> participationRepository.save(eventParticipation));
        event.setStatus(EventStatus.INITIATED);
        eventRepository.save(event);
    }

}