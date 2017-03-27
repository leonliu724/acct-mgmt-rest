package com.conduent.ts.ops.restapi.filters;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.conduent.ts.ops.restapi.authentication.Authenticator;
import com.conduent.ts.ops.restapi.authentication.Role;
import com.conduent.ts.ops.restapi.authentication.SecureResource;
import com.conduent.ts.ops.restapi.database.RESTfulServicesDAO;
import com.conduent.ts.ops.restapi.exceptions.CustomAppException;

@SecureResource
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;
	
	private static Logger logger = Logger.getLogger(AuthenticationFilter.class.getName());
	
    /**
     * Default constructor. 
     */
    public AuthenticationFilter() {
    }
    
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		String authHeader = requestContext.getHeaders().getFirst("Authorization");
		Authenticator authenticator = new Authenticator();
		JWT jwt = null;
		
		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<Role> classRoles = extractRoles(resourceClass);
		
		Method resourceMethod = resourceInfo.getResourceMethod();
		List<Role> methodRoles = extractRoles(resourceMethod);
		
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		
		try {			
			logger.info("Start token validation");
			jwt = authenticator.retrieveJWTFromHeader(authHeader);
			
			if (jwt == null) {
				throw new Exception("Invalid Token");
			}
			if (dao.checkRevokedTokens(jwt.getId())) {
				logger.warn("Token " + jwt.getId() + " for user " + jwt.getSubject() + " has already been revoked");
				throw new Exception("This token has already been revoked");
			}
			logger.info("Token validated. Username is: " + jwt.getSubject() + ". Start role validation");
			String[] roles = jwt.getClaim("roles").asArray(String.class);
			if (methodRoles != null) {
				checkRolePermission(roles, methodRoles);
			} else if (classRoles != null) {
				checkRolePermission(roles, classRoles);
			}
			logger.info("Role validated");
			
			requestContext.setProperty("jwt", jwt);
		} catch (Exception e) {
			throw new CustomAppException(e.getMessage(),Response.Status.UNAUTHORIZED.getStatusCode());
		}	
	}
    
	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	private List<Role> extractRoles(AnnotatedElement annotatedElement) {
		if (annotatedElement == null) {
            return null;
        } else {
        	SecureResource securedResource = annotatedElement.getAnnotation(SecureResource.class);
            if (securedResource == null) {
                return null;
            } else {
                Role[] allowedRoles = securedResource.value();
                return Arrays.asList(allowedRoles);
            }
        }
	}

	private void checkRolePermission(String[] userRoles, List<Role> requiredRoles) throws Exception {
		if (requiredRoles.isEmpty()) {
			return;
		}
		for (String userRole:userRoles) {
			if (requiredRoles.contains(Role.valueOf(userRole))) {
				return;
			}
		}
		throw new Exception("User does not have the right role prvilege");
	}
}
