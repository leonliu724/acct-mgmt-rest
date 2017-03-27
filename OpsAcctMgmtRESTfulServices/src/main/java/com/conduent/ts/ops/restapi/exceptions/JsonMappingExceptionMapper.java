package com.conduent.ts.ops.restapi.exceptions;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.dto.ErrorResponse;
import com.fasterxml.jackson.databind.JsonMappingException;

@Provider
public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {

	private static Logger logger = Logger.getLogger(JsonMappingExceptionMapper.class.getName());
	
	public Response toResponse(JsonMappingException exception) {
		logger.error(exception.getMessage());
		
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setStatus(Response.Status.BAD_REQUEST.getStatusCode());
		errorResponse.setError_msg("Invalid Json input.");
		
		return Response.status(errorResponse.getStatus())
				.entity(errorResponse)
				.type(MediaType.APPLICATION_JSON).build();
	}

}
