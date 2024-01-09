package com.dezso.varga.pokerfoci.utils;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import org.apache.commons.lang3.RandomStringUtils;

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

    public static Account aTestAccountWithRoleAndUsername(Long id, String role, String username, String password) {
        return new Account(id,
                username,
                username+"@"+role+".com",
                password,
                true,
                Set.of(new Role( role)));
//        return Account.builder()
//                .id(1L)
//                .username(username)
//                .email(username+"@"+role+".com")
//                .password(password)
//                .active(true)
//                .roles(Set.of(new Role( role)))
//                .build();
    }

    public static Account aTestAccountWithUsername(String username, Long id, String password) {
        return new Account(id,
                username,
                username+"@user.com",
                password,
                true,
                Set.of(new Role( "ROLE_USER")));
    }
}
