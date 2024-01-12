package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.domain.EventStatus;
import com.dezso.varga.pokerfoci.dto.ValidationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ValidatorServiceImpl implements ValidatorService {

    @Override
    public ValidationResult validateEventRegistration(Event latestEvent, String userEmail) {
        List<String> errorMessages = new ArrayList<>();
        if (!latestEvent.getStatus().equals(EventStatus.INITIATED)) {
            errorMessages.add("Event not active anymore");
        }
        if (latestEvent.getParticipationList().stream().anyMatch(p -> p.getAccount().getEmail().equals(userEmail))){
            errorMessages.add("User already registered to the latest event");
        }
        return errorMessages.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errorMessages);
    }
}
