package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.converters.EventConverter;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.Participation;
import com.dezso.varga.pokerfoci.dto.EventResponseDto;
import com.dezso.varga.pokerfoci.dto.ValidationResult;
import com.dezso.varga.pokerfoci.exeptions.GlobalException;
import com.dezso.varga.pokerfoci.repository.AccountRepository;
import com.dezso.varga.pokerfoci.repository.EventRepository;
import com.dezso.varga.pokerfoci.repository.ParticipationRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventConverter eventConverter;
    private final AccountRepository accountRepository;
    private final ParticipationRepository participationRepository;
    private final ValidatorService validatorService;

    @Override
    public EventResponseDto getLatestEvent() {
        Event latestEvent = eventRepository.findLatestEvent();
        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }

    @Override
    public EventResponseDto registerToLatestEvent(String userEmail) throws Exception{
        Event latestEvent = eventRepository.findLatestEvent();
        ValidationResult validationResult = validatorService.validateEventRegistration(latestEvent, userEmail);
        if (!validationResult.isValid()) {
            throw new GlobalException(validationResult.getErrorMessages().get(0), HttpStatus.BAD_REQUEST.value());
        }
        Account loggedInAccount = accountRepository.findByEmail(userEmail);
        Participation newParticipation = participationRepository.save(new Participation(loggedInAccount));
        latestEvent.getParticipationList().add(newParticipation);
        eventRepository.save(latestEvent);
        return eventConverter.fromEventToEventResponseDto(latestEvent);
    }
}
