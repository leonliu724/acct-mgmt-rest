package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountsChangePasswordInput implements InputValidation {
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
			flagObject.setUsername("");
			flagObject.setOld_password("");
			flagObject.setNew_password("");
			
			errorMsg = this.user_info.dataValidation(flagObject);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		String username = this.user_info.getUsername();
		String old_password = this.user_info.getOld_password();
		String new_password = this.user_info.getNew_password();
		
		if (username == null || username.trim().equals("")) {
			errorMsg += "Username is empty. ";
		}
		if (old_password == null || old_password.trim().equals("")) {
			errorMsg += "Old Password is empty. ";
		}
		if (new_password == null || new_password.trim().equals("")) {
			errorMsg += "New Password is empty. ";
		}
		
		return errorMsg;
	}
	
}
