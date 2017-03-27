package com.conduent.ts.ops.restapi.dto;

import com.conduent.ts.ops.restapi.util.Validator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown=true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressInfo implements DataValidation {
	
	private String address_id;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zip;
	private String country;
	
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

	public String dataValidation(Object flagObject) {
		AddressInfo validationFlag = (AddressInfo) flagObject;
		
		String errorMsg = "";
		
		
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
		
		return errorMsg;
	}
	
}
