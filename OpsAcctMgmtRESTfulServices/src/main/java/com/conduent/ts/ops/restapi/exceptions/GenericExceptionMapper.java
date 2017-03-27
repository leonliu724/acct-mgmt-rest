package com.conduent.ts.ops.restapi.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import org.apache.log4j.Logger;
import com.conduent.ts.ops.restapi.dto.ErrorResponse;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

	private static Logger logger = Logger.getLogger(GenericExceptionMapper.class.getName());
	
	public Response toResponse(Exception exception) {
		logger.error(exception.getMessage());
		
		ErrorResponse errorResponse = new ErrorResponse();
		
		if (exception instanceof WebApplicationException) {
			Response response = ((WebApplicationException) exception).getResponse();
			if (exception instanceof CustomAppException) {
				return response;
			} else {
				errorResponse.setStatus(response.getStatus());
				errorResponse.setError_msg(exception.getMessage());
				return Response.status(errorResponse.getStatus())
						.entity(errorResponse)
						.type(MediaType.APPLICATION_JSON).build();
			}
		} else {
			errorResponse.setStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
			errorResponse.setError_msg("The server encountered an internal error.");
			return Response.status(errorResponse.getStatus())
					.entity(errorResponse)
					.type(MediaType.APPLICATION_JSON).build();
		}
	}
}
