package com.conduent.ts.ops.restapi.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonParseException;

@Provider
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

	private static Logger logger = Logger.getLogger(JsonParseExceptionMapper.class.getName());
	
	public Response toResponse(JsonParseException exception) {
		logger.error(exception.getMessage());
		
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
		errorResponse.setError_msg("Invalid Json input.");
		
		return Response.status(errorResponse.getStatus())
				.entity(errorResponse)
				.type(MediaType.APPLICATION_JSON).build();
	}
	
}
