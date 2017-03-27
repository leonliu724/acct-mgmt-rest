package com.conduent.ts.ops.restapi.siebel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.config.Constants;
import com.conduent.ts.ops.restapi.dto.AddressInfo;
import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.conduent.ts.ops.restapi.exceptions.CustomAppException;

import createacct.com.siebel.customui.ATLCustomerRegistrationProcess;
import createacct.com.siebel.customui.ATLCustomerRegistrationProcess_Service;
import createacct.com.siebel.customui.CustomerRegistration1Input;
import createacct.com.siebel.customui.CustomerRegistration1Output;
import createacct.com.siebel.xml.customerreg.CustomerReg;
import createacct.com.siebel.xml.customerreg.CustomerRegReqData;
import findacct.com.siebel.customui.ATLFindAccountProcess;
import findacct.com.siebel.customui.ATLFindAccountProcessInput;
import findacct.com.siebel.customui.ATLFindAccountProcessOutput;
import findacct.com.siebel.customui.ATLFindAccountProcess_Service;
import findacct.com.siebel.xml.accountfindresponse.FindAccountInfoDef;
import findacct.com.siebel.xml.accountfindresponse.FindAccountReplyData;
import findacct.com.siebel.xml.findaccount.FindAccount;
import findacct.com.siebel.xml.findaccount.FindAccountReqData;
import notify.com.siebel.customui.AMSpcServices;
import notify.com.siebel.customui.NotifyCustomer;
import notify.com.siebel.customui.NotifyCustomerInput;
import notify.com.siebel.customui.NotifyCustomerOutput;
import notify.com.siebel.xml.notifycustomerreq.NotifyCustomerRequest;
import updtacct.com.siebel.customui.ATLCustomerProfileUpateProcess;
import updtacct.com.siebel.customui.ATLCustomerProfileUpateProcess_Service;
import updtacct.com.siebel.customui.CustomerProfileUpdInput;
import updtacct.com.siebel.customui.CustomerProfileUpdOutput;
import updtacct.com.siebel.xml.customerupdate.CustomerProfileUpdateReqData;
import updtacct.com.siebel.xml.customerupdate.CustomerUpdate;

public class SiebelServicesClient {
	
	private static Logger logger = Logger.getLogger(SiebelServicesClient.class.getName());
	private DateFormat df = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
	
	/**
	 * Send email notification for resetting password
	 * 
	 * @param contact_id
	 * @param newPassword
	 */
	public void notifyCustomerResetPassword(String contact_id, String newPassword) {
		logger.info("Start");
		
		AMSpcServices amSpcServices = new AMSpcServices();
		SOAPHeaderHandlerResolver handlerResolver = new SOAPHeaderHandlerResolver();
		amSpcServices.setHandlerResolver(handlerResolver);
		NotifyCustomer notifyCustomer = amSpcServices.getNotifyCustomer();
		
		NotifyCustomerInput notifyCustomerInput = new NotifyCustomerInput();
		NotifyCustomerRequest notifyCustomerRequest = new NotifyCustomerRequest();
		notify.com.siebel.xml.notifycustomerreq.SessionData sessionData = new notify.com.siebel.xml.notifycustomerreq.SessionData();
		
		sessionData.setSessionID(UUID.randomUUID().toString());
		sessionData.setEquipmentID(Constants.SIEBEL_WS_REQUEST_SOURCE);
		sessionData.setRequestDate(df.format(new Date()));
		sessionData.setRequestSource(Constants.SIEBEL_WS_REQUEST_SOURCE);
		notifyCustomerRequest.setSessionData(sessionData);
		notifyCustomerRequest.setContactID(contact_id);
		notifyCustomerRequest.setSourceID(contact_id);
		notifyCustomerRequest.setSourceObject("Contact");
		notifyCustomerRequest.setTemplateName("ATL Forgot Password");
		notifyCustomerRequest.setPassword(newPassword);
		notifyCustomerInput.setNotifyCustomerRequest(notifyCustomerRequest);
		
		try {
			NotifyCustomerOutput notifyCustomerOutput = notifyCustomer.notifyCustomer(notifyCustomerInput);
			String statusCode = notifyCustomerOutput.getNotifyCustomerResponse().getStatusCode();
			
			if (statusCode == null || !statusCode.equals("0000")) {
				throw new Exception("Failed to send email. StatusCode: " + statusCode + "; Message: " + notifyCustomerOutput.getNotifyCustomerResponse().getStatusMessage());
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new CustomAppException("Password was reset. Failed to send notification email.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		logger.info("End");
	}
	
	/**
	 * Send email notification for changing password
	 * 
	 * @param contact_id
	 */
	public void notifyCustomerChangePassword(String contact_id) {
		logger.info("Start");
		
		AMSpcServices amSpcServices = new AMSpcServices();
		SOAPHeaderHandlerResolver handlerResolver = new SOAPHeaderHandlerResolver();
		amSpcServices.setHandlerResolver(handlerResolver);
		NotifyCustomer notifyCustomer = amSpcServices.getNotifyCustomer();
		
		NotifyCustomerInput notifyCustomerInput = new NotifyCustomerInput();
		NotifyCustomerRequest notifyCustomerRequest = new NotifyCustomerRequest();
		notify.com.siebel.xml.notifycustomerreq.SessionData sessionData = new notify.com.siebel.xml.notifycustomerreq.SessionData();
		
		sessionData.setSessionID(UUID.randomUUID().toString());
		sessionData.setEquipmentID(Constants.SIEBEL_WS_REQUEST_SOURCE);
		sessionData.setRequestDate(df.format(new Date()));
		sessionData.setRequestSource(Constants.SIEBEL_WS_REQUEST_SOURCE);
		notifyCustomerRequest.setSessionData(sessionData);
		notifyCustomerRequest.setContactID(contact_id);
		notifyCustomerRequest.setSourceID(contact_id);
		notifyCustomerRequest.setSourceObject("Contact");
		notifyCustomerRequest.setTemplateName("ATL Change Password");
		notifyCustomerInput.setNotifyCustomerRequest(notifyCustomerRequest);
		
		try {
			NotifyCustomerOutput notifyCustomerOutput = notifyCustomer.notifyCustomer(notifyCustomerInput);
			String statusCode = notifyCustomerOutput.getNotifyCustomerResponse().getStatusCode();
			
			if (statusCode == null || !statusCode.equals("0000")) {
				throw new Exception("Failed to send email. StatusCode: " + statusCode + "; Message: " + notifyCustomerOutput.getNotifyCustomerResponse().getStatusMessage());
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new CustomAppException("Password was changed. Failed to send notification email.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		logger.info("End");
	}
	
	/**
	 * Find account that matches the search criteria. Result only returns the first record 
	 * that matches.
	 * 
	 * @param userInfo
	 * @return
	 */
	public UserInfo findAccount(UserInfo userInfo, boolean loadData) {
		logger.info("Start");
		
		ATLFindAccountProcess_Service findAccountProcessService = new ATLFindAccountProcess_Service();
		SOAPHeaderHandlerResolver handlerResolver = new SOAPHeaderHandlerResolver();
		findAccountProcessService.setHandlerResolver(handlerResolver);
		ATLFindAccountProcess findAccountProcess = findAccountProcessService.getATLFindAccountProcess();
		
		ATLFindAccountProcessInput findAccountProcessInput = new ATLFindAccountProcessInput();
		FindAccount findAccount = new FindAccount();
		FindAccountReqData findAccountReqData = new FindAccountReqData();
		findacct.com.siebel.xml.findaccount.SessionData sessionData = new findacct.com.siebel.xml.findaccount.SessionData();
		
		sessionData.setSessionId(UUID.randomUUID().toString());
		sessionData.setEquipmentId(Constants.SIEBEL_WS_REQUEST_SOURCE);
		findAccountReqData.setRequestDate(df.format(new Date()));
		findAccountReqData.setRequestSource(Constants.SIEBEL_WS_REQUEST_SOURCE);
		findAccountReqData.setLastName(userInfo.getLastname());
		findAccountReqData.setFirstName(userInfo.getFirstname());
		findAccountReqData.setZipCode(userInfo.getZip());
		findAccountReqData.setEmailID(userInfo.getEmail());
		findAccountReqData.setUserName(userInfo.getUsername());
		findAccountReqData.setContactID(userInfo.getContact_id());
		findAccountReqData.setSessionData(sessionData);
		findAccount.setFindAccountReqData(findAccountReqData);
		findAccountProcessInput.setFindAccount(findAccount);
		
		userInfo = null;
		
		try {
			ATLFindAccountProcessOutput findAccountProcessOutput = findAccountProcess.atlFindAccountProcess(findAccountProcessInput);
			List<FindAccountReplyData> findAccountReplyDataList = findAccountProcessOutput.getAccountFindResponse().getFindAccountReplyData();
			String statusCode = findAccountReplyDataList.get(0).getStatusCode();
			
			if (statusCode == null || !statusCode.equals("0000")) {
				logger.debug("No record found.");
			} else {
				userInfo = new UserInfo();
				if (loadData) {
					FindAccountInfoDef findAccountInfoDef = findAccountReplyDataList.get(0).getListOfContacts().getFindAccountInfoDef().get(0);
					
					userInfo.setContact_id(findAccountInfoDef.getContactID());
					userInfo.setFirstname(findAccountInfoDef.getFirstName());
					userInfo.setLastname(findAccountInfoDef.getLastName());
					userInfo.setEmail(findAccountInfoDef.getEmailID());
					userInfo.setCell_phone(findAccountInfoDef.getPrimaryPhoneNo());
					userInfo.setHome_phone(findAccountInfoDef.getAttrib08());
					//TODO get title, language, contact method, phone type
					userInfo.setUsername(findAccountInfoDef.getUserName());
					userInfo.setProfile_type(findAccountInfoDef.getAttrib02());
					userInfo.setStatus(findAccountInfoDef.getAttrib06());
					userInfo.setMiddlename(findAccountInfoDef.getAttrib07());
					userInfo.setSecurity_q(findAccountInfoDef.getSecInfo().getSecQuestion1());
					userInfo.setSecurity_a(findAccountInfoDef.getSecInfo().getSecAnswer1());
					
					List<findacct.com.siebel.xml.accountfindresponse.AddrInfo> addrInfoList = findAccountInfoDef.getAddrInfo();
					for (int i = 0; i < addrInfoList.size(); i++) {
						findacct.com.siebel.xml.accountfindresponse.AddrInfo addrInfo = addrInfoList.get(i);
						if (addrInfo.getAttrib02() != null && addrInfo.getAttrib02().equals("Y")) {
							userInfo.setAddress_id(addrInfo.getAttrib01());
							userInfo.setAddress1(addrInfo.getAddressLine1());
							userInfo.setAddress2(addrInfo.getAddressLine2());
							userInfo.setCity(addrInfo.getCity());
							userInfo.setState(addrInfo.getState());
							userInfo.setZip(addrInfo.getZip());
							userInfo.setCountry(addrInfo.getCountry());
							break;
						}
						
						if (i == 0) {
							userInfo.setAddress_id(addrInfo.getAttrib01());
							userInfo.setAddress1(addrInfo.getAddressLine1());
							userInfo.setAddress2(addrInfo.getAddressLine2());
							userInfo.setCity(addrInfo.getCity());
							userInfo.setState(addrInfo.getState());
							userInfo.setZip(addrInfo.getZip());
							userInfo.setCountry(addrInfo.getCountry());
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new CustomAppException("An internal error occurred when trying to fetch data.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		logger.info("End");
		return userInfo;
	}
	
	/**
	 * Register account for retail customer
	 * 
	 * @param userInfo
	 */
	public void registerAccount(UserInfo userInfo) {
		logger.info("Start");
		
		ATLCustomerRegistrationProcess_Service registrationProcessService = new ATLCustomerRegistrationProcess_Service();
		SOAPHeaderHandlerResolver handlerResolver = new SOAPHeaderHandlerResolver();
		registrationProcessService.setHandlerResolver(handlerResolver);
		ATLCustomerRegistrationProcess registrationProcess = registrationProcessService.getATLCustomerRegistrationProcess();
		
		CustomerRegistration1Input customerRegistration1Input = new CustomerRegistration1Input();
		CustomerReg customerReg = new CustomerReg();
		CustomerRegReqData customerRegReqData = new CustomerRegReqData();
		createacct.com.siebel.xml.customerreg.SessionData sessionData = new createacct.com.siebel.xml.customerreg.SessionData();
		createacct.com.siebel.xml.customerreg.AddressInfo addressInfo = new createacct.com.siebel.xml.customerreg.AddressInfo();
		createacct.com.siebel.xml.customerreg.SecInfo secInfo = new createacct.com.siebel.xml.customerreg.SecInfo();
		
		sessionData.setSessionId(UUID.randomUUID().toString());
		sessionData.setEquipmentId(Constants.SIEBEL_WS_REQUEST_SOURCE);
		addressInfo.setAddressLine1(userInfo.getAddress1());
		addressInfo.setAddressLine2(userInfo.getAddress2());
		addressInfo.setCity(userInfo.getCity());
		addressInfo.setState(userInfo.getState());
		addressInfo.setZip(userInfo.getZip());
		addressInfo.setCountry(userInfo.getCountry());
		secInfo.setSecQuestion1(userInfo.getSecurity_q());
		secInfo.setSecAnswer1(userInfo.getSecurity_a());
		customerRegReqData.setRequestDate(df.format(new Date()));
		customerRegReqData.setRequestSource(Constants.SIEBEL_WS_REQUEST_SOURCE);
		customerRegReqData.setTitle(userInfo.getTitle());
		customerRegReqData.setFirstNam(userInfo.getFirstname());
		customerRegReqData.setMiddleInit(userInfo.getMiddlename());
		customerRegReqData.setLastName(userInfo.getLastname());
		customerRegReqData.setEmailID(userInfo.getEmail());
		customerRegReqData.setHomePhoneNo(userInfo.getHome_phone());
		customerRegReqData.setCellPhoneNo(userInfo.getCell_phone());
		customerRegReqData.setUserName(userInfo.getUsername());
		customerRegReqData.setPassword(userInfo.getPassword());
		customerRegReqData.setPreferLanguage(userInfo.getPreferred_language());
		customerRegReqData.setPrefCommType(userInfo.getPreferred_contact_method());
		customerRegReqData.setPrimaryPhoneNo(userInfo.getPreferred_phone_type());
		customerRegReqData.setSessionData(sessionData);
		customerRegReqData.setAddressInfo(addressInfo);
		customerRegReqData.setSecInfo(secInfo);
		customerReg.setCustomerRegReqData(customerRegReqData);
		customerRegistration1Input.setCustomerReg(customerReg);
		
		try {
			CustomerRegistration1Output customerRegistration1Output = registrationProcess.customerRegistration1(customerRegistration1Input);
			String statusCode = customerRegistration1Output.getCustomerRegResponse().getCustomerRegReplyData().getStatusCode();
			
			if (statusCode == null || !statusCode.equals("0000")) {
				throw new Exception("Failed to create account. StatusCode: " + statusCode + "; Message: " +customerRegistration1Output.getCustomerRegResponse().getCustomerRegReplyData().getAttrib01());
			} else {
				userInfo.setContact_id(customerRegistration1Output.getCustomerRegResponse().getCustomerRegReplyData().getContactID());
				userInfo.setProfile_type(customerRegistration1Output.getCustomerRegResponse().getCustomerRegReplyData().getAttrib03());
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new CustomAppException("An internal error occurred when trying to post data.", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		logger.info("End");
	}
	
	/**
	 * Update account profile for retail customer
	 * 
	 * @param info
	 */
	public void updateAccount(UserInfo userInfo) {
		logger.info("Start");
		
		ATLCustomerProfileUpateProcess_Service profileUpdateProcessService = new ATLCustomerProfileUpateProcess_Service();
		SOAPHeaderHandlerResolver handlerResolver = new SOAPHeaderHandlerResolver();
		profileUpdateProcessService.setHandlerResolver(handlerResolver);
		ATLCustomerProfileUpateProcess profileUpdateProcess = profileUpdateProcessService.getATLCustomerProfileUpateProcess();
		
		CustomerProfileUpdInput customerProfileUpdInput = new CustomerProfileUpdInput();
		CustomerUpdate customerUpdate = new CustomerUpdate();
		CustomerProfileUpdateReqData customerProfileUpdateReqData = new CustomerProfileUpdateReqData();
		updtacct.com.siebel.xml.customerupdate.SessionData sessionData = new updtacct.com.siebel.xml.customerupdate.SessionData();
		updtacct.com.siebel.xml.customerupdate.AddressInfo addressInfo = new updtacct.com.siebel.xml.customerupdate.AddressInfo();
		updtacct.com.siebel.xml.customerupdate.SecInfo secInfo = new updtacct.com.siebel.xml.customerupdate.SecInfo();
		
		sessionData.setSessionId(UUID.randomUUID().toString());
		sessionData.setEquipmentId(Constants.SIEBEL_WS_REQUEST_SOURCE);
		addressInfo.setAddressId(userInfo.getAddress_id());
		addressInfo.setAddressLine1(userInfo.getAddress1());
		addressInfo.setAddressLine2(userInfo.getAddress2());
		addressInfo.setCity(userInfo.getCity());
		addressInfo.setState(userInfo.getState());
		addressInfo.setZip(userInfo.getZip());
		addressInfo.setCountry(userInfo.getCountry());
		secInfo.setSecQuestion1(userInfo.getSecurity_q());
		secInfo.setSecAnswer1(userInfo.getSecurity_a());
		customerProfileUpdateReqData.setRequestDate(df.format(new Date()));
		customerProfileUpdateReqData.setRequestSource(Constants.SIEBEL_WS_REQUEST_SOURCE);
		customerProfileUpdateReqData.setContactID(userInfo.getContact_id());
		customerProfileUpdateReqData.setTitle(userInfo.getTitle());
		customerProfileUpdateReqData.setFirstNam(userInfo.getFirstname());
		customerProfileUpdateReqData.setMiddleInit(userInfo.getMiddlename());
		customerProfileUpdateReqData.setLastName(userInfo.getLastname());
		customerProfileUpdateReqData.setEmailID(userInfo.getEmail());
		customerProfileUpdateReqData.setHomePhoneNo(userInfo.getHome_phone());
		customerProfileUpdateReqData.setCellPhoneNo(userInfo.getCell_phone());
		customerProfileUpdateReqData.setPreferLanguage(userInfo.getPreferred_language());
		customerProfileUpdateReqData.setPrefPhoneType(userInfo.getPreferred_phone_type());
		customerProfileUpdateReqData.setPrefCommType(userInfo.getPreferred_contact_method());
		customerProfileUpdateReqData.setSessionData(sessionData);
		customerProfileUpdateReqData.setAddressInfo(addressInfo);
		customerProfileUpdateReqData.setSecInfo(secInfo);
		customerUpdate.setCustomerProfileUpdateReqData(customerProfileUpdateReqData);
		customerProfileUpdInput.setCustomerUpdate(customerUpdate);
		
		try {
			CustomerProfileUpdOutput customerProfileUpdOutput = profileUpdateProcess.customerProfileUpd(customerProfileUpdInput);
			String statusCode = customerProfileUpdOutput.getCustomerUpdResponse().getCustomerProfielUpdReplyData().getStatusCode();
			
			if (statusCode == null || !statusCode.equals("0000")) {
				throw new Exception("Failed to update Account. StatusCode: " + statusCode);
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			throw new CustomAppException("Unable to update account. ", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
		}
		
		logger.info("End");
	}
	
	/**
	 * Update address info for an existing address profile
	 * 
	 * @param addressInfo
	 */
	public void updateAddress(AddressInfo addressInfo) {
		
		
		
		
	}
}
