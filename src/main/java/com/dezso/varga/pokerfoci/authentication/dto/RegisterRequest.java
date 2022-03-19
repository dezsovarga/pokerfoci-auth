package com.dezso.varga.pokerfoci.authentication.dto;

import com.dezso.varga.pokerfoci.authentication.domain.Account;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
	
	private Account account;
	private String verificationLink;
	private String confirmToken;

}
