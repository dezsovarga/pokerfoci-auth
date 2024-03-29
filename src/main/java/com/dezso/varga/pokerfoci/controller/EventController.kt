package com.dezso.varga.pokerfoci.controller

import com.dezso.varga.pokerfoci.authentication.utils.AuthUtils
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto
import com.dezso.varga.pokerfoci.services.EventService
import com.dezso.varga.pokerfoci.services.AdminService
import com.dezso.varga.pokerfoci.dto.EventResponseDto
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping

import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
class EventController(
    private val adminService: AdminService,
    private val eventService: EventService
) {

    @Secured("ROLE_ADMIN")
    @PostMapping("/event")
    fun addEvent(@RequestHeader("Authorization") authHeader: String,
                 @RequestBody newEventDtoRequest: CreateEventDto): EventResponseDto {
        val token = AuthUtils.extractTokenFromBearerToken(authHeader)
        val userEmail = AuthUtils.getAccountEmailFromBearerToken(token)
        return adminService.createEvent(newEventDtoRequest, userEmail)
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/event")
    fun updateEvent(@RequestHeader("Authorization") authHeader: String,
                 @RequestBody eventDtoRequest: CreateEventDto): EventResponseDto {
        val token = AuthUtils.extractTokenFromBearerToken(authHeader)
        val userEmail = AuthUtils.getAccountEmailFromBearerToken(token)
        return adminService.updateEvent(eventDtoRequest, userEmail)
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("/events")
    fun listEvents() : List<EventResponseDto> {
        return adminService.listEvents()
    }

    @GetMapping("/latest")
    fun getLatestEvent(): EventResponseDto {
        return eventService.latestEvent
    }

    @PostMapping("/register")
    fun registerToLatestEvent(@RequestHeader("Authorization") authHeader: String ) : EventResponseDto {

        val token = AuthUtils.extractTokenFromBearerToken(authHeader)
        val userEmail = AuthUtils.getAccountEmailFromBearerToken(token)
        return eventService.registerToLatestEvent(userEmail)
    }

    @PostMapping("/unregister")
    fun unRegisterFromLatestEvent(@RequestHeader("Authorization") authHeader: String ) : EventResponseDto {

        val token = AuthUtils.extractTokenFromBearerToken(authHeader)
        val userEmail = AuthUtils.getAccountEmailFromBearerToken(token)
        return eventService.unRegisterFromLatestEvent(userEmail)
    }

}