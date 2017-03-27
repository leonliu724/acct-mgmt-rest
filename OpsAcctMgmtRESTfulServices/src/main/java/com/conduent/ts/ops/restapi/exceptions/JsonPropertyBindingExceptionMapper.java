package com.conduent.ts.ops.restapi.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.dto.ErrorResponse;
import com.fasterxml.jackson.databind.exc.PropertyBindingException;

@Provider
public class JsonPropertyBindingExceptionMapper implements ExceptionMapper<PropertyBindingException> {
	
	private static Logger logger = Logger.getLogger(JsonPropertyBindingExceptionMapper.class.getName());
	
	public Response toResponse(PropertyBindingException exception) {
		logger.error(exception.getMessage());
		
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
		errorResponse.setError_msg("Invalid Json input.");
		
		return Response.status(errorResponse.getStatus())
				.entity(errorResponse)
				.type(MediaType.APPLICATION_JSON).build();
	}
}
