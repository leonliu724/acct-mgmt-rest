package com.conduent.ts.ops.restapi.siebel;

import java.util.Date;

public class SessionToken {
	private Date expiryDate;
	private String token;
	
	public Date getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
