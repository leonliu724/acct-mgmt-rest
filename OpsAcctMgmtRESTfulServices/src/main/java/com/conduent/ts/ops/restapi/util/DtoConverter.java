package com.conduent.ts.ops.restapi.util;

import java.util.ArrayList;
import java.util.List;

import com.conduent.ts.ops.restapi.dto.Product;

import authenticate.com.siebel.customui.AuthenticateOutput;
import authenticate.com.siebel.xml.authenticateresponse.ProductInfo;

public class DtoConverter {
	
	public static List<Product> covertFromAuthenticateOutputToProductList(AuthenticateOutput authenticateOutput) throws Exception {
		List<Product> productList = new ArrayList<Product>();
		List<ProductInfo> productInfoList = authenticateOutput.getAuthenticateResponse()
											.getReplyData().getListOfEligibleProducts().getProductInfo();
		
		if (productInfoList != null && !productInfoList.isEmpty()) {
			for (ProductInfo productInfo:productInfoList) {
				Product product = new Product();
				product.setProduct_id(productInfo.getProductID());
				product.setProduct_name(productInfo.getProductDesc());
				product.setProduct_price(Double.parseDouble(productInfo.getPrice()));
				product.setProduct_category(productInfo.getAttrib01());
				productList.add(product);
			}
		}
		
		return productList;
	}
}
