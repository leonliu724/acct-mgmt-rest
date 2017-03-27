package com.conduent.ts.ops.restapi.config;

import java.util.Date;

public class OSCodes {
	private long codeId;
	private String descripShort;
	private String descripLong;
	private String codeType;
	private Date updateDate;
	
	public long getCodeId() {
		return codeId;
	}
	public void setCodeId(long codeId) {
		this.codeId = codeId;
	}
	public String getDescripShort() {
		return descripShort;
	}
	public void setDescripShort(String descripShort) {
		this.descripShort = descripShort;
	}
	public String getDescripLong() {
		return descripLong;
	}
	public void setDescripLong(String descripLong) {
		this.descripLong = descripLong;
	}
	public String getCodeType() {
		return codeType;
	}
	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
}
