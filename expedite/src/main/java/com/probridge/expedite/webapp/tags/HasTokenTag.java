package com.probridge.expedite.webapp.tags;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.webapp.Constant;
import com.probridge.expedite.webapp.Utility;

public class HasTokenTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory
			.getLogger(HasTokenTag.class);

	@Override
	public void doTag() throws JspException, IOException {
		logger.debug("checking if user has external auth token");
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		if (!Utility.isEmptyOrNull(Utility.getStringVal(sess
				.getAttribute(Constant.SESSION_AUTH_TOKEN)))) {
			logger.debug("user has external token.");
			getJspBody().invoke(null);
		} else {
			logger.debug("user does not have token.");
		}
	}
}