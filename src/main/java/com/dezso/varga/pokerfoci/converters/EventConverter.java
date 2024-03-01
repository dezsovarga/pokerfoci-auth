package com.dezso.varga.pokerfoci.converters;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventLog;
import com.dezso.varga.pokerfoci.dto.EventLogsDto;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;

import java.util.List;

public interface EventConverter {

    Event fromCreateEventDtoToEvent(CreateEventDto createEventDto);
    EventResponseDto fromEventToEventResponseDto(Event event);
    List<EventResponseDto> fromEventListToEventResponseDtoList(List<Event> eventList);
    EventLogsDto fromEventLogsToEventLogsDto(EventLog evenLogs);
    List<EventLogsDto> fromEventLogsListToEventLogsDtoList(List<EventLog> eventLogsList);

}
