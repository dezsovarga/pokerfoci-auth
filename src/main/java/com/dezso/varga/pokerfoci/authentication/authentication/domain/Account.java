package com.dezso.varga.pokerfoci.authentication.authentication.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Account {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private String password;
	private Set<Role> roles;

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Account(Long id, String firstName, String lastName, String email, String password, Set<Role> roles) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
		this.roles = roles;
	}

	public Account(RegisterRequest registerRequest) {
		this.firstName = registerRequest.getAccount().getFirstName();
		this.lastName = registerRequest.getAccount().getLastName();
		this.email = registerRequest.getAccount().getEmail();
		this.password = registerRequest.getAccount().getPassword();
	}

	public Account(String firstName, String lastName, String email, String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
		this.password = password;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "account_role", joinColumns = @JoinColumn(name = "account_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
}
