package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.AccountConverter;
import com.dezso.varga.pokerfoci.converters.EventConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventLog;
import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.domain.Participation;
import com.dezso.varga.pokerfoci.domain.Team;
import com.dezso.varga.pokerfoci.domain.TeamVariation;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.ValidationResult;
import com.dezso.varga.pokerfoci.dto.admin.AccountForAdminDto;
import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
import com.dezso.varga.pokerfoci.dto.admin.CreateEventDto;
import com.dezso.varga.pokerfoci.exeptions.GlobalException;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import com.dezso.varga.pokerfoci.repository.EventLogRepository;
import com.dezso.varga.pokerfoci.repository.TeamRepository;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import com.dezso.varga.pokerfoci.repository.ParticipationRepository;
import com.dezso.varga.pokerfoci.repository.TeamVariationRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private AccountRepository accountRepository;
    private AccountConverter accountConverter;
    private EventConverter eventConverter;
    private final EventRepository eventRepository;
    private final ParticipationRepository participationRepository;
    private final EventLogRepository eventLogRepository;
    private final ValidatorService validatorService;
    private final TeamGeneratorService teamGeneratorService;
    private final TeamRepository teamRepository;
    private final TeamVariationRepository teamVariationRepository;

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
        EventLog eventLog = new EventLogFactory().build("CREATED", userEmail);
        eventLogRepository.save(eventLog);
        Event event = eventConverter.fromCreateEventDtoToEvent(createEventDto);
        participationRepository.saveAll(event.getParticipationList());
        event.setStatus(EventStatus.INITIATED);
        event.getEventLogList().add(eventLog);
        eventRepository.save(event);
        return eventConverter.fromEventToEventResponseDto(event);
    }

    @Override
    public EventResponseDto updateEvent(CreateEventDto eventDto, String userEmail) throws Exception {
        Event latestEvent = eventRepository.findLatestEvent();
        ValidationResult validationResult = validatorService.validateEventUpdate(latestEvent, userEmail);
        if (!validationResult.isValid()) {
            throw new GlobalException(validationResult.getErrorMessages().get(0), HttpStatus.BAD_REQUEST.value());
        }
        boolean playerListChanged = this.syncEventPlayers(latestEvent, eventDto);
        if (playerListChanged) {
            latestEvent.getTeamVariations().removeAll(latestEvent.getTeamVariations());
            Account loggedInAccount = accountRepository.findByEmail(userEmail);
            EventLog eventLog = new EventLogFactory().build("UPDATED", loggedInAccount.getUsername());
            eventLogRepository.save(eventLog);
            latestEvent.getEventLogList().add(eventLog);
        }
        eventRepository.save(latestEvent);
        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }

    private boolean syncEventPlayers(Event event, CreateEventDto eventDto) {
        List<String> playerNameListBeforeSync = event.getParticipationList()
                .stream()
                .map(participation -> participation.getAccount().getUsername())
                .collect(Collectors.toList());

        event.getParticipationList()
                .removeIf(p -> !eventDto.getRegisteredPlayers().contains(p.getAccount().getUsername()));

        List<String> oldPlayerNameList = event.getParticipationList()
                .stream()
                .map(participation -> participation.getAccount().getUsername())
                .collect(Collectors.toList());

        List<Participation> missingPlayers = eventDto.getRegisteredPlayers()
                .stream()
                .filter(playerName -> !oldPlayerNameList.contains(playerName))
                .map(playerName -> new Participation(accountRepository.findByUsername(playerName), LocalDateTime.now()))
                .collect(Collectors.toList());

        event.getParticipationList().addAll(missingPlayers);

        List<String> playerNameListAfterSync = event.getParticipationList()
                .stream()
                .map(participation -> participation.getAccount().getUsername())
                .collect(Collectors.toList());

//        return if there is any difference after the sync
        return !playerNameListBeforeSync.equals(playerNameListAfterSync);
    }

    @Override
    public List<EventResponseDto> listEvents() {
        List<Event> allEvents = eventRepository.findAll();
        List<EventResponseDto> dtoList = eventConverter.fromEventListToEventResponseDtoList(allEvents);
        dtoList.sort(Comparator.comparing(EventResponseDto::getEventDateTime).reversed());
        return dtoList;
    }

    @Override
    public EventResponseDto generateTeams(String userEmail) {
        Event latestEvent = eventRepository.findLatestEvent();
        //TODO: add validation
        latestEvent.getTeamVariations().removeAll(latestEvent.getTeamVariations());
        List<Account> registeredAccounts = latestEvent.getParticipationList()
                .stream()
                .map(Participation::getAccount)
                .collect(Collectors.toList());
        List<TeamVariation> teamVariations = teamGeneratorService.generateVariations(registeredAccounts);
        List<TeamVariation> variations = new ArrayList<>();
        teamVariations.forEach(v -> {
            Team team1 = teamRepository.save(v.getTeam1());
            Team team2 = teamRepository.save(v.getTeam2());
            v.setTeam1(team1);
            v.setTeam2(team2);
            TeamVariation variation = teamVariationRepository.save(v);
            variations.add(variation);
        });
        latestEvent.getTeamVariations().addAll(variations);
        eventRepository.save(latestEvent);
        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }

    @Override
    public EventResponseDto updateTeamVariationSelection(String userEmail, List<Long> selectedVariationIds) throws Exception {

        Event latestEvent = eventRepository.findLatestEvent();

        //unselecting all variations
        latestEvent.getTeamVariations().forEach(v -> {
            v.setSelectedForVoting(false);
            teamVariationRepository.save(v);
        });

        List<Long> variationIdsFromLatestEvent = latestEvent.getTeamVariations().stream().map(TeamVariation::getId).collect(Collectors.toList());

        for (Long variationId : selectedVariationIds) {
            if (!variationIdsFromLatestEvent.contains(variationId) ) {
                throw new GlobalException("Team variation with id ${variationId} is not part of the latest event", HttpStatus.PRECONDITION_FAILED.value());
            }
            Optional<TeamVariation> variationOptional = teamVariationRepository.findById(variationId);
            if (variationOptional.isEmpty()) {
                throw new GlobalException("Team variation with id ${variationId} not found", HttpStatus.PRECONDITION_FAILED.value());
            }
            TeamVariation variation = variationOptional.get();
            variation.setSelectedForVoting(true);
            teamVariationRepository.save(variation);
        }

        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }

    @Override
    public EventResponseDto updateStatus(String userEmail, String status) throws Exception {
        Event latestEvent = eventRepository.findLatestEvent();

        ValidationResult validationResult = validatorService.validateEventStatusUpdate(latestEvent, userEmail, status);
        if (!validationResult.isValid()) {
            throw new GlobalException(validationResult.getErrorMessages().get(0), HttpStatus.BAD_REQUEST.value());
        }

        latestEvent.setStatus(EventStatus.valueOf(status));
        eventRepository.save(latestEvent);
        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }
}
