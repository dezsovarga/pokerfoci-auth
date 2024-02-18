package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventLog;
import com.dezso.varga.pokerfoci.dto.EventHistoryDto;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountWithSkillDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventConverterImpl implements EventConverter {

    private final AccountConverter accountConverter;
    private final EventRepository eventRepository;

    @Override
    public Event fromCreateEventDtoToEvent(CreateEventDto createEventDto) {

        LocalDateTime eventDateTime = Instant.ofEpochMilli(createEventDto.getEventDateEpoch())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Event.builder()
                .date(eventDateTime)
                .participationList(accountConverter.fromAccountNameListToEventParticipationList(createEventDto.getRegisteredPlayers()))
                .eventLogList(new ArrayList<>())
                .build();
    }

    @Override
    public EventResponseDto fromEventToEventResponseDto(Event event) {
        List<AccountWithSkillDto> registeredPlayers = accountConverter.fromParticipationListToAccountWithSkillDtoList(event.getParticipationList());
        for (AccountWithSkillDto participant: registeredPlayers) {
            if (participant.getRegistrationDate() == null) {
                participant.setRegistrationDate(LocalDateTime.now());
            }
        }
        registeredPlayers.sort(Comparator.comparing(AccountWithSkillDto::getRegistrationDate));

        List<EventHistoryDto> eventHistoryDto = this.fromEventHistoryListToEventHistoryDtoList(event.getEventLogList());

        return EventResponseDto.builder()
                .id(event.getId())
                .eventDateTime(event.getDate())
                .status(event.getStatus())
                .registeredPlayers(registeredPlayers)
                .eventHistory(eventHistoryDto)
                .build();
    }

    @Override
    public List<EventResponseDto> fromEventListToEventResponseDtoList(List<Event> eventList) {

        return eventList.stream().map(this::fromEventToEventResponseDto).collect(Collectors.toList());
    }

    @Override
    public EventHistoryDto fromEventHistoryToEventHistoryDto(EventLog eventHistory) {
        return EventHistoryDto.builder()
                .historyMessage(eventHistory.getLogMessage())
                .historyTime(eventHistory.getLogTime())
                .build();
    }

    @Override
    public List<EventHistoryDto> fromEventHistoryListToEventHistoryDtoList(List<EventLog> eventHistoryList) {
        return eventHistoryList.stream().map(this::fromEventHistoryToEventHistoryDto).collect(Collectors.toList());
    }
}
