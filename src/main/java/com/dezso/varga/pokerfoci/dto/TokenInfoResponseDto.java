package com.dezso.varga.pokerfoci.dto;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfoResponseDto {

    private String username;
    private String bearerToken;
    private Set<String> roles;
}
