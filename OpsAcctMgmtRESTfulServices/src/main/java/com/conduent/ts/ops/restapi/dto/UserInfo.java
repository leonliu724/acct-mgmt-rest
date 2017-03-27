package com.conduent.ts.ops.restapi.dto;

import com.conduent.ts.ops.restapi.util.Validator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfo implements DataValidation {
	
	private String contact_id;
	private String profile_type;
	private String title;
	private String firstname;
	private String middlename;
	private String lastname;
	private String preferred_language;
	private String address_id;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String country;
	private String preferred_contact_method;
	private String email;
	private String preferred_phone_type;
	private String cell_phone;
	private String home_phone;
	private String work_phone;
	private String username;
	private String password;
	private String old_password;
	private String new_password;
	private String security_q;
	private String security_a;
	private String status;
	
	public String getContact_id() {
		return contact_id;
	}
	public void setContact_id(String contact_id) {
		this.contact_id = contact_id;
	}
	public String getProfile_type() {
		return profile_type;
	}
	public void setProfile_type(String profile_type) {
		this.profile_type = profile_type;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getMiddlename() {
		return middlename;
	}
	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getPreferred_language() {
		return preferred_language;
	}
	public void setPreferred_language(String preferred_language) {
		this.preferred_language = preferred_language;
	}
	public String getAddress_id() {
		return address_id;
	}
	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPreferred_contact_method() {
		return preferred_contact_method;
	}
	public void setPreferred_contact_method(String preferred_contact_method) {
		this.preferred_contact_method = preferred_contact_method;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPreferred_phone_type() {
		return preferred_phone_type;
	}
	public void setPreferred_phone_type(String preferred_phone_type) {
		this.preferred_phone_type = preferred_phone_type;
	}
	public String getCell_phone() {
		return cell_phone;
	}
	public void setCell_phone(String cell_phone) {
		this.cell_phone = cell_phone;
	}
	public String getHome_phone() {
		return home_phone;
	}
	public void setHome_phone(String home_phone) {
		this.home_phone = home_phone;
	}
	public String getWork_phone() {
		return work_phone;
	}
	public void setWork_phone(String work_phone) {
		this.work_phone = work_phone;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSecurity_q() {
		return security_q;
	}
	public void setSecurity_q(String security_q) {
		this.security_q = security_q;
	}
	public String getSecurity_a() {
		return security_a;
	}
	public void setSecurity_a(String security_a) {
		this.security_a = security_a;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getOld_password() {
		return old_password;
	}
	public void setOld_password(String old_password) {
		this.old_password = old_password;
	}
	public String getNew_password() {
		return new_password;
	}
	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}
	
	public String dataValidation(Object flagObject) {
		UserInfo validationFlag = (UserInfo) flagObject;
		
		String errorMsg = "";
		
		if (validationFlag.getContact_id() != null) {
			contact_id = contact_id == null?null:contact_id.trim();
			if (!Validator.isAlphanumeric(contact_id, "\\-")) {
				errorMsg += "Invalid Contact ID. ";
			}
		}
		if (validationFlag.getTitle() != null) {
			title = title == null?null:title.trim();
			if (!Validator.isCharacter(title, "\\.")) {
				errorMsg += "Invalid Title. ";
			}
		}
		if (validationFlag.getFirstname() != null) {
			firstname = firstname == null?null:firstname.trim();
			if (!Validator.isName(firstname)) {
				errorMsg += "Invalid First Name. ";
			}
		}
		if (validationFlag.getMiddlename() != null) {
			middlename = middlename == null?null:middlename.trim();
			if (!Validator.isName(middlename)) {
				errorMsg += "Invalid Middle Name. ";
			}
		}
		if (validationFlag.getLastname() != null) {
			lastname = lastname == null?null:lastname.trim();
			if (!Validator.isName(lastname)) {
				errorMsg += "Invalid Last Name. ";
			}
		}
		if (validationFlag.getPreferred_language() != null) {
			preferred_language = preferred_language == null?null:preferred_language.trim();
			if (!Validator.isCharacter(preferred_language, "")) {
				errorMsg += "Invalid Preferred Language. ";
			}
		}
		if (validationFlag.getAddress_id() != null) {
			address_id = address_id == null?null:address_id.trim();
			if (!Validator.isAlphanumeric(address_id, "\\-")) {
				errorMsg += "Invalid Address ID. ";
			}
		}
		if (validationFlag.getAddress1() != null) {
			address1 = address1 == null?null:address1.trim();
			if (!Validator.isAddress(address1)) {
				errorMsg += "Invalid Address 1. ";
			}
		}
		if (validationFlag.getAddress2() != null) {
			address2 = address2 == null?null:address2.trim();
			if (!Validator.isAddress(address2)) {
				errorMsg += "Invalid Address 2. ";
			}
		}
		if (validationFlag.getCity() != null) {
			city = city == null?null:city.trim();
			if (!Validator.isCity(city)) {
				errorMsg += "Invalid City. ";
			}
		}
		if (validationFlag.getState() != null) {
			state = state == null?null:state.trim();
			if (!Validator.isCharacter(state, "")) {
				errorMsg += "Invalid State. ";
			}
		}
		if (validationFlag.getZip() != null) {
			zip = zip == null?null:zip.trim();
			if (!Validator.isZip(zip)) {
				errorMsg += "Invalid Zip Code. ";
			}
		}
		if (validationFlag.getCountry() != null) {
			country = country == null?null:country.trim();
			if (!Validator.isCharacter(country, "")) {
				errorMsg += "Invalid Country. ";
			}
		}
		if (validationFlag.getPreferred_contact_method() != null) {
			preferred_contact_method = preferred_contact_method == null?null:preferred_contact_method.trim();
			if (!Validator.isCharacter(preferred_contact_method, "")) {
				errorMsg += "Invalid Preferred Contact Method. ";
			}
		}
		if (validationFlag.getEmail() != null) {
			email = email == null?null:email.trim();
			if (!Validator.isEmail(email)) {
				errorMsg += "Invalid Email. ";
			}
		}
		if (validationFlag.getPreferred_phone_type() != null) {
			preferred_phone_type = preferred_phone_type == null?null:preferred_phone_type.trim();
			if (!Validator.isCharacter(preferred_phone_type, "")) {
				errorMsg += "Invalid Preferred Phone Type. ";
			}
		}
		if (validationFlag.getCell_phone() != null) {
			cell_phone = cell_phone == null?null:cell_phone.trim();
			if (!Validator.isPhone(cell_phone)) {
				errorMsg += "Invalid Cell Phone. ";
			}
		}
		if (validationFlag.getHome_phone() != null) {
			home_phone = home_phone == null?null:home_phone.trim();
			if (!Validator.isPhone(home_phone)) {
				errorMsg += "Invalid Home Phone. ";
			}
		}
		if (validationFlag.getWork_phone() != null) {
			work_phone = work_phone == null?null:work_phone.trim();
			if (!Validator.isPhone(work_phone)) {
				errorMsg += "Invalid Work Phone. ";
			}
		}
		if (validationFlag.getUsername() != null) {
			username = username == null?null:username.trim();
			if (!Validator.isUsername(username)) {
				errorMsg += "Invalid Username. ";
			}
		}
		if (validationFlag.getPassword() != null) {
			password = password == null?null:password.trim();
			if (!Validator.isPwd(password) || password.equals(username)) {
				errorMsg += "Invalid Password. ";
			}
		}
		if (validationFlag.getNew_password() != null) {
			new_password = new_password == null?null:new_password.trim();
			if (!Validator.isPwd(new_password) || new_password.equals(username)) {
				errorMsg += "Invalid New Password. ";
			}
		}
		if (validationFlag.getSecurity_q() != null) {
			security_q = security_q == null?null:security_q.trim();
			if (!Validator.isAlphanumeric(security_q, "\\s\\,\\.\\?\\-")) {
				errorMsg += "Invalid Security Question. ";
			}
		}
		if (validationFlag.getSecurity_a() != null) {
			security_a = security_a == null?null:security_a.trim();
			if (!Validator.isSecurityA(security_a)) {
				errorMsg += "Invalid Security Anwser. ";
			}
		}
		
		return errorMsg;
	}
}
