package com.conduent.ts.ops.restapi.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.conduent.ts.ops.restapi.dto.ErrorResponse;

public class CustomAppException extends WebApplicationException {
	
	private static final long serialVersionUID = 1L;

	public CustomAppException(String errorMsg, int statusCode) {
		super(Response.status(statusCode)
				.entity(new ErrorResponse(errorMsg, statusCode))
				.type(MediaType.APPLICATION_JSON).build());
	}
}
