package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountsForgotInput implements InputValidation {
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
			flagObject.setFirstname("");
			flagObject.setLastname("");
			flagObject.setEmail("");
			
			errorMsg = this.user_info.dataValidation(flagObject);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		String firstname = this.user_info.getFirstname();
		String lastname = this.user_info.getLastname();
		String email = this.user_info.getEmail();
		
		if (firstname == null || firstname.trim().equals("")) {
			errorMsg += "First Name is empty. ";
		}
		if (lastname == null || lastname.trim().equals("")) {
			errorMsg += "Last Name is empty. ";
		}
		if (email == null || email.trim().equals("")) {
			errorMsg += "Email is empty. ";
		}
		
		return errorMsg;
	}
	
}
