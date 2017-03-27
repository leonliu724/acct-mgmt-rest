package com.conduent.ts.ops.restapi.dto;

public class ErrorResponse {
	private int status;
	private String error_msg;
	
	public ErrorResponse() {
	}
	
	public ErrorResponse(String error_msg, int status) {
		this.error_msg = error_msg;
		this.status = status;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getError_msg() {
		return error_msg;
	}
	public void setError_msg(String error_msg) {
		this.error_msg = error_msg;
	}
	
	
}
