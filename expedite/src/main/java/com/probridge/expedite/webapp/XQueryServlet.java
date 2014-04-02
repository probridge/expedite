package com.probridge.expedite.webapp;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XQueryServlet extends HttpServlet {

	private static final long serialVersionUID = -5761849563881652897L;
	private static String XQUERY_VIEW = "/pages/xquery.jsp";
//	private static final Logger logger = LoggerFactory.getLogger(XQueryServlet.class);

	public XQueryServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("appName", Constant.USERINFO_APP);
		req.setAttribute("formName", Constant.USERINFO_FORM);
		req.setAttribute("emailControl", Constant.USERINFO_EMAIL_CONTROL);
		req.setAttribute("emailPath", Constant.USERINFO_EMAIL_PATH);
		RequestDispatcher view = req.getRequestDispatcher(XQUERY_VIEW);
		view.forward(req, resp);
	}
}
