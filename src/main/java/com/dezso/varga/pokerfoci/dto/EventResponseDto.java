package com.dezso.varga.pokerfoci.dto;

import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponseDto {

    private Long id;
    private LocalDateTime eventDateTime;
    private List<AccountWithSkillDto> registeredPlayers;
    private List<EventHistoryDto> eventHistory;
    private EventStatus status;
    private String score;
}
