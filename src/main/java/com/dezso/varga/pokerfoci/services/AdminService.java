package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;

import java.util.List;

public interface AdminService {

    List<AccountForAdminDto> listAccounts();
    AccountForAdminDto addNewAccount(AccountDto newAccountDtoRequest);
    AccountForAdminDto updateAccount(AccountForAdminDto updateAccountDtoRequest) throws Exception;
    EventResponseDto createEvent(CreateEventDto createEventDto, String userEmail) throws Exception;
    EventResponseDto updateEvent(CreateEventDto eventDto, String userEmail) throws Exception;
    List<EventResponseDto> listEvents();

}
