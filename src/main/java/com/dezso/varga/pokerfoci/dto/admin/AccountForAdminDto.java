package com.dezso.varga.pokerfoci.dto.admin;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountForAdminDto {

    private long id;
    private String username;
    private String email;
    private boolean isAdmin;
    private boolean isActive;
}
