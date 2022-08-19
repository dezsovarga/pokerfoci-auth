package com.dezso.varga.pokerfoci.authentication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenInfoResponseDto {

    private String username;
    private String bearerToken;
}
