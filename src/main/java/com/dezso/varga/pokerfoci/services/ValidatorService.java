package com.dezso.varga.pokerfoci.services;

import com.dezso.varga.pokerfoci.domain.Event;
import com.dezso.varga.pokerfoci.dto.ValidationResult;

public interface ValidatorService {

    ValidationResult validateEventRegistration(Event latestEvent, String userEmail);

    ValidationResult validateEventUnRegistration(Event latestEvent, String userEmail);

    ValidationResult validateEventUpdate(Event latestEvent, String userEmail);

}
