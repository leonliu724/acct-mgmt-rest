package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountsResetInput implements InputValidation {
	private UserInfo user_info;

	public UserInfo getUser_info() {
		return user_info;
	}

	public void setUser_info(UserInfo user_info) {
		this.user_info = user_info;
	}
	
	public String dataValidation() {
		String errorMsg = nullValidation();
		
		if (errorMsg.equals("")) {
			UserInfo flagObject = new UserInfo();
			flagObject.setContact_id("");
			flagObject.setSecurity_a("");
			
			errorMsg = this.user_info.dataValidation(flagObject);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		String contact_id = this.user_info.getContact_id();
		String security_a = this.user_info.getSecurity_a();
		
		if (contact_id == null || contact_id.trim().equals("")) {
			errorMsg += "Contact ID is empty. ";
		}
		if (security_a == null || security_a.trim().equals("")) {
			errorMsg += "Security Answer is empty. ";
		}
		
		return errorMsg;
	}
}
