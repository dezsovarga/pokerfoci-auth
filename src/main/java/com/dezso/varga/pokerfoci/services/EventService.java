package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.dto.EventResponseDto;


public interface EventService {

    EventResponseDto getLatestEvent();

    EventResponseDto registerToLatestEvent(String userEmail) throws Exception;

    EventResponseDto unRegisterFromLatestEvent(String userEmail) throws Exception;
}
