package com.conduent.ts.ops.restapi.siebel;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.log4j.Logger;

import com.conduent.ts.ops.restapi.config.ConfigCache;
import com.conduent.ts.ops.restapi.config.Constants;

public class SOAPSecurityHeaderHandler implements SOAPHandler<SOAPMessageContext> {

	private static Logger logger = Logger.getLogger(SOAPSecurityHeaderHandler.class.getName());
	
	@Override
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean outBoundRequest = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		if (outBoundRequest.booleanValue()) {
			try {
				SOAPMessage message = context.getMessage();
				SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
				SOAPHeader header = envelope.getHeader();
				
				if (header == null) {
					header = envelope.addHeader();
				}
				
				SOAPHeaderElement sessionType = header.addHeaderElement(envelope.createName("SessionType", "sh", "http://siebel.com/webservices"));
				sessionType.addTextNode("Stateless");
				
				if (ConfigCache.isSiebelTokenAvailable()) {
					SOAPHeaderElement sessionToken = header.addHeaderElement(envelope.createName("SessionToken", "sh", "http://siebel.com/webservices"));
					sessionToken.addTextNode(ConfigCache.getSiebelToken());
					
				} else {
		            SOAPHeaderElement usernameToken = header.addHeaderElement(envelope.createName("UsernameToken", "sh", "http://siebel.com/webservices"));
		            usernameToken.addTextNode(ConfigCache.getProcessParameter(Constants.PARAM_NAME_SIEBEL_WS_USERNAME).getParamValue());
					
					SOAPHeaderElement passwordText = header.addHeaderElement(envelope.createName("PasswordText", "sh", "http://siebel.com/webservices"));
					passwordText.addTextNode(ConfigCache.getProcessParameter(Constants.PARAM_NAME_SIEBEL_WS_PASSWORD).getParamValue());
				}
				
				if (logger.isDebugEnabled()) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					message.writeTo(bout);
					String logMessage = bout.toString();
					logger.debug("Outbound SOAP Message: " + logMessage);
				}
				
			} catch (Exception ex) {
				logger.error("Error when adding SOAP security header: " + ex.getMessage());
			}
		} else {
			try {
				SOAPMessage message = context.getMessage();
				SOAPEnvelope envelope = message.getSOAPPart().getEnvelope();
				SOAPHeader header = envelope.getHeader();
				
				if (header != null) {
					Iterator<?> iterator = header.getChildElements();
					while (iterator.hasNext()) {
						SOAPHeaderElement headerElement = (SOAPHeaderElement) iterator.next();
						if (headerElement.getLocalName().equalsIgnoreCase("SessionToken")) {
							String token = headerElement.getValue();
							ConfigCache.setSiebelToken(token);
						}
					}
				} else {
					logger.warn("SOAP security header is null. ");
				}
				
				if (logger.isDebugEnabled()) {
					ByteArrayOutputStream bout = new ByteArrayOutputStream(); 
					message.writeTo(bout);
					String logMessage = bout.toString();
					logger.debug("Inbound SOAP Message: " + logMessage);
				}
			} catch (Exception ex) {
				logger.error("Error when retrieving SOAP security header: " + ex.getMessage());
			}
			
		}
		
		return true;
	}
	
	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}
	
	@Override
	public void close(MessageContext context) {
	}

}
