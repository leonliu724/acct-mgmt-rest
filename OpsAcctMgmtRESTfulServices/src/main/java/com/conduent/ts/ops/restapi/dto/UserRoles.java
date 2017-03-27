package com.conduent.ts.ops.restapi.dto;

import java.util.List;

import com.conduent.ts.ops.restapi.authentication.Role;

public class UserRoles {
	private String username;
	private String contactId;
	private String accountId;
	private List<String> siebelRoles;
	private List<Role> appRoles;
	private String type;
	private String firstName;
	private String lastName;
	private String email;
	private String securityQ;
	private String securityA;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContactId() {
		return contactId;
	}
	public void setContactId(String contactId) {
		this.contactId = contactId;
	}
	public List<String> getSiebelRoles() {
		return siebelRoles;
	}
	public void setSiebelRoles(List<String> siebelRoles) {
		this.siebelRoles = siebelRoles;
	}
	public List<Role> getAppRoles() {
		return appRoles;
	}
	public void setAppRoles(List<Role> appRoles) {
		this.appRoles = appRoles;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
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
	public String getSecurityQ() {
		return securityQ;
	}
	public void setSecurityQ(String securityQ) {
		this.securityQ = securityQ;
	}
	public String getSecurityA() {
		return securityA;
	}
	public void setSecurityA(String securityA) {
		this.securityA = securityA;
	}
	
}
