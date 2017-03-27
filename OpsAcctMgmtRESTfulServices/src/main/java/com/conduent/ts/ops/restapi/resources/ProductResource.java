package com.conduent.ts.ops.restapi.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.conduent.ts.ops.restapi.authentication.Role;
import com.conduent.ts.ops.restapi.authentication.SecureResource;
import com.conduent.ts.ops.restapi.dto.Product;
import com.conduent.ts.ops.restapi.util.DtoConverter;

import authenticate.com.siebel.customui.ATLAuthenticationProcess;
import authenticate.com.siebel.customui.ATLAuthenticationProcess_Service;
import authenticate.com.siebel.customui.AuthenticateInput;
import authenticate.com.siebel.customui.AuthenticateOutput;
import authenticate.com.siebel.xml.authenticate.Authenticate;
import authenticate.com.siebel.xml.authenticate.AuthenticationData;
import authenticate.com.siebel.xml.authenticate.CcInfo;
import authenticate.com.siebel.xml.authenticate.XmlDataDef;

@Path("/products")
public class ProductResource {
	
	@GET
	@Path("/no_secure")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProducts(@QueryParam("product_category") String product_category) {
		List<Product> productList = new ArrayList<Product>();
		
		ATLAuthenticationProcess_Service authProcessService = new ATLAuthenticationProcess_Service();
		ATLAuthenticationProcess authProcess = authProcessService.getATLAuthenticationProcess();
		
		AuthenticateInput authenticateInput = new AuthenticateInput();
		Authenticate authenticate = new Authenticate();
		AuthenticationData authenticateData = new AuthenticationData();
		XmlDataDef xmlDataDef = new XmlDataDef();
		CcInfo ccInfo = new CcInfo();
		
		authenticateData.setRequestSource("FVD");
		ccInfo.setFareMediaNo("0000000000000000");
		
		xmlDataDef.setCcInfo(ccInfo);
		authenticateData.setXmlDataDef(xmlDataDef);
		authenticate.setAuthenticationData(authenticateData);
		authenticateInput.setAuthenticate(authenticate);
		
		AuthenticateOutput authenticateOutput = authProcess.authenticate(authenticateInput);
		try {
			productList = DtoConverter.covertFromAuthenticateOutputToProductList(authenticateOutput);
		} catch (Exception e) {
		}
		
		return Response.ok(productList).build();
	}
	
	@GET
	@Path("secure")
	@Produces(MediaType.APPLICATION_JSON)
	@SecureResource({Role.INDV})
	public List<Product> getProductsSecure(@QueryParam("product_category") String product_category) {
		List<Product> productList = new ArrayList<Product>();
		
		ATLAuthenticationProcess_Service authProcessService = new ATLAuthenticationProcess_Service();
		ATLAuthenticationProcess authProcess = authProcessService.getATLAuthenticationProcess();
		
		AuthenticateInput authenticateInput = new AuthenticateInput();
		Authenticate authenticate = new Authenticate();
		AuthenticationData authenticateData = new AuthenticationData();
		XmlDataDef xmlDataDef = new XmlDataDef();
		CcInfo ccInfo = new CcInfo();
		
		authenticateData.setRequestSource("FVD");
		ccInfo.setFareMediaNo("0000000000000000");
		
		xmlDataDef.setCcInfo(ccInfo);
		authenticateData.setXmlDataDef(xmlDataDef);
		authenticate.setAuthenticationData(authenticateData);
		authenticateInput.setAuthenticate(authenticate);
		
		AuthenticateOutput authenticateOutput = authProcess.authenticate(authenticateInput);
		try {
			productList = DtoConverter.covertFromAuthenticateOutputToProductList(authenticateOutput);
		} catch (Exception e) {
			
		}
		return productList;
	}
}