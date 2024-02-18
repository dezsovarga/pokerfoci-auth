package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventLog;
import com.dezso.varga.pokerfoci.dto.EventHistoryDto;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;

import java.util.List;

public interface EventConverter {

    Event fromCreateEventDtoToEvent(CreateEventDto createEventDto);
    EventResponseDto fromEventToEventResponseDto(Event event);
    List<EventResponseDto> fromEventListToEventResponseDtoList(List<Event> eventList);
    EventHistoryDto fromEventHistoryToEventHistoryDto(EventLog eventHistory);
    List<EventHistoryDto> fromEventHistoryListToEventHistoryDtoList(List<EventLog> eventHistoryList);

}
