package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.AccountConverter;
import com.dezso.varga.pokerfoci.converters.EventConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventHistory;
import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.exeptions.GlobalException;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import com.dezso.varga.pokerfoci.repository.EventHistoryRepository;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import com.dezso.varga.pokerfoci.repository.ParticipationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private AccountRepository accountRepository;
    private AccountConverter accountConverter;
    private EventConverter eventConverter;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;
    private final EventHistoryRepository eventHistoryRepository;

    private static final Logger LOG = getLogger(AdminServiceImpl.class);

    @Override
    public List<AccountForAdminDto> listAccounts() {
        List<Account> allAccounts = accountRepository.findAll();
        return accountConverter.fromAccountListToAccountForAdminDtoList(allAccounts);
    }

    @Override
    public AccountForAdminDto addNewAccount(AccountDto newAccountDtoRequest) {
        //TODO: add validation
        Account account = accountConverter.fromAddNewAccountDtoToAccount(newAccountDtoRequest);
        Account savedAccount = accountRepository.save(account);
        return accountConverter.fromAccountToAccountForAdminDto(savedAccount);
    }

    @Override
    public AccountForAdminDto updateAccount(AccountForAdminDto updateAccountDtoRequest) throws Exception{
        if (updateAccountDtoRequest.getId() == null) {
            throw new GlobalException("Account id cannot be null for update account", HttpStatus.PRECONDITION_FAILED.value());
        }
        Optional<Account> existingAccount = accountRepository.findById(updateAccountDtoRequest.getId());
        if (existingAccount.isEmpty()) {
            throw new GlobalException("Invalid account for update", HttpStatus.PRECONDITION_FAILED.value());
        }
        Account updatedAccount = accountConverter.fromUpdateAccountDtoToAccount(updateAccountDtoRequest, existingAccount.get());
        updatedAccount = accountRepository.save(updatedAccount);
        return accountConverter.fromAccountToAccountForAdminDto(updatedAccount);
    }

    @Override
    public EventResponseDto createEvent(CreateEventDto createEventDto, String userEmail) throws Exception {
        if (createEventDto.getEventDateEpoch() == null) {
            throw new GlobalException("Event date cannot be null", HttpStatus.PRECONDITION_FAILED.value());
        }
        EventHistory eventHistory = EventHistory.builder()
                .historyTime(LocalDateTime.now())
                .historyMessage(userEmail + " created a new event")
                .build();
        eventHistoryRepository.save(eventHistory);
        Event event = eventConverter.fromCreateEventDtoToEvent(createEventDto);
        event.getParticipationList().forEach(eventParticipation -> participationRepository.save(eventParticipation));
        event.setStatus(EventStatus.INITIATED);
        event.getEventHistoryList().add(eventHistory);
        eventRepository.save(event);
        return eventConverter.fromEventToEventResponseDto(event);
    }

    @Override
    public List<EventResponseDto> listEvents() {
        List<Event> allEvents = eventRepository.findAll();
        List<EventResponseDto> dtoList = eventConverter.fromEventListToEventResponseDtoList(allEvents);
        Collections.sort(dtoList, Comparator.comparing(EventResponseDto::getEventDateTime).reversed());
        return dtoList;
    }
}
