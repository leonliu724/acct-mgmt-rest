package com.conduent.ts.ops.restapi.resources.indv;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.conduent.ts.ops.restapi.authentication.Role;
import com.conduent.ts.ops.restapi.authentication.SecureResource;
import com.conduent.ts.ops.restapi.exceptions.CustomAppException;
import com.conduent.ts.ops.restapi.input.AddressesCreateInput;
import com.conduent.ts.ops.restapi.input.AddressesUpdateInput;
import com.conduent.ts.ops.restapi.resources.PATCH;
import com.conduent.ts.ops.restapi.siebel.SiebelServicesClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class AddressResource {
	private static Logger logger = Logger.getLogger(AddressResource.class.getName());
	private ObjectMapper om = new ObjectMapper();
	
	@POST
	@Path("/indv_users/{contact_id}/addresses")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public Response createAddress(	@Context HttpServletRequest request, 
									@PathParam("contact_id") String contact_id,
									AddressesCreateInput input) {
		logger.info("Start");
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Path Parameters: contact_id=" + contact_id);
				logger.debug(om.writeValueAsString(input));
			} catch (JsonProcessingException e) {
			}
		}
		
		String errorMsg = input.dataValidation();
		if (!errorMsg.equals("")) {
			logger.warn("Invalid Input: " + errorMsg);
			throw new CustomAppException(errorMsg, 422);
		}
		
		JWT jwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		/* Access privilege check start */
		String jwtContactId = jwt.getClaim("contact_id").asString();
		
		if (!jwtContactId.equals(contact_id)) {
			logger.warn("Contact ID: " + jwtContactId + " does not have privilege for this request.");
			throw new CustomAppException("You don't have privilege to access the requested resource.", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		/* Access privilege check end */
		
		SiebelServicesClient client = new SiebelServicesClient();
		
		
		logger.info("End");
		return Response.status(Status.CREATED).build();
	}
	
	@GET
	@Path("/indv_users/{contact_id}/addresses")
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public Response getAllAddresses(@Context HttpServletRequest request, 
									@PathParam("contact_id") String contact_id) {
		logger.info("Start");
		logger.debug("Path Parameters: contact_id=" + contact_id);
		
		JWT jwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		/* Access privilege check start */
		String jwtContactId = jwt.getClaim("contact_id").asString();
		
		if (!jwtContactId.equals(contact_id)) {
			logger.warn("Contact ID: " + jwtContactId + " does not have privilege for this request.");
			throw new CustomAppException("You don't have privilege to access the requested resource.", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		/* Access privilege check end */
		
		SiebelServicesClient client = new SiebelServicesClient();
		
		
		logger.info("End");
		return Response.ok().build();
	}
	
	@GET
	@Path("/indv_users/{contact_id}/addresses/{address_id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public Response getAddressById(	@Context HttpServletRequest request, 
									@PathParam("contact_id") String contact_id,
									@PathParam("address_id") String address_id) {
		logger.info("Start");
		logger.debug("Path Parameters: contact_id=" + contact_id + ", address_id=" + address_id);
		
		JWT jwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		/* Access privilege check start */
		String jwtContactId = jwt.getClaim("contact_id").asString();
		
		if (!jwtContactId.equals(contact_id)) {
			logger.warn("Contact ID: " + jwtContactId + " does not have privilege for this request.");
			throw new CustomAppException("You don't have privilege to access the requested resource.", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		/* Access privilege check end */
		
		SiebelServicesClient client = new SiebelServicesClient();
		
		
		logger.info("End");
		return Response.ok().build();
	}
	
	@PATCH
	@Path("/indv_users/{contact_id}/addresses/{address_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public Response updateAddress(	@Context HttpServletRequest request, 
									@PathParam("contact_id") String contact_id,
									@PathParam("address_id") String address_id,
									AddressesUpdateInput input) {
		logger.info("Start");
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Path Parameters: contact_id=" + contact_id + ", address_id=" + address_id);
				logger.debug(om.writeValueAsString(input));
			} catch (JsonProcessingException e) {
			}
		}
		
		input.getAddress_info().setAddress_id(address_id);
		String errorMsg = input.dataValidation();
		if (!errorMsg.equals("")) {
			logger.warn("Invalid Input: " + errorMsg);
			throw new CustomAppException(errorMsg, 422);
		}
		
		JWT jwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		/* Access privilege check start */
		String jwtContactId = jwt.getClaim("contact_id").asString();
		
		if (!jwtContactId.equals(contact_id)) {
			logger.warn("Contact ID: " + jwtContactId + " does not have privilege for this request.");
			throw new CustomAppException("You don't have privilege to access the requested resource.", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		/* Access privilege check end */
		
		SiebelServicesClient client = new SiebelServicesClient();
		
		
		logger.info("End");
		return Response.ok().build();
	}
}
