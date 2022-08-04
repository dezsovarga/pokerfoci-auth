package com.dezso.varga.pokerfoci.authentication.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDto {
	
	private AccountDto accountDto;
	private String verificationLink;
	private String confirmToken;

}
