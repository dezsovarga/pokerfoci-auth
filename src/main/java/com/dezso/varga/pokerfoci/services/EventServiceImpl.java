package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.EventConverter;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventConverter eventConverter;

    @Override
    public EventResponseDto getLatestEvent() {
        Event latestEvent = eventRepository.findLatestEvent();
        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }
}
