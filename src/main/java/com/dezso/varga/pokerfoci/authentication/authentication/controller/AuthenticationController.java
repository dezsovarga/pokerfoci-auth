package com.dezso.varga.pokerfoci.authentication.authentication.controller;

import com.dezso.varga.pokerfoci.authentication.authentication.utils.AuthUtils;
import com.dezso.varga.pokerfoci.authentication.authentication.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dezso.varga.pokerfoci.authentication.authentication.domain.RegisterRequest;

@RestController
@RequestMapping("account")
public class AuthenticationController {
	
	@Autowired
	private AuthenticationService authenticationService;

	@RequestMapping(method=RequestMethod.POST,value="/register")
	public String signup(@RequestBody RegisterRequest registerRequest) throws Exception{
		String confirmationToken = authenticationService.getConfirmationToken(registerRequest);
		return confirmationToken;
	}

	@RequestMapping(method=RequestMethod.GET, value="register/confirm")
	public String confirm(@RequestHeader (value="Authorization") String confirmToken) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.saveAccount(confirmToken));
	}

	@RequestMapping(method=RequestMethod.POST, value="/login")
	public String login(@RequestHeader (value="Authorization", required=false) String authHeader) throws Exception {

		return AuthUtils.generateBearerToken(authenticationService.login(authHeader));
	}
}
