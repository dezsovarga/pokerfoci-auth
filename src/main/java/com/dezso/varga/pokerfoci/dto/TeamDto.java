package com.dezso.varga.pokerfoci.dto;

import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamDto {

    private List<AccountWithSkillDto> teamMembers;
    private Integer skillSum;
}
