package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AccountsCreateInput implements InputValidation {
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
			user_info.setContact_id(null);
			user_info.setProfile_type(null);
			user_info.setAddress_id(null);
			user_info.setOld_password(null);
			user_info.setNew_password(null);
			user_info.setStatus(null);
			
			errorMsg = this.user_info.dataValidation(user_info);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		String firstname = this.user_info.getFirstname();
		String lastname = this.user_info.getLastname();
		String address1 = this.user_info.getAddress1();
		String city = this.user_info.getCity();
		String state = this.user_info.getState();
		String zip = this.user_info.getZip();
		String preferred_contact_method = this.user_info.getPreferred_contact_method();
		String email = this.user_info.getEmail();
		String preferred_phone_type = this.user_info.getPreferred_phone_type();
		String cell_phone = this.user_info.getCell_phone();
		String home_phone = this.user_info.getHome_phone();
		String work_phone = this.user_info.getWork_phone();
		String username = this.user_info.getUsername();
		String password = this.user_info.getPassword();
		String security_q = this.user_info.getSecurity_q();
		String security_a = this.user_info.getSecurity_a();
		
		if (firstname == null || firstname.trim().equals("")) {
			errorMsg += "First Name is empty. ";
		}
		if (lastname == null || lastname.trim().equals("")) {
			errorMsg += "Last Name is empty. ";
		}
		if (address1 == null || address1.trim().equals("")) {
			errorMsg += "Address 1 is empty. ";
		}
		if (city == null || city.trim().equals("")) {
			errorMsg += "City is empty. ";
		}
		if (state == null || state.trim().equals("")) {
			errorMsg += "State is empty. ";
		}
		if (zip == null || zip.trim().equals("")) {
			errorMsg += "Zip is empty. ";
		}
		if (preferred_contact_method == null || preferred_contact_method.trim().equals("")) {
			errorMsg += "Preferred Contact Method is empty. ";
		}
		if (email == null || email.trim().equals("")) {
			errorMsg += "Email is empty. ";
		}
		if (preferred_phone_type == null || preferred_phone_type.trim().equals("")) {
			errorMsg += "Preferred Phone Type is empty. ";
		}
		if (	(cell_phone == null || cell_phone.trim().equals("")) &&
				(home_phone == null || home_phone.trim().equals("")) &&
				(work_phone == null || work_phone.trim().equals(""))) {
			errorMsg += "Phone Number is empty. ";
		}
		if (username == null || username.trim().equals("")) {
			errorMsg += "Username is empty. ";
		}
		if (password == null || password.trim().equals("")) {
			errorMsg += "Password is empty. ";
		}
		if (security_q == null || security_q.trim().equals("")) {
			errorMsg += "Security Question is empty. ";
		}
		if (security_a == null || security_a.trim().equals("")) {
			errorMsg += "Security Answer is empty. ";
		}
		
		return errorMsg;
	}
}
