package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventConverterImpl implements EventConverter {

    private final AccountConverter accountConverter;
    private final EventRepository eventRepository;

    @Override
    public Event fromCreateEventDtoToEvent(CreateEventDto createEventDto) {

        return Event.builder()
                .date(createEventDto.getEventDate())
                .registeredPlayers(accountConverter.fromAccountNameListToAccountList(createEventDto.getRegisteredPlayers()))
                .build();
    }

    @Override
    public EventResponseDto fromEventToEventResponseDto(Event event) {
        return EventResponseDto.builder()
                .id(event.getId())
                .eventDate(event.getDate())
                .status(event.getStatus())
                .registeredPlayers(accountConverter.fromAccountListToAccountDtoList(event.getRegisteredPlayers()))
                .build();
    }
}
