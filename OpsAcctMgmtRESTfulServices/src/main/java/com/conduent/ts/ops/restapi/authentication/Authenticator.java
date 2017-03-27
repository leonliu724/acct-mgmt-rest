package com.conduent.ts.ops.restapi.authentication;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.ModificationItem;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.conduent.ts.ops.restapi.config.ConfigCache;
import com.conduent.ts.ops.restapi.config.Constants;
import com.conduent.ts.ops.restapi.config.OSCodes;
import com.conduent.ts.ops.restapi.database.RESTfulServicesDAO;
import com.conduent.ts.ops.restapi.dto.RevokedJWT;
import com.conduent.ts.ops.restapi.dto.UserRoles;
import com.conduent.ts.ops.restapi.exceptions.CustomAppException;
import com.conduent.ts.ops.restapi.util.Validator;

public class Authenticator {
	private static String TOKEN_ISSUER;
	private static int TOKEN_VALID_PERIOD;
	private static String TOKEN_SECRET_KEY;
	private static String LDAP_PROTOCOL;
	private static String LDAP_URL;
	private static String LDAP_PORT;
	private static String LDAP_BASE_DN;
	private static String LDAP_ADMIN;
	private static String LDAP_PWD;
	private static Map<String,OSCodes> ROLE_MAPPING = new HashMap<String,OSCodes>();
	private static final String[] PWD_CHAR_LIB_LETTER = {"ABCDEFGHJKLMNPQRSTUVWXYZ", "abcdefghijkmnpqrstuvwxyz", "23456789", "@!_-$"};
	
	private static volatile boolean configurationLoaded = false;
	
	private static Logger logger = Logger.getLogger(Authenticator.class.getName());
	
	public Authenticator() {
		if (!configurationLoaded) {
			synchronized (Authenticator.class) {
				if (!configurationLoaded) {
					try {
						TOKEN_SECRET_KEY = System.getProperty(Constants.CONFIG_REST_KEY);
						TOKEN_ISSUER = ConfigCache.getProcessParameter(Constants.PARAM_NAME_TOKEN_ISSUER).getParamValue();
						TOKEN_VALID_PERIOD = Integer.parseInt(
								ConfigCache.getProcessParameter(Constants.PARAM_NAME_TOKEN_VALID_MINUTES).getParamValue());
						LDAP_PROTOCOL = ConfigCache.getProcessParameter(Constants.PARAM_NAME_LDAP_PROTOCOL).getParamValue();
						LDAP_URL = ConfigCache.getProcessParameter(Constants.PARAM_NAME_LDAP_URL).getParamValue();
						LDAP_PORT = ConfigCache.getProcessParameter(Constants.PARAM_NAME_LDAP_PORT).getParamValue();
						LDAP_BASE_DN = ConfigCache.getProcessParameter(Constants.PARAM_NAME_LDAP_BASE_DN).getParamValue();
						LDAP_ADMIN = ConfigCache.getProcessParameter(Constants.PARAM_NAME_LDAP_ADMIN).getParamValue();
						LDAP_PWD = ConfigCache.getProcessParameter(Constants.PARAM_NAME_LDAP_PWD).getParamValue();
						ROLE_MAPPING = ConfigCache.getCodesMap();
						
						System.setProperty("javax.net.ssl.trustStore", System.getProperty(Constants.CONFIG_LDAP_TRUSTSTORE_LOC));
						System.setProperty("javax.net.ssl.trustStorePassword", System.getProperty(Constants.CONFIG_LDAP_TRUSTSTORE_KEY));
						
						configurationLoaded = true;
					} catch (Exception e) {
						throw new CustomAppException("Error getting configuration data", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
					}
				}
			}
		}
	}
	
	public String generateJWT(UserRoles userRoles) {
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("typ", "JWT");
		String[] rolesArray = generateStringRolesArray(userRoles);
		Calendar issueAt = Calendar.getInstance();
		Calendar expireAt = Calendar.getInstance();
		expireAt.setTime(issueAt.getTime());
		expireAt.add(Calendar.MINUTE, TOKEN_VALID_PERIOD);
		
		String token = "";
		
		try {
			token = JWT.create().withIssuer(TOKEN_ISSUER)
						.withHeader(headerMap)
						.withSubject(userRoles.getUsername())
						.withIssuedAt(issueAt.getTime())
						.withExpiresAt(expireAt.getTime())
						.withJWTId(UUID.randomUUID().toString())
						.withClaim("name", userRoles.getFirstName() + " " + userRoles.getLastName())
						.withClaim("contact_id", userRoles.getContactId())
						.withClaim("account_id", userRoles.getAccountId())
						.withArrayClaim("roles", rolesArray)
						.sign(Algorithm.HMAC256(TOKEN_SECRET_KEY));
		} catch (Exception e) {
		    logger.error(e.getMessage());
		    throw new CustomAppException("Error generating token", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		return token;
	}
	
	public String generateJWT(JWT jwt) {
		Map<String, Object> headerMap = new HashMap<String, Object>();
		headerMap.put("typ", "JWT");
		Calendar issueAt = Calendar.getInstance();
		Calendar expireAt = Calendar.getInstance();
		expireAt.setTime(issueAt.getTime());
		expireAt.add(Calendar.MINUTE, TOKEN_VALID_PERIOD);
		
		String token = "";
		
		try {
			token = JWT.create().withIssuer(TOKEN_ISSUER)
						.withHeader(headerMap)
						.withSubject(jwt.getSubject())
						.withIssuedAt(issueAt.getTime())
						.withExpiresAt(expireAt.getTime())
						.withJWTId(UUID.randomUUID().toString())
						.withClaim("name", jwt.getClaim("name").asString())
						.withClaim("contact_id", jwt.getClaim("contact_id").asString())
						.withClaim("account_id", jwt.getClaim("account_id").asString())
						.withArrayClaim("roles", jwt.getClaim("roles").asArray(String.class))
						.sign(Algorithm.HMAC256(TOKEN_SECRET_KEY));
		} catch (Exception e) {
		    logger.error(e.getMessage());
		    throw new CustomAppException("Error generating token", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		return token;
	}
	
	public JWT retrieveJWTFromHeader(String credential) {
		if (credential == null || !credential.toUpperCase().startsWith("BEARER")) {
			logger.warn("Not a valid bearer token.");
			return null;
		}
		credential = credential.replaceFirst("(?i)Bearer\\s+", "");
		
		JWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET_KEY))
										.withIssuer(TOKEN_ISSUER).build();
			jwt = (JWT)verifier.verify(credential);
		} catch (JWTVerificationException e) {
			logger.warn("Token verification failed: " + e.getLocalizedMessage());
		} catch (IllegalArgumentException e) {
			logger.error("Error decoding the token " + e.getMessage());
		} catch (UnsupportedEncodingException e) {
			logger.error("Error decoding the token " + e.getMessage());
		} 
		
		return jwt;
	}
	
	public UserRoles basicAuthenticate(String credential) {
		if (credential == null || !credential.toUpperCase().startsWith("BASIC")) {
			logger.warn("Not a valid basic authentication.");
			return null;
		}
		
		credential = credential.replaceFirst("(?i)Basic\\s+", "");
		String username;
		String password;
		
		try {
			String decodedCredential = new String(DatatypeConverter.parseBase64Binary(credential),"UTF-8");
			StringTokenizer tokenizer = new StringTokenizer(decodedCredential,":");
			username = tokenizer.nextToken();
			password = tokenizer.nextToken();
		} catch (Exception e) {
			logger.warn("Unable to decode BASE64 auth header: " + e.getMessage());
			return null;
		}
		
		if (ldapAuthenticate(username, password)) {
			logger.info("User " + username + " start role validation");
			
			RESTfulServicesDAO dao = new RESTfulServicesDAO();
			
			UserRoles userRoles = new UserRoles();
			userRoles.setUsername(username);
			userRoles = dao.getUserRoles(userRoles);
			
			if (validateRoles(userRoles)) {
				logger.info("User " + username + " role validated");
				return userRoles;
			}
			logger.warn("User " + username + " doesn't have role privilege");
		}
		
		return null;
	}
	
	public RevokedJWT generateRevokedJWT(JWT jwt) {
		RevokedJWT revokedJWT = new RevokedJWT();
		
		revokedJWT.setJti(jwt.getId());
		revokedJWT.setUsername(jwt.getSubject());
		revokedJWT.setEffectiveDate(jwt.getIssuedAt());
		revokedJWT.setExpiryDate(jwt.getExpiresAt());
		
		return revokedJWT;
	}
	
	public boolean ldapAuthenticate(String username, String password) {
		if (username == null || password == null) {
			return false;
		}
		
		if (!Validator.isAlphanumeric(username, "")) {
			logger.warn("User " + username + " is not a valid username.");
			throw new CustomAppException("Not a valid username.", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		
		// Check if account is locked
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		if (dao.checkLockedAccount(username)) {
			logger.warn("User " + username + " is locked out.");
			throw new CustomAppException("Account is locked out. Please reset the account", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		
		String ldapUrl = LDAP_PROTOCOL + "://" + LDAP_URL + ":" + LDAP_PORT;
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "CN="+username+",OU=PEOPLE," + LDAP_BASE_DN);
		env.put(Context.SECURITY_CREDENTIALS, password);
		
		try {
			logger.info("User " + username + " start ldap authentication");
			
			DirContext ctx = new InitialDirContext(env);
			ctx.close();
			
			logger.info("User " + username + " ldap authenticated");
			return true;
		} catch (AuthenticationException ex) {
		    String errorCode = "";
		    String dataCode = "";
			
			Pattern pattern = Pattern.compile("^(.+)(error code\\s+)(\\d{2})(.+)(data\\s+)(\\w{3})(.+)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(ex.getMessage());
			
			if (matcher.find()) {
				errorCode = matcher.group(3);
				dataCode = matcher.group(6);
			}
			
			if (errorCode.equals("49")) {
				switch (dataCode) {
					case "525":
						logger.warn("LDAP error 49/525: User not found");
						throw new CustomAppException("Authentication failed", Response.Status.UNAUTHORIZED.getStatusCode());
					case "52e":
						logger.warn("LDAP error 49/52e: Invalid password");
						throw new CustomAppException("Authentication failed", Response.Status.UNAUTHORIZED.getStatusCode());
					case "530":
						logger.warn("LDAP error 49/530: Not permitted to logon at this time");
						throw new CustomAppException("Authentication failed", Response.Status.UNAUTHORIZED.getStatusCode());
					case "532":
						logger.warn("LDAP error 49/532: Password expired");
						throw new CustomAppException("Password is expired. Please reset the password.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "533":
						logger.warn("LDAP error 49/533: Account disabled");
						throw new CustomAppException("Account is disabled. Please reset the account.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "701":
						logger.warn("LDAP error 49/701: Account expired");
						throw new CustomAppException("Account is expired. Please reset the account.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "773":
						logger.warn("LDAP error 49/773: User must reset password");
						throw new CustomAppException("Password is expired. Please reset the password.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "775":
						dao.insertLockedAccount(username);
						logger.warn("LDAP error 49/775: Account locked");
						throw new CustomAppException("Account is locked out. Please reset the account.", Response.Status.UNAUTHORIZED.getStatusCode());
					default:
						logger.warn("LDAP authentication failed: " + ex.getMessage() );
				}
			}
		    return false;
		} catch (NamingException ex) {
		    logger.error("Error when trying to create the ldap context: " + ex.getMessage());
		    throw new CustomAppException("Authentication service is temporarily unavailable. Please try again later.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}
	
	public void ldapChangePassword(String username, String oldPassword, String newPassword) {
		// Check if account is locked
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		if (dao.checkLockedAccount(username)) {
			logger.warn("User " + username + " is locked out.");
			throw new CustomAppException("Account is locked out. Please reset the account", Response.Status.UNAUTHORIZED.getStatusCode());
		}
		
		String ldapUrl = LDAP_PROTOCOL + "://" + LDAP_URL + ":" + LDAP_PORT;
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "CN="+username+",OU=PEOPLE," + LDAP_BASE_DN);
		env.put(Context.SECURITY_CREDENTIALS, oldPassword);
		
		oldPassword = "\"" + oldPassword + "\"";
		newPassword = "\"" + newPassword + "\"";
		
		try {
			logger.info("Start changing password for user " + username + " in ldap");
			DirContext ctx = new InitialDirContext(env);
			
			ModificationItem[] mods = new ModificationItem[2];
			mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd", oldPassword.getBytes("UTF-16LE")));
			mods[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd", newPassword.getBytes("UTF-16LE")));
			ctx.modifyAttributes("CN=" + username + ",OU=PEOPLE,DC=TTVECTOR,DC=local", mods);
			
			ctx.close();
			logger.info("Changing password for  " + username + " in ldap succeeds");
		} catch (AuthenticationException ex) {
			String errorCode = "";
		    String dataCode = "";
			
			Pattern pattern = Pattern.compile("^(.+)(error code\\s+)(\\d{2})(.+)(data\\s+)(\\w{3})(.+)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(ex.getMessage());
			
			if (matcher.find()) {
				errorCode = matcher.group(3);
				dataCode = matcher.group(6);
			}
			
			if (errorCode.equals("49")) {
				switch (dataCode) {
					case "525":
						logger.warn("LDAP error 49/525: User not found");
						throw new CustomAppException("Old Password is incorrect", Response.Status.UNAUTHORIZED.getStatusCode());
					case "52e":
						logger.warn("LDAP error 49/52e: Invalid password");
						throw new CustomAppException("Old Password is incorrect", Response.Status.UNAUTHORIZED.getStatusCode());
					case "530":
						logger.warn("LDAP error 49/530: Not permitted to logon at this time");
						throw new CustomAppException("Old Password is incorrect", Response.Status.UNAUTHORIZED.getStatusCode());
					case "532":
						logger.warn("LDAP error 49/532: Password expired");
						throw new CustomAppException("Old Password is expired. Please reset the password.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "533":
						logger.warn("LDAP error 49/533: Account disabled");
						throw new CustomAppException("Account is disabled. Please reset the account.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "701":
						logger.warn("LDAP error 49/701: Account expired");
						throw new CustomAppException("Account is expired. Please reset the account.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "773":
						logger.warn("LDAP error 49/773: User must reset password");
						throw new CustomAppException("Old Password is expired. Please reset the password.", Response.Status.UNAUTHORIZED.getStatusCode());
					case "775":
						dao.insertLockedAccount(username);
						logger.warn("LDAP error 49/775: Account locked");
						throw new CustomAppException("Account is locked out. Please reset the account.", Response.Status.UNAUTHORIZED.getStatusCode());
					default:
						logger.warn("LDAP authentication failed: " + ex.getMessage() );
				}
			}
		} catch (InvalidAttributeValueException ex) {
			logger.warn("New password does not meet the requirement: " + ex.getMessage());
		    throw new CustomAppException("New password does not meet the length, complexity or history requirement.", 422);
		} catch (NamingException ex) {
		    logger.error("Error when trying to create the ldap context: " + ex.getMessage());
		    throw new CustomAppException("Authentication service is temporarily unavailable. Please try again later.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		} catch (Exception ex) {
			logger.error("Error when resetting the password: " + ex.getMessage());
			throw new CustomAppException("Unable to reset password at this time. Please try again later.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}
	
	public void ldapResetPassword(String username, String password) {
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		if (dao.removeLockedAccount(username) > 0) {
			logger.info("User " + username + " is unlocked from DB.");
		}
		
		String ldapUrl = LDAP_PROTOCOL + "://" + LDAP_URL + ":" + LDAP_PORT;
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapUrl);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "CN="+LDAP_ADMIN+",OU=PEOPLE," + LDAP_BASE_DN);
		env.put(Context.SECURITY_CREDENTIALS, LDAP_PWD);
		
		password = "\"" + password + "\"";
		
		try {
			logger.info("Start resetting user " + username + " in ldap");
			DirContext ctx = new InitialDirContext(env);
			
			ModificationItem[] mods = new ModificationItem[2];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("unicodePwd", password.getBytes("UTF-16LE")));
			mods[1] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("lockoutTime", "0"));
			ctx.modifyAttributes("CN=" + username + ",OU=PEOPLE,DC=TTVECTOR,DC=local", mods);
			
			ctx.close();
			logger.info("Resetting " + username + " in ldap succeeds");
		} catch (NamingException ex) {
		    logger.error("Error when trying to create the ldap context: " + ex.getMessage());
		    throw new CustomAppException("Authentication service is temporarily unavailable. Please try again later.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		} catch (Exception ex) {
			logger.error("Error when resetting the password: " + ex.getMessage());
			throw new CustomAppException("Unable to reset password at this time. Please try again later.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
	}
	
	public String generatePassword() {
		Set<Integer> requiredLetter = new HashSet<Integer>(Arrays.asList(0,1,2,3)); 
		String newPassword = "";
		
		for (int i = 0; i< Constants.PASSWORD_LENGTH; i++) {
			if (i == 0) {
				int libIndex = (int) (Math.random() * 2);
				int index = (int) (Math.random() * PWD_CHAR_LIB_LETTER[libIndex].length());
				newPassword += PWD_CHAR_LIB_LETTER[libIndex].substring(index, index + 1);
				requiredLetter.remove(libIndex);
			} else if ((Constants.PASSWORD_LENGTH - i) == requiredLetter.size()) {
				for (Integer libIndex: requiredLetter) {
					int index = (int) (Math.random() * PWD_CHAR_LIB_LETTER[libIndex].length());
					newPassword += PWD_CHAR_LIB_LETTER[libIndex].substring(index, index + 1);
				}
			} else {
				int libIndex = (int) (Math.random() * 4);
				int index = (int) (Math.random() * PWD_CHAR_LIB_LETTER[libIndex].length());
				newPassword += PWD_CHAR_LIB_LETTER[libIndex].substring(index, index + 1);
				requiredLetter.remove(libIndex);
			}
		}
		return newPassword;
	}
	
	private boolean validateRoles(UserRoles userRoles) {
		if (userRoles == null) {
			return false;
		}
		List<String> allRoles = userRoles.getSiebelRoles();
		if (allRoles == null || allRoles.isEmpty()) {
			return false;
		}
		List<Role> appRoles = new ArrayList<Role>();
		boolean hasRole = false;
		
		for (String role:allRoles) {
			if (ROLE_MAPPING.containsKey(role)) {
				hasRole = true;
				appRoles.add(Role.valueOf(ROLE_MAPPING.get(role).getDescripShort()));
			}
		}
		userRoles.setAppRoles(appRoles);
		return hasRole;
	}
	
	private String[] generateStringRolesArray(UserRoles userRoles) {
		List<Role> roles = userRoles.getAppRoles();
		String[] rolesArray = new String[roles.size()];
		
		for (int i=0; i<roles.size(); i++) {
			rolesArray[i] = roles.get(i).getRole();
		}
		
		return rolesArray;
	}
}
