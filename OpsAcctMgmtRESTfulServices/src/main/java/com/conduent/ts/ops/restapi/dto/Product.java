package com.conduent.ts.ops.restapi.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Product {
	private String product_id;
	private String product_name;
	private String product_desc;
	private Double product_price;
	private String product_category;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="MM/dd/yyyy hh:mm:ss")
	private Date validity_start_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="MM/dd/yyyy hh:mm:ss")
	private Date validity_end_date;
	
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getProduct_desc() {
		return product_desc;
	}
	public void setProduct_desc(String product_desc) {
		this.product_desc = product_desc;
	}
	public Double getProduct_price() {
		return product_price;
	}
	public void setProduct_price(Double product_price) {
		this.product_price = product_price;
	}
	public String getProduct_category() {
		return product_category;
	}
	public void setProduct_category(String product_category) {
		this.product_category = product_category;
	}
	public Date getValidity_start_date() {
		return validity_start_date;
	}
	public void setValidity_start_date(Date validity_start_date) {
		this.validity_start_date = validity_start_date;
	}
}