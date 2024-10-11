package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

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
        assertEquals(account.getEmail() + " created a new event", eventResponseDto.getEventLogs().get(0).getLogMessage());
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
        assertEquals(2, savedLatestEventResponseDto.getEventLogs().size());
        assertEquals(account.getUsername() + " registered to the event", savedLatestEventResponseDto.getEventLogs().get(1).getLogMessage());

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

        Assert.assertEquals(latestEventResponseDto.getRegisteredPlayers().size(), createEventDto1.getRegisteredPlayers().size());
        assertEquals(3, latestEventResponseDto.getEventLogs().size());
        assertEquals(account.getUsername() + " unregistered from the event", latestEventResponseDto.getEventLogs().get(2).getLogMessage());

//        trying to unregister again with same user
        ResponseEntity<String> response = apiWrapper.unRegisterFromLatestEvent(port, bearerToken);
        Map<String, String> responseMap = mapper.readValue(response.getBody(), Map.class);
        assertEquals("User not registered to the latest event", responseMap.get("reason"));
    }

    @Test
    void updateEventPlayers() throws Exception {
        Account account = createTestAccount(
                "ROLE_ADMIN", RandomStringUtils.random(10, true, false), 70);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");

        Account account1 = createTestAccount(
                "ROLE_USER", RandomStringUtils.random(10, true, false), 73);
        Account account2 = createTestAccount(
                "ROLE_USER", RandomStringUtils.random(10, true, false), 74);
        Account account3 = createTestAccount(
                "ROLE_USER", RandomStringUtils.random(10, true, false), 65);

        CreateEventDto createEventDto = Utils.aCreateEventDto(Arrays.asList(account1.getUsername(), account2.getUsername()));
        apiWrapper.addNewEvent(port, bearerToken, createEventDto);

        CreateEventDto updateEventDto = Utils.aCreateEventDto(
                Arrays.asList(account2.getUsername(), account3.getUsername())
        );
        apiWrapper.updateEvent(port, bearerToken, updateEventDto);

        ResponseEntity<String> savedResponse = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto savedLatestEventResponseDto = mapper.readValue(savedResponse.getBody(), new TypeReference<>() {} );
        Assert.assertEquals(2, savedLatestEventResponseDto.getRegisteredPlayers().size());
        Assert.assertEquals(account2.getUsername(), savedLatestEventResponseDto.getRegisteredPlayers().get(0).getUsername());
        Assert.assertEquals(account3.getUsername(), savedLatestEventResponseDto.getRegisteredPlayers().get(1).getUsername());
        assertEquals(account.getUsername() + " updated the players list from the latest event", savedLatestEventResponseDto.getEventLogs().get(1).getLogMessage());
    }

    @Test
    void generateTeamVariations() throws Exception {
        Account account = createTestAccount(
                "ROLE_ADMIN", RandomStringUtils.random(10, true, false), 70);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");
        CreateEventDto createEventDto = this.createEventWith12Players();
        apiWrapper.addNewEvent(port, bearerToken, createEventDto);

        apiWrapper.generateTeamVariations(port, bearerToken);

        ResponseEntity<String> savedResponse = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto savedLatestEventResponseDto = mapper.readValue(savedResponse.getBody(), new TypeReference<>() {} );
        Assert.assertEquals(10, savedLatestEventResponseDto.getTeamVariations().size());

        //generate teams again
        apiWrapper.generateTeamVariations(port, bearerToken);

        ResponseEntity<String> savedResponseAgain = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto savedLatestEventResponseDtoAgain = mapper.readValue(savedResponseAgain.getBody(), new TypeReference<>() {} );
        //assert that team variations were replaced instead of adding them again in the list
        Assert.assertEquals(10, savedLatestEventResponseDtoAgain.getTeamVariations().size());
    }

    @Test
    void selectTeamVariationsForVoting() throws Exception {
        Account account = createTestAccount(
                "ROLE_ADMIN", RandomStringUtils.random(10, true, false), 70);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");
        CreateEventDto createEventDto = this.createEventWith12Players();
        apiWrapper.addNewEvent(port, bearerToken, createEventDto);

        apiWrapper.generateTeamVariations(port, bearerToken);

        ResponseEntity<String> savedResponse = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto savedLatestEventResponseDto = mapper.readValue(savedResponse.getBody(), new TypeReference<>() {} );
        Assert.assertEquals(10, savedLatestEventResponseDto.getTeamVariations().size());

        List<Long> variationIdsToUpdate = List.of(savedLatestEventResponseDto.getTeamVariations().get(0).getVariationId(),
                savedLatestEventResponseDto.getTeamVariations().get(1).getVariationId(),
                savedLatestEventResponseDto.getTeamVariations().get(2).getVariationId());

        savedResponse = apiWrapper.updateTeamVariationsSelection(port, bearerToken, variationIdsToUpdate);
        savedLatestEventResponseDto = mapper.readValue(savedResponse.getBody(), new TypeReference<>() {} );

        Assert.assertTrue(savedLatestEventResponseDto.getTeamVariations().get(0).isSelectedForVoting());
        Assert.assertTrue(savedLatestEventResponseDto.getTeamVariations().get(1).isSelectedForVoting());
        Assert.assertTrue(savedLatestEventResponseDto.getTeamVariations().get(2).isSelectedForVoting());
    }

    @Test
    void generateTeamVariationsThenUpdatePlayersList() throws Exception {
        Account account = createTestAccount(
                "ROLE_ADMIN", RandomStringUtils.random(10, true, false), 70);
        String bearerToken = this.generateBearerToken( account.getEmail(),"password");
        CreateEventDto createEventDto = this.createEventWith12Players();
        apiWrapper.addNewEvent(port, bearerToken, createEventDto);

        apiWrapper.generateTeamVariations(port, bearerToken);

        CreateEventDto updateEventDto = Utils.aCreateEventDto(
                Arrays.asList(createTestAccount(
                        "ROLE_USER", "Csabesz", 85).getUsername())
        );
        apiWrapper.updateEvent(port, bearerToken, updateEventDto);

        ResponseEntity<String> savedResponse = apiWrapper.getLatestEvent(port, bearerToken);
        EventResponseDto savedLatestEventResponseDto = mapper.readValue(savedResponse.getBody(), new TypeReference<>() {} );
        Assert.assertEquals(0, savedLatestEventResponseDto.getTeamVariations().size());

    }

    private CreateEventDto createEventWith12Players() {

        Account account1 = createTestAccount(
                "ROLE_USER", "Csabesz", 85);
        Account account2 = createTestAccount(
                "ROLE_USER", "dezsovarga", 64);
        Account account3 = createTestAccount(
                "ROLE_USER", "Dragos", 58);
        Account account4 = createTestAccount(
                "ROLE_USER", "horvathkuki", 73);
        Account account5 = createTestAccount(
                "ROLE_USER", "kuplung", 72);
        Account account6 = createTestAccount(
                "ROLE_USER", "pistike", 80);
        Account account7 = createTestAccount(
                "ROLE_USER", "szloszlo", 76);
        Account account8 = createTestAccount(
                "ROLE_USER", "szury", 34);
        Account account9 = createTestAccount(
                "ROLE_USER", "atarr", 76);
        Account account10 = createTestAccount(
                "ROLE_USER", "mikloszsolt", 74);
        Account account11 = createTestAccount(
                "ROLE_USER", "orban", 75);
        Account account12 = createTestAccount(
                "ROLE_USER", "vinitor", 68);

        return Utils.aCreateEventDto(
                Arrays.asList(
                        account1.getUsername(),
                        account2.getUsername(),
                        account3.getUsername(),
                        account4.getUsername(),
                        account5.getUsername(),
                        account6.getUsername(),
                        account7.getUsername(),
                        account8.getUsername(),
                        account9.getUsername(),
                        account10.getUsername(),
                        account11.getUsername(),
                        account12.getUsername()
                )
        );
    }

    private Account createTestAccount(String role, String username, int skill) {
        Account account = new Account(
                username,
                username+"@"+role+".com",
                passwordEncoder.encode("password"),
                skill,
                true,
                Set.of(new Role( role)));
        accountRepository.save(account);
        return account;
    }

}
