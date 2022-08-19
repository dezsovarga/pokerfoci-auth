package com.dezso.varga.pokerfoci.authentication.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmTokenResponseDto {

    private String username;
    private String bearerToken;
}
