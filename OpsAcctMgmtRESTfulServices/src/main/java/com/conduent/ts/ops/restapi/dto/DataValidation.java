package com.conduent.ts.ops.restapi.dto;

public interface DataValidation {
	
	/**
	 * Perform input validation for class fields. Any fields that is null in flagObject will not be validated.
	 * 
	 * @param flagObject
	 * @return error message after input validation
	 */
	public String dataValidation(Object flagObject);
}
