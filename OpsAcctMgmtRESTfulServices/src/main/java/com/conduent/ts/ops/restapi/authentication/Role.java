package com.conduent.ts.ops.restapi.authentication;

public enum Role {
	INDV ("INDV"),
	INST ("INST");
	
	private final String role;
	
	Role (String role) {
		this.role = role;
	}
	
	public String getRole() {
		return this.role;
	}
}
