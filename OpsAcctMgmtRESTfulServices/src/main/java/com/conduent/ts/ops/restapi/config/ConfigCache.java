package com.conduent.ts.ops.restapi.config;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.database.RESTfulServicesDAO;
import com.conduent.ts.ops.restapi.siebel.SessionToken;


public class ConfigCache {
	private static boolean configLoaded = false;
	private static short agencyId = 0;
	private static Map<String,OSProcessParameters> parametersMap = null;
	private static Map<String,OSCodes> codesMap = null;
	private static CopyOnWriteArrayList<SessionToken> tokenList = new CopyOnWriteArrayList<SessionToken>();
	
	private static Logger logger = Logger.getLogger(ConfigCache.class.getName());
	
	public static void loadConfigCache() {
		if (!configLoaded) {
			try {
				loadAgencyId();
				loadProcessParameters();
				loadCodes();
				configLoaded = true;
			} catch (Exception e) {
				logger.error("Error Loading configuration parameters to cache");
			}
		}
	}
	
	public static OSProcessParameters getProcessParameter(String paramName) {
		return parametersMap.get(paramName);
	}
	
	public static OSCodes getCodes(String longDescription) {
		return codesMap.get(longDescription);
	}
	
	public static OSCodes getCodesByShortDesc(String shortDescription) {
		for (OSCodes osCodes:codesMap.values()) {
			if (osCodes.getDescripShort().equals(shortDescription)) {
				return osCodes;
			}
		}
		return null;
	}
	
	public static Map<String,OSCodes> getCodesMap() {
		return codesMap;
	}
	
	public static boolean isSiebelTokenAvailable() {
		if (tokenList.isEmpty()) {
			return false;
		}
		
		SessionToken token = tokenList.get(0);
		Date currentDate = new Date();
		
		return token.getExpiryDate().compareTo(currentDate) > 0;
	}
	
	public static String getSiebelToken() {
		return tokenList.get(0).getToken();
	}
	
	public static void setSiebelToken(String token) {
		SessionToken sessionToken = new SessionToken();
		Calendar currentCal = Calendar.getInstance();
		int validTokenMinutes = Integer.parseInt(parametersMap.get(Constants.PARAM_NAME_SIEBEL_WS_TOKEN_VALID_MINUTES).getParamValue());
		currentCal.add(Calendar.MINUTE, validTokenMinutes);
		
		sessionToken.setToken(token);
		sessionToken.setExpiryDate(currentCal.getTime());
		
		if (tokenList.isEmpty()) {
			tokenList.add(sessionToken);
		} else {
			tokenList.set(0, sessionToken);
		}
		
	}
	
	
	private static void loadAgencyId() throws Exception {
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		
		agencyId = dao.getAgencyId();
		if (agencyId == 0) {
			throw new Exception("Error loading Agency ID");
		}
		logger.info("&&&&&&------Agency ID loaded------&&&&&&");
	}
	
	private static void loadProcessParameters() throws Exception {
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		
		List<OSProcessParameters> parametersList = dao.getProcessParameters(agencyId, Constants.REST_PARAM_GROUP);
		if (parametersList.isEmpty()) {
			throw new Exception("Error loading Process Parameters");
		}
		
		parametersMap = new HashMap<String,OSProcessParameters>();
		for (OSProcessParameters parameter:parametersList) {
			parametersMap.put(parameter.getParamName(), parameter);
		}
		
		logger.info("&&&&&&------Process Parameters loaded------&&&&&&");
	}
	
	private static void loadCodes() throws Exception {
		RESTfulServicesDAO dao = new RESTfulServicesDAO();
		
		List<OSCodes> codesList = dao.getCodes(Constants.REST_ROLE_IND);
		if (codesList.isEmpty()) {
			throw new Exception("Error loading Codes");
		}
		
		codesMap = new HashMap<String,OSCodes>();
		for (OSCodes code:codesList) {
			codesMap.put(code.getDescripLong(), code);
		}
		
		logger.info("&&&&&&------Codes loaded------&&&&&&");
	}
}
