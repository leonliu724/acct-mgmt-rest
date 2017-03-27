package com.conduent.ts.ops.restapi.resources.security;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.conduent.ts.ops.restapi.authentication.Authenticator;
import com.conduent.ts.ops.restapi.authentication.SecureResource;
import com.conduent.ts.ops.restapi.database.RESTfulServicesDAO;
import com.conduent.ts.ops.restapi.dto.RevokedJWT;
import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.conduent.ts.ops.restapi.dto.UserRoles;
import com.conduent.ts.ops.restapi.exceptions.CustomAppException;
import com.conduent.ts.ops.restapi.input.AccountsChangePasswordInput;
import com.conduent.ts.ops.restapi.input.AccountsResetInput;
import com.conduent.ts.ops.restapi.input.AccountsForgotInput;
import com.conduent.ts.ops.restapi.siebel.SiebelServicesClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/")
public class AuthenticationResource {
	
	private static Logger logger = Logger.getLogger(AuthenticationResource.class.getName());
	private ObjectMapper om = new ObjectMapper();
	
	@POST
	@Path("/login")
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@HeaderParam("Authorization") String authHeader){
		logger.info("Start");
		
		Authenticator authenticator = new Authenticator();
		UserRoles userRoles = authenticator.basicAuthenticate(authHeader);
		
		if (userRoles != null) {
			JsonObject jsonValue = Json.createObjectBuilder()
					.add("access-token", authenticator.generateJWT(userRoles))
					.add("token_type", "Bearer")
					.build();
			
			logger.info("End");
			return Response.ok(jsonValue).build();
		} else {
			throw new CustomAppException("Authentication Failed", Response.Status.UNAUTHORIZED.getStatusCode());
		}
	}
	
	@POST
	@Path("/logout")
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource
	public Response logout(@Context HttpServletRequest request) {
		logger.info("Start");
		
		JWT jwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		Authenticator authenticator = new Authenticator();
		RevokedJWT revokedJWT = authenticator.generateRevokedJWT(jwt);

		if (dao.insertRevokedToken(revokedJWT) == 0) {	
			logger.error("Unable to insert revoked token to database");
			throw new CustomAppException("Unable to revoke the token at this time", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		logger.info("End");
		return Response.ok().build();
	}
	
	@POST
	@Path("/refresh_token")
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource
	public Response refreshToken(@Context HttpServletRequest request) {
		logger.info("Start");
		
		JWT oldJwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		Authenticator authenticator = new Authenticator();
		RevokedJWT revokedJWT = authenticator.generateRevokedJWT(oldJwt);
		
		dao.insertRevokedToken(revokedJWT);
		
		JsonObject jsonValue = Json.createObjectBuilder()
				.add("access-token", authenticator.generateJWT(oldJwt))
				.add("token_type", "Bearer")
				.build();
		
		logger.info("End");
		return Response.ok(jsonValue).build();
	}
	
	
	@POST
	@Path("/accounts/forgot")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response verifyAccount(AccountsForgotInput input) {
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
		
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		UserRoles userRoles = new UserRoles();
		userRoles.setFirstName(input.getUser_info().getFirstname());
		userRoles.setLastName(input.getUser_info().getLastname());
		userRoles.setEmail(input.getUser_info().getEmail());
		
		userRoles = dao.getUserRoles(userRoles);
		if (userRoles != null) {
			UserInfo userInfo = new UserInfo();
			userInfo.setContact_id(userRoles.getContactId());
			userInfo.setSecurity_q(userRoles.getSecurityQ());
			
			logger.info("End");
			return Response.ok().entity(userInfo).build();
		} else {
			logger.warn("User cannot be found");
			throw new CustomAppException("User cannot be found", Response.Status.NOT_FOUND.getStatusCode());
		}
	}
	
	@POST
	@Path("/accounts/reset")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetAccount(AccountsResetInput input) {
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

		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		UserRoles userRoles = new UserRoles();
		userRoles.setContactId(input.getUser_info().getContact_id());
		
		userRoles = dao.getUserRoles(userRoles);
		
		if (userRoles == null) {
			logger.warn("Contact ID not found for: " + input.getUser_info().getContact_id());
			throw new CustomAppException("Invalid security answer", Response.Status.UNAUTHORIZED.getStatusCode());
		} else if (!input.getUser_info().getSecurity_a().equals(userRoles.getSecurityA())) {
			logger.warn("Incorrect security answer for Contact_ID:" + input.getUser_info().getContact_id()
					+ ", Security Answer: " + input.getUser_info().getSecurity_a());
			throw new CustomAppException("Invalid security answer", Response.Status.UNAUTHORIZED.getStatusCode());
		} else {
			Authenticator authenticator = new Authenticator();
			String newPassword = authenticator.generatePassword();
			authenticator.ldapResetPassword(userRoles.getUsername(), newPassword);
			
			SiebelServicesClient client = new SiebelServicesClient();
			client.notifyCustomerResetPassword(input.getUser_info().getContact_id(), newPassword);
			
			UserInfo userInfo = new UserInfo();
			userInfo.setUsername(userRoles.getUsername());
			
			logger.info("End");
			return Response.ok().entity(userInfo).build();
		}
	}
	
	@POST
	@Path("/accounts/{contact_id}/change_pwd")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource
	public Response changePwd(	@Context HttpServletRequest request, 
								@PathParam("contact_id") String contact_id,
								AccountsChangePasswordInput input) {
		logger.info("Start");
		if (logger.isDebugEnabled()) {
			try {
				logger.debug("Path Parameters: contact_id=" + contact_id);
				logger.debug(om.writeValueAsString(input));
			} catch (JsonProcessingException e) {
			}
		}
		
		JWT jwt = (JWT) request.getAttribute("jwt");
		request.removeAttribute("jwt");
		
		String errorMsg = input.dataValidation();
		if (!errorMsg.equals("")) {
			logger.warn("Invalid Input: " + errorMsg);
			throw new CustomAppException(errorMsg, 422);
		}
		
		/* Access privilege check start */
		String jwtContactId = jwt.getClaim("contact_id").asString();
		String jwtUsername = jwt.getSubject();
		
		if (!jwtContactId.equals(contact_id) || !jwtUsername.equals(input.getUser_info().getUsername())) {
			logger.warn("Contact ID: " + jwtContactId + " does not have privilege for this request.");
			throw new CustomAppException("You don't have privilege to access the requested resource.", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		/* Access privilege check end */
		
		Authenticator authenticator = new Authenticator();
		authenticator.ldapChangePassword(input.getUser_info().getUsername(), 
				input.getUser_info().getOld_password(), input.getUser_info().getNew_password());
		
		SiebelServicesClient client = new SiebelServicesClient();
		client.notifyCustomerChangePassword(contact_id);
		
		logger.info("End");
		return Response.ok().build();
	}
}
