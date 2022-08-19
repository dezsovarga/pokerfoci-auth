package com.dezso.varga.pokerfoci.authentication.controller;

import com.dezso.varga.pokerfoci.authentication.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.authentication.domain.Account;
import com.dezso.varga.pokerfoci.authentication.dto.ConfirmTokenResponseDto;
import com.dezso.varga.pokerfoci.authentication.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.dezso.varga.pokerfoci.authentication.dto.RegisterRequestDto;

@CrossOrigin(origins="http://localhost:3000")
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
	public ConfirmTokenResponseDto confirm(@PathVariable String confirmToken) throws Exception {


		Account account = authenticationService.saveAccount(confirmToken);
		String bearerToken = AuthUtils.generateBearerToken(account);

		return ConfirmTokenResponseDto.builder().username(account.getUsername()).bearerToken(bearerToken).build();
	}

	@RequestMapping(method=RequestMethod.POST, value="/login")
	public String login(@RequestHeader (value="Authorization", required=false) String authHeader) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.login(authHeader));
	}
}
