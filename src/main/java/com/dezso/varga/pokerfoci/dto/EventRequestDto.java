package com.dezso.varga.pokerfoci.dto;

import com.dezso.varga.pokerfoci.domain.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequestDto {

    private LocalDate eventDate;
    private List<String> registeredPlayers;
    private EventStatus status;
    private String score;

}
