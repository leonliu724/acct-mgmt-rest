package com.conduent.ts.ops.restapi.input;

import com.conduent.ts.ops.restapi.dto.AddressInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AddressesUpdateInput {
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
			errorMsg = this.address_info.dataValidation(address_info);
		} 
		
		return errorMsg;
	}

	public String nullValidation() {
		String errorMsg = "";
		
		String address_id = this.address_info.getAddress_id();
		
		if (address_id == null || address_id.trim().equals("")) {
			errorMsg += "Address ID is empty. ";
		}
		
		return errorMsg;
	}
}
