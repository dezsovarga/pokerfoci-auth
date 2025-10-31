package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.dto.ValidationResult;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ValidatorServiceImpl implements ValidatorService {

    private static final Logger LOG = getLogger(ValidatorServiceImpl.class);

    @Override
    public ValidationResult validateEventRegistration(Event latestEvent, String userEmail) {
        List<String> errorMessages = validateEventNotActive(latestEvent);
        if (latestEvent.getParticipationList().stream().anyMatch(p -> p.getAccount().getEmail().equals(userEmail))){
            errorMessages.add("User already registered to the latest event");
        }
        return errorMessages.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errorMessages);
    }

    @Override
    public ValidationResult validateEventUnRegistration(Event latestEvent, String userEmail) {
        List<String> errorMessages = validateEventNotActive(latestEvent);
        if (latestEvent.getParticipationList().stream().noneMatch(p -> p.getAccount().getEmail().equals(userEmail))){
            errorMessages.add("User not registered to the latest event");
        }
        return errorMessages.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errorMessages);
    }

    @Override
    public ValidationResult validateEventUpdate(Event latestEvent, String userEmail) {
        List<String> errorMessages = validateEventNotActive(latestEvent);

        return errorMessages.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errorMessages);
    }

    @Override
    public ValidationResult validateEventStatusUpdate(Event latestEvent, String userEmail, String newStatus) {
        List<String> errorMessages = validateEventNotActive(latestEvent);
        try {
            EventStatus eventStatus = EventStatus.valueOf(newStatus.toUpperCase());
        } catch(IllegalArgumentException ex) {
            LOG.error("Invalid event status: {}", newStatus);
            errorMessages.add("Invalid event status: " + newStatus);
            return ValidationResult.invalid(errorMessages);
        }

        return errorMessages.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errorMessages);
    }

    @Override
    public ValidationResult validateCreateVoting(Event latestEvent, String userEmail) {
        List<String> errorMessages = validateEventNotActive(latestEvent);
        if (latestEvent.isVotingEnabled()) {
            errorMessages.add("Voting was already enabled for the latest event");
        }
        return errorMessages.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errorMessages);
    }

    private List<String> validateEventNotActive(Event latestEvent) {
        List<String> errorMessages = new ArrayList<>();
        if (latestEvent.getStatus().equals(EventStatus.COMPLETED)) {
            errorMessages.add("Event not active anymore");
        }
        return errorMessages;
    }
}
