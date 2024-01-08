package com.dezso.varga.pokerfoci.dto.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountForAdminDto {

    private Long id;
    private String username;
    private String email;
    private Boolean isAdmin;
    private Boolean isActive;
    private Integer skill;
}
