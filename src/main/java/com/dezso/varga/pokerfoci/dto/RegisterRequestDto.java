package com.dezso.varga.pokerfoci.dto;

import com.dezso.varga.pokerfoci.dto.admin.AccountDto;
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
