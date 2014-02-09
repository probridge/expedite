package com.probridge.expedite.webapp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.jaccount.JAccountManager;

/**
 * Servlet implementation class DispatchServlet
 */
public class LogoutServlet extends HttpServlet {
	private static final long serialVersionUID = 7982070678512891402L;
	private static final Logger logger = LoggerFactory
			.getLogger(LogoutServlet.class);

	public LogoutServlet() {
		super();
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		logger.debug("trigger logout processing");
		request.getSession().invalidate();
		JAccountManager jam = new JAccountManager(Constant.jAccountSiteId,
				Constant.configPath);
		String returnUrl = request.getRequestURI().substring(0,
				request.getRequestURI().lastIndexOf("/") + 1) + "pages/";
		logger.debug("logging out with returnUrl=" + returnUrl);
		jam.logout(request, response, returnUrl);
	}
}
