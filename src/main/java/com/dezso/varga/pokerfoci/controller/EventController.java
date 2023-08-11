package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("event")
@AllArgsConstructor
public class EventController {

    private AdminService adminService;

    @Secured( "ROLE_ADMIN" )
    @PostMapping("/event")
    public EventResponseDto addEvent(@RequestBody CreateEventDto newEventDtoRequest) throws Exception{
        return adminService.createEvent(newEventDtoRequest);
    }

    @Secured( "ROLE_ADMIN" )
    @GetMapping("/events")
    public List<EventResponseDto> listEvents() {

        return adminService.listEvents();
    }
}
