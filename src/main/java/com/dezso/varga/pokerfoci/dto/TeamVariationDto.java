package com.dezso.varga.pokerfoci.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamVariationDto {

    private TeamDto team1;
    private TeamDto team2;
    double skillDifference;
}
