package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountsUpdateInput implements InputValidation {
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
			user_info.setProfile_type(null);
			user_info.setUsername(null);
			user_info.setPassword(null);
			user_info.setOld_password(null);
			user_info.setNew_password(null);
			user_info.setStatus(null);
			
			errorMsg = this.user_info.dataValidation(user_info);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		
		String contact_id = this.user_info.getContact_id();
		String address_id = this.user_info.getAddress_id();
		String address1 = this.user_info.getAddress1();
		String address2 = this.user_info.getAddress2();
		String city = this.user_info.getCity();
		String state = this.user_info.getState();
		String zip = this.user_info.getZip();
		String country = this.user_info.getCountry();
		
		address1 = address1==null?"":address1.trim();
		address2 = address2==null?"":address2.trim();
		city = city==null?"":city.trim();
		state = state==null?"":state.trim();
		zip = zip==null?"":zip.trim();
		country = country==null?"":country.trim();
		
		if (contact_id == null || contact_id.trim().equals("")) {
			errorMsg += "Contact ID is empty";
		}
		
		if (	!address1.equals("") || !address2.equals("") ||
				!city.equals("") || !state.equals("") ||
				!zip.equals("") || !country.equals("")) {
			
			if (address_id == null || address_id.trim().equals("")) {
				errorMsg += "Address ID is empty";
			}
		}
		
		return errorMsg;
	}
	
}
