package com.probridge.expedite.webapp.tags;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.webapp.Constant;

public class AuthenticatedTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthenticatedTag.class);

	@Override
	public void doTag() throws JspException, IOException {
		logger.debug("checking if user is authenticated");
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		if ("true".equals(sess.getAttribute(Constant.SESSION_AUTH_FLAG))) {
			logger.debug("user is authenticated");
			getJspBody().invoke(null);
		} else {
			logger.debug("user is NOT authenticated");
		}
	}
}