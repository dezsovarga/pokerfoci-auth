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

import static org.junit.Assert.assertNotNull;

public class EventControllerTest extends BaseControllerTest {

    @Test
    void getListOfEventsForAdminPage() throws Exception {
        Account account = Utils.aTestAccountWithRole("ROLE_ADMIN", passwordEncoder.encode("password"));
        accountRepository.save(account);

        String username1 = RandomStringUtils.random(10, true, false);
        Account account1 = Utils.aTestAccountWithUsername(username1, 31L, passwordEncoder.encode("password"));
        account1.setSkill(61);
        accountRepository.save(account1);

        String username2 = RandomStringUtils.random(10, true, false);

        Account account2 = Utils.aTestAccountWithUsername(username2, 32L, passwordEncoder.encode("password"));
        account2.setSkill(62);
        accountRepository.save(account2);

        String bearerToken = this.generateBearerToken( "email@varga.com","password");

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
}
