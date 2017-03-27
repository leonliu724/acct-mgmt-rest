package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.AddressInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AddressesCreateInput implements InputValidation {
	private AddressInfo address_info;

	public AddressInfo getAddress_info() {
		return address_info;
	}

	public void setAddress_info(AddressInfo address_info) {
		this.address_info = address_info;
	}
	
	public String dataValidation() {
		String errorMsg = nullValidation();
		
		if (errorMsg.equals("")) {
			address_info.setAddress_id(null);
			
			errorMsg = this.address_info.dataValidation(address_info);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		String address1 = this.address_info.getAddress1();
		String city = this.address_info.getCity();
		String state = this.address_info.getState();
		String zip = this.address_info.getZip();
		String country = this.address_info.getCountry();
		
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
		if (country == null || country.trim().equals("")) {
			errorMsg += "Country is empty. ";
		}
		
		return errorMsg;
	}
	
}
