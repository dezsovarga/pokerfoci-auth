package com.dezso.varga.pokerfoci.authentication.authentication.domain;

public class RegisterRequest {
	
	Account account;
	String verificationLink;

	public RegisterRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RegisterRequest(Account account, String verificationLink) {
		super();
		this.account = account;
		this.verificationLink = verificationLink;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getVerificationLink() {
		return verificationLink;
	}

	public void setVerificationLink(String verificationLink) {
		this.verificationLink = verificationLink;
	}

}
