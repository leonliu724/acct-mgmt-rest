package com.conduent.ts.ops.restapi.config;

import java.util.Date;

public class OSProcessParameters {
	private long processParameterId;
	private short agencyId;
	private String paramName;
	private String paramGroup;
	private String paramCode;
	private String paramValue;
	private Date updateDate;
	
	public long getProcessParameterId() {
		return processParameterId;
	}
	public void setProcessParameterId(long processParameterId) {
		this.processParameterId = processParameterId;
	}
	public short getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(short agencyId) {
		this.agencyId = agencyId;
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getParamGroup() {
		return paramGroup;
	}
	public void setParamGroup(String paramGroup) {
		this.paramGroup = paramGroup;
	}
	public String getParamCode() {
		return paramCode;
	}
	public void setParamCode(String paramCode) {
		this.paramCode = paramCode;
	}
	public String getParamValue() {
		return paramValue;
	}
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
