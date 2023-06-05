package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.services.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("admin")
@AllArgsConstructor
@Secured( "ROLE_ADMIN" )
public class AdminController {

    private AdminService adminService;

    @GetMapping("/accounts")
    public List<AccountForAdminDto> listAccounts() {
        return adminService.listAccounts();
    }

    @PostMapping("/account")
    public AccountForAdminDto addAccount(@RequestBody AccountDto newAccountDtoRequest) {
        return adminService.addNewAccount(newAccountDtoRequest);
    }

    @PutMapping("/account")
    public AccountForAdminDto updateAccount(@RequestBody AccountForAdminDto updateAccountDtoRequest) throws Exception{

        return adminService.updateAccount(updateAccountDtoRequest);
    }

    @PostMapping("/event")
    public EventResponseDto addEvent(@RequestBody CreateEventDto newEventDtoRequest) throws Exception{
        return adminService.createEvent(newEventDtoRequest);
    }

    @GetMapping("/events")
    public List<EventResponseDto> listEvents() {

        return adminService.listEvents();
    }
}
