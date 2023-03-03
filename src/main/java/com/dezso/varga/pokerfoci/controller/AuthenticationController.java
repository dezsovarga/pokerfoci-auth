package com.dezso.varga.pokerfoci.controller;

import com.dezso.varga.pokerfoci.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.domain.Account;
import com.dezso.varga.pokerfoci.domain.Role;
import com.dezso.varga.pokerfoci.dto.ChangePasswordRequestDto;
import com.dezso.varga.pokerfoci.dto.TokenInfoResponseDto;
import com.dezso.varga.pokerfoci.services.AuthenticationService;
import com.dezso.varga.pokerfoci.dto.RegisterRequestDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("account")
@AllArgsConstructor
public class AuthenticationController {
	
	private AuthenticationService authenticationService;

	@RequestMapping(method=RequestMethod.POST,value="/register")
	public RegisterRequestDto signup(@RequestBody RegisterRequestDto registerRequest) throws Exception{
		String confirmationToken = authenticationService.getConfirmationToken(registerRequest);
		String confirmationLink = authenticationService.generateConfirmationLink(confirmationToken).toString();
		return RegisterRequestDto.builder().verificationLink(confirmationLink).confirmToken(confirmationToken).build();
	}

	@GetMapping("/register/confirm/{confirmToken}")
	public TokenInfoResponseDto confirm(@PathVariable String confirmToken) throws Exception {

		Account account = authenticationService.saveAccount(confirmToken);
		String bearerToken = AuthUtils.generateBearerToken(account);

		return TokenInfoResponseDto.builder()
				.username(account.getUsername())
				.bearerToken(bearerToken)
				.roles(account.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
				.build();
	}

	@RequestMapping(method=RequestMethod.POST, value="/login")
	public TokenInfoResponseDto login(@RequestHeader ("Authorization") String authHeader) throws Exception {

		Account account = authenticationService.login(authHeader);
		String bearerToken = AuthUtils.generateBearerToken(account);
		return TokenInfoResponseDto.builder()
				.username(account.getUsername())
				.bearerToken(bearerToken)
				.roles(account.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
				.build();
	}

	@RequestMapping(method=RequestMethod.POST, value="/change-password")
	public ResponseEntity<Object> changePassword(@RequestBody ChangePasswordRequestDto changePasswordRequestDto) throws Exception {

		//TODO: add validation for change password
		authenticationService.changePassword(changePasswordRequestDto);
		return ResponseEntity.ok().build();
	}
}
