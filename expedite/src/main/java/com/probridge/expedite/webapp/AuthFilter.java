package com.probridge.expedite.webapp;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
	private String userNameHeader;
	private String groupNameHeader;
	private String rolesHeader;
	private String[] unProtectedResources = null;

	@Override
	public void destroy() {
		logger.info("AuthFilter destoryed");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final ServletRequestWrapper httpRequest = new ServletRequestWrapper((HttpServletRequest) servletRequest);
		final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		//
		String uri = httpRequest.getServletPath().toLowerCase();
		logger.debug("filtering " + uri);
		//
		HttpSession sess = httpRequest.getSession();
		if (!isUnprotected(uri)) {
			// protect
			logger.debug("protected resource: " + uri);
			if (!"true".equals(sess.getAttribute(Constant.SESSION_AUTH_FLAG))) {
				httpResponse.sendRedirect("login.jsp");
				return;
			}
		}
		// Process header
		if (httpRequest.getHeader(userNameHeader) == null) {
			if (sess.getAttribute(Constant.SESSION_USER_NAME) != null)
				httpRequest.addHeader(userNameHeader,
						Utility.getStringVal(sess.getAttribute(Constant.SESSION_USER_NAME)));
			else
				httpRequest.addHeader(userNameHeader, "Anonymous");
		}

		if (httpRequest.getHeader(groupNameHeader) == null) {
			if (sess.getAttribute(Constant.SESSION_GROUP_NAME) != null)
				httpRequest.addHeader(groupNameHeader,
						Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME)));
			else
				httpRequest.addHeader(groupNameHeader, "Anonymous");
		}
		//
		if (httpRequest.getHeader(rolesHeader) == null) {
			if (sess.getAttribute(Constant.SESSION_ROLE_LIST) != null)
				httpRequest.addHeader(rolesHeader, Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST)));
			else
				httpRequest.addHeader(rolesHeader, "Anonymous");
		}//
		filterChain.doFilter(httpRequest, httpResponse);

	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("AuthFilter started");
		this.userNameHeader = config.getInitParameter(Constant.AUTH_FILTER_USER_NAME);
		this.groupNameHeader = config.getInitParameter(Constant.AUTH_FILTER_GROUP_NAME);
		this.rolesHeader = config.getInitParameter(Constant.AUTH_FILTER_ROLES);
		unProtectedResources = config.getInitParameter(Constant.AUTH_FILTER_UNPROTECTED).split("\\s*,\\s*");
		if (userNameHeader == null || groupNameHeader == null || rolesHeader == null || unProtectedResources == null)
			throw new ServletException("AuthFilter missing required parameter.");
	}

	private boolean isUnprotected(String uri) {
		for (String eachUri : unProtectedResources) {
			if (uri.startsWith(eachUri))
				return true;
		}
		return false;
	}
}
