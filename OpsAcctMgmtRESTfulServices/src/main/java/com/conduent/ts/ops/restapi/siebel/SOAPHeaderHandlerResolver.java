package com.conduent.ts.ops.restapi.siebel;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

public class SOAPHeaderHandlerResolver implements HandlerResolver {

	@Override
	public List<Handler> getHandlerChain(PortInfo arg0) {
		List<Handler> handlerChain = new ArrayList<Handler>();
		SOAPSecurityHeaderHandler securityHeaderHandler = new SOAPSecurityHeaderHandler();
		handlerChain.add(securityHeaderHandler);
		
		return handlerChain;
	}
}
