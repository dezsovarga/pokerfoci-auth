package com.dezso.varga.pokerfoci.authentication.controller;

import com.dezso.varga.pokerfoci.authentication.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.authentication.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.dezso.varga.pokerfoci.authentication.dto.RegisterRequest;

@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("account")
@AllArgsConstructor
public class AuthenticationController {
	
	private AuthenticationService authenticationService;

	@RequestMapping(method=RequestMethod.POST,value="/register")
	public RegisterRequest signup(@RequestBody RegisterRequest registerRequest) throws Exception{
		String confirmationToken = authenticationService.getConfirmationToken(registerRequest);
		String confirmationLink = authenticationService.generateConfirmationLink(confirmationToken).toString();
		return RegisterRequest.builder().verificationLink(confirmationLink).confirmToken(confirmationToken).build();
	}

	@GetMapping("/register/confirm/{confirmToken}")
	public String confirm(@PathVariable String confirmToken) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.saveAccount(confirmToken));
	}

	@RequestMapping(method=RequestMethod.POST, value="/login")
	public String login(@RequestHeader (value="Authorization", required=false) String authHeader) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.login(authHeader));
	}
}
