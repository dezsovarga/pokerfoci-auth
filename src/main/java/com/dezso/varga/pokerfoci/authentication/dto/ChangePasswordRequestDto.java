package com.dezso.varga.pokerfoci.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordRequestDto {

    private String email;
    private String oldPassword;
    private String newPassword;

}
