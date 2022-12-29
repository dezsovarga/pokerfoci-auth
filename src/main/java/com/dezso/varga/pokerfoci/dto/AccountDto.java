package com.dezso.varga.pokerfoci.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
}
