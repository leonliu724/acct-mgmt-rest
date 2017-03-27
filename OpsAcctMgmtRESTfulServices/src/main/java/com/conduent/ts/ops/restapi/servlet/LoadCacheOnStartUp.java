package com.conduent.ts.ops.restapi.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.conduent.ts.ops.restapi.config.ConfigCache;

/**
 * Servlet implementation class LoadCacheOnStartUp
 */
public class LoadCacheOnStartUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoadCacheOnStartUp() {
        super();
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		ConfigCache.loadConfigCache();
	}
}
