package com.dezso.varga.pokerfoci.dto;

import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
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
    private List<AccountDto> registeredPlayers;
    private EventStatus status;
    private String score;
}
