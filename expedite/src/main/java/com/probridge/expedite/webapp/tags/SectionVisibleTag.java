package com.probridge.expedite.webapp.tags;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.webapp.Constant;
import com.probridge.expedite.webapp.Utility;

public class SectionVisibleTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory.getLogger(SectionVisibleTag.class);
	private String requiredRole = null;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		HashSet<String> roleList = new HashSet<String>();
		if (strRoleLists != null)
			roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
		logger.debug("role list=" + roleList.toString());
		//
		if ("*".equals(requiredRole) || roleList.contains(requiredRole)) {
			getJspBody().invoke(null);
		}
	}

	public String getRequiredRole() {
		return requiredRole;
	}

	public void setRequiredRole(String requiredRole) {
		this.requiredRole = requiredRole;
	}
}