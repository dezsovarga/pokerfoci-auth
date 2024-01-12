package com.dezso.varga.pokerfoci.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountWithSkillDto {

    private String username;
    private int skill;
    private LocalDateTime registrationDate;
}
