package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventConverterImpl implements EventConverter {

    private final AccountConverter accountConverter;
    private final EventRepository eventRepository;

    @Override
    public Event fromCreateEventDtoToEvent(CreateEventDto createEventDto) {

        LocalDateTime eventDateTime = Instant.ofEpochSecond(createEventDto.getEventDateEpoch())
                .atZone(ZoneId.systemDefault()).toLocalDateTime();
        return Event.builder()
                .date(eventDateTime)
                .participationList(accountConverter.fromAccountNameListToEventParticipationList(createEventDto.getRegisteredPlayers()))
                .build();
    }

    @Override
    public EventResponseDto fromEventToEventResponseDto(Event event) {
        List<Account> registeredPlayers = event.getParticipationList().stream().map(eventParticipation -> eventParticipation.getAccount()).collect(Collectors.toList());
        return EventResponseDto.builder()
                .id(event.getId())
                .eventDateTime(event.getDate())
                .status(event.getStatus())
                .registeredPlayers(accountConverter.fromAccountListToAccountDtoList(registeredPlayers))
                .build();
    }

    @Override
    public List<EventResponseDto> fromEventListToEventResponseDtoList(List<Event> eventList) {

        return eventList.stream().map(event -> fromEventToEventResponseDto(event)).collect(Collectors.toList());
    }
}
