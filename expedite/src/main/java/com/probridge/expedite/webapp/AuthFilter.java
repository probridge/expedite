package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jcifs.util.transport.Request;

import org.orbeon.oxf.webapp.ProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.actors.threadpool.Arrays;

public class AuthFilter implements Filter {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthFilter.class);
	private String userNameHeader;
	private String groupNameHeader;
	private String rolesHeader;
	private String[] unProtectedResources = null;

	@Override
	public void destroy() {
		logger.info("AuthFilter destoryed");
	}

	@Override
	public void doFilter(ServletRequest servletRequest,
			ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final ServletRequestWrapper httpRequest = new ServletRequestWrapper(
				(HttpServletRequest) servletRequest);
		final HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
		//
		String uri = httpRequest.getServletPath().toLowerCase();
		logger.debug("filtering " + uri);
		//
		if (!isUnprotected(uri)) {
			// protect
			logger.debug("protected resource: " + uri);

			HttpSession sess = httpRequest.getSession();
			if ("true".equals(sess.getAttribute(Constant.SESSION_AUTH_FLAG))) {
				httpRequest.addHeader(userNameHeader, Utility.getStringVal(sess
						.getAttribute(Constant.SESSION_USER_NAME)));
				httpRequest.addHeader(groupNameHeader, Utility
						.getStringVal(sess
								.getAttribute(Constant.SESSION_GROUP_NAME)));
				httpRequest.addHeader(rolesHeader, Utility.getStringVal(sess
						.getAttribute(Constant.SESSION_ROLE_LIST)));
				filterChain.doFilter(httpRequest, httpResponse);
			} else {
				httpRequest.addHeader(userNameHeader, Constant.ANONYMOUS);
				httpRequest.addHeader(groupNameHeader, Constant.ANONYMOUS);
				httpRequest.addHeader(rolesHeader, Constant.ANONYMOUS);
				// Stop here
				httpResponse.sendRedirect("login.jsp");
			}
		} else {
			// pass through
			logger.debug("unprotected resource: " + uri);
			filterChain.doFilter(httpRequest, httpResponse);
		}
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.info("AuthFilter started");
		this.userNameHeader = config
				.getInitParameter(Constant.AUTH_FILTER_USER_NAME);
		this.groupNameHeader = config
				.getInitParameter(Constant.AUTH_FILTER_GROUP_NAME);
		this.rolesHeader = config.getInitParameter(Constant.AUTH_FILTER_ROLES);
		unProtectedResources = config.getInitParameter(
				Constant.AUTH_FILTER_UNPROTECTED).split(",");
		if (userNameHeader == null || groupNameHeader == null
				|| rolesHeader == null || unProtectedResources == null)
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
