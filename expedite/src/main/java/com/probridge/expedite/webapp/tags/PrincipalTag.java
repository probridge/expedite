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

public class PrincipalTag extends SimpleTagSupport {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory
			.getLogger(PrincipalTag.class);

	public PrincipalTag() {
	}

	@Override
	public void doTag() throws JspException, IOException {
		
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		String principal = "Anonymous";
		if ("true".equals(sess.getAttribute(Constant.SESSION_AUTH_FLAG)))
			principal = Utility.getStringVal(sess
					.getAttribute(Constant.SESSION_USER_NAME));
		//
		getJspContext().getOut().write(principal);
	}
}