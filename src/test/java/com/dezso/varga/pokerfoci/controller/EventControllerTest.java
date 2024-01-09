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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EventControllerTest extends BaseControllerTest {

    @Test
    void getLatestEvent() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername(8L,"ROLE_ADMIN", username, passwordEncoder.encode("password"));
        accountRepository.save(account);

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithUsername(username1, 41L, passwordEncoder.encode("password"));
        account1.setSkill(61);
        accountRepository.save(account1);

        String username2 = RandomStringUtils.random(10, true, false);

        Account account2 = Utils.aTestAccountWithUsername(username2, 42L, passwordEncoder.encode("password"));
        account2.setSkill(62);
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
    }

    @Test
    void registerToLatestEvent() throws Exception {
        String username = RandomStringUtils.random(10, true, false);

        Account account = Utils.aTestAccountWithRoleAndUsername(9L,"ROLE_ADMIN", username, passwordEncoder.encode("password"));
        account.setSkill(70);
        accountRepository.save(account);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithUsername(username1, 51L, passwordEncoder.encode("password"));
        account1.setSkill(61);
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
    }
}
