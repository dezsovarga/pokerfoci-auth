package com.dezso.varga.pokerfoci.utils;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;

public class Utils {

    public static CreateEventDto aCreateEventDto(List<String> registeredPlayers) {
        ZoneId zoneId = ZoneId.systemDefault(); // or: ZoneId.of("Europe/Oslo");
        long epoch = LocalDateTime.now().atZone(zoneId).toEpochSecond()*1000;
        return CreateEventDto.builder()
                .eventDateEpoch(epoch)
                .registeredPlayers(registeredPlayers)
                .build();
    }

    public static CreateEventDto aCreateEventDto(List<String> registeredPlayers, long epoch) {
        return CreateEventDto.builder()
                .eventDateEpoch(epoch)
                .registeredPlayers(registeredPlayers)
                .build();
    }

    public static Account aTestAccountWithRole(String role, String password) {
        return new Account(1L,
                "username",
                "email@varga.com",
                password,
                true,
                Set.of(new Role( role)));
    }

    public static Account aTestAccountWithUsername(String username, Long id, String password) {
        return new Account(id,
                username,
                username+"@varga.com",
                password,
                true,
                Set.of(new Role( "ROLE_USER")));
    }
}
