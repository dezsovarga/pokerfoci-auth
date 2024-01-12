package com.dezso.varga.pokerfoci.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationResult {

    private boolean isValid;
    private List<String> errorMessages;

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(List<String> errorMessages) {
        return new ValidationResult(false, errorMessages);
    }
}
