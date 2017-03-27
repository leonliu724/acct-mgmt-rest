package com.conduent.ts.ops.restapi.resources.indv;

import java.util.ArrayList;
import java.util.List;

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
import com.conduent.ts.ops.restapi.config.ConfigCache;
import com.conduent.ts.ops.restapi.config.OSCodes;
import com.conduent.ts.ops.restapi.database.RESTfulServicesDAO;
import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.conduent.ts.ops.restapi.dto.UserRoles;
import com.conduent.ts.ops.restapi.exceptions.CustomAppException;
import com.conduent.ts.ops.restapi.input.AccountsCreateInput;
import com.conduent.ts.ops.restapi.input.AccountsUpdateInput;
import com.conduent.ts.ops.restapi.resources.PATCH;
import com.conduent.ts.ops.restapi.siebel.SiebelServicesClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class AccountResource {
	private static Logger logger = Logger.getLogger(AccountResource.class.getName());
	private ObjectMapper om = new ObjectMapper();
	
	@POST
	@Path("/indv_users")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createAccount(AccountsCreateInput input) {
		logger.info("Start");
		if (logger.isDebugEnabled()) {
			try {
				logger.debug(om.writeValueAsString(input));
			} catch (JsonProcessingException e) {
			}
		}
		
		String errorMsg = input.dataValidation();
		if (!errorMsg.equals("")) {
			logger.warn("Invalid Input: " + errorMsg);
			throw new CustomAppException(errorMsg, 422);
		}
		
		/* Duplicate account check start */
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		UserRoles searchUserRoles = new UserRoles();
		searchUserRoles.setUsername(input.getUser_info().getUsername());
		searchUserRoles = dao.getUserRoles(searchUserRoles);
		if (searchUserRoles != null) {
			logger.warn("Username: " + input.getUser_info().getUsername() + " already exists.");
			throw new CustomAppException("Username already exists.", 422);
		}
		searchUserRoles = new UserRoles();
		List<String> siebelRoles = new ArrayList<String>();
		OSCodes osCodes = ConfigCache.getCodesByShortDesc(Role.INDV.getRole());
		if (osCodes != null) {
			siebelRoles.add(osCodes.getDescripLong());
		}
		searchUserRoles.setFirstName(input.getUser_info().getFirstname());
		searchUserRoles.setLastName(input.getUser_info().getLastname());
		searchUserRoles.setEmail(input.getUser_info().getEmail());
		searchUserRoles.setSiebelRoles(siebelRoles);
		searchUserRoles = dao.getUserRoles(searchUserRoles);
		if (searchUserRoles != null) {
			logger.warn("User " + input.getUser_info().getFirstname() + " " + input.getUser_info().getLastname() + " already exists.");
			throw new CustomAppException("You already have an account in the system. Please use forgot password to retrieve your account.", 422);
		}
		/* Duplicate account check end */
		
		SiebelServicesClient client = new SiebelServicesClient();
		client.registerAccount(input.getUser_info());
		
		logger.info("End");
		return Response.status(Status.CREATED).build();
	}
	
	@GET
	@Path("/indv_users/{contact_id}")
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public Response findAccount(@Context HttpServletRequest request, 
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
		UserInfo searchUserInfo = new UserInfo();
		searchUserInfo.setContact_id(contact_id);
		searchUserInfo = client.findAccount(searchUserInfo, true);
		if (searchUserInfo == null) {
			logger.warn("No account found for Contact ID: " + contact_id);
			throw new CustomAppException("Account not found.", Response.Status.NOT_FOUND.getStatusCode());
		}
		
		logger.info("End");
		return Response.ok().entity(searchUserInfo).build();
	}
	
	@PATCH
	@Path("/indv_users/{contact_id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public Response updateAccount(	@Context HttpServletRequest request, 
									@PathParam("contact_id") String contact_id,
									AccountsUpdateInput input) {
		logger.info("Start");
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Path Parameters: contact_id=" + contact_id);
				logger.debug(om.writeValueAsString(input));
			} catch (JsonProcessingException e) {
			}
		}
		
		input.getUser_info().setContact_id(contact_id);
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
		client.updateAccount(input.getUser_info());
		
		logger.info("End");
		return Response.ok().build();
	}
}
