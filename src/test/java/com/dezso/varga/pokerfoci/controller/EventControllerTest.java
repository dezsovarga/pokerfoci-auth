package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventControllerTest extends BaseControllerTest {

    @Test
    void getLatestEvent() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithRoleAndUsername("ROLE_USER", username1, passwordEncoder.encode("password"));
        accountRepository.save(account1);

        String username2 = RandomStringUtils.random(10, true, false);

        Account account2 = Utils.aTestAccountWithRoleAndUsername("ROLE_USER", username2, passwordEncoder.encode("password"));
        accountRepository.save(account2);

        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        CreateEventDto createEventDto1 = Utils.aCreateEventDto(Arrays.asList(username1,username2));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto1);

        ZoneId zoneId = ZoneId.systemDefault();
        long epoch = LocalDateTime.now().plusDays(7).atZone(zoneId).toEpochSecond()*1000;
        CreateEventDto createEventDto2 = Utils.aCreateEventDto(Arrays.asList(username1,username2), epoch);
        apiWrapper.addNewEvent(port, bearerToken, createEventDto2);

        ResponseEntity<String> response = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto eventResponseDto =
                mapper.readValue(response.getBody(), new TypeReference<>() {
                } );

        assertNotNull(eventResponseDto);
        Assertions.assertEquals(eventResponseDto.getEventDateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), epoch);
        assertEquals(account.getEmail() + " created a new event", eventResponseDto.getEventHistory().get(0).getHistoryMessage());
    }

    @Test
    void registerToLatestEvent() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithRoleAndUsername("ROLE_USER", username1, passwordEncoder.encode("password"));
        accountRepository.save(account1);

        CreateEventDto createEventDto1 = Utils.aCreateEventDto(Collections.singletonList(account1.getUsername()));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto1);

        ResponseEntity<String> response = apiWrapper.registerToLatestEvent(port, bearerToken);
        EventResponseDto eventResponseDto = mapper.readValue(response.getBody(), new TypeReference<>() {} );
        assertNotNull(eventResponseDto);
        assertTrue(eventResponseDto.getRegisteredPlayers().size() > createEventDto1.getRegisteredPlayers().size());

        ResponseEntity<String> savedResponse = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto savedLatestEventResponseDto = mapper.readValue(savedResponse.getBody(), new TypeReference<>() {} );

        assertTrue(savedLatestEventResponseDto.getRegisteredPlayers().size() > createEventDto1.getRegisteredPlayers().size());
        assertEquals(2, savedLatestEventResponseDto.getEventHistory().size());
        assertEquals(account.getUsername() + " registered to the event", savedLatestEventResponseDto.getEventHistory().get(1).getHistoryMessage());

        //trying to register again with same user
        response = apiWrapper.registerToLatestEvent(port, bearerToken);
        Map<String, String> responseMap = mapper.readValue(response.getBody(), Map.class);
        assertEquals("User already registered to the latest event", responseMap.get("reason"));
    }

    @Test
    void unRegisterFromLatestEvent() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername("ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithRoleAndUsername("ROLE_USER", username1, passwordEncoder.encode("password"));
        accountRepository.save(account1);

        CreateEventDto createEventDto1 = Utils.aCreateEventDto(Collections.singletonList(account1.getUsername()));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto1);
        apiWrapper.registerToLatestEvent(port, bearerToken);
        apiWrapper.unRegisterFromLatestEvent(port, bearerToken);

        ResponseEntity<String> latestEventResponse = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto latestEventResponseDto = mapper.readValue(latestEventResponse.getBody(), new TypeReference<>() {} );

        assertTrue(latestEventResponseDto.getRegisteredPlayers().size() == createEventDto1.getRegisteredPlayers().size());
        assertEquals(3, latestEventResponseDto.getEventHistory().size());
        assertEquals(account.getUsername() + " unregistered from the event", latestEventResponseDto.getEventHistory().get(2).getHistoryMessage());


//        trying to unregister again with same user
        ResponseEntity<String> response = apiWrapper.unRegisterFromLatestEvent(port, bearerToken);
        Map<String, String> responseMap = mapper.readValue(response.getBody(), Map.class);
        assertEquals("User not registered to the latest event", responseMap.get("reason"));
    }
}
