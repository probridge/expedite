package com.probridge.expedite.webapp.tags;

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.webapp.Constant;

public class ImageUrlTag extends SimpleTagSupport {

	private String relPath = null;
	private String attr = "";

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public void setRelPath(String relPath) {
		this.relPath = relPath;
	}

	private static final Logger logger = LoggerFactory
			.getLogger(ImageUrlTag.class);

	public ImageUrlTag() {
		logger.debug("Resource base url: " + Constant.RESOURCE_BASE_URL);
	}

	@Override
	public void doTag() throws JspException, IOException {
		PageContext context = (PageContext) getJspContext();
		ServletRequest request = context.getRequest();
		String hostUrl = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort() + "/"
				+ Constant.RESOURCE_BASE_URL + "/";
		if (attr != null)
			attr = " " + attr;
		getJspContext().getOut().write(
				"<xhtml:img src=\"" + hostUrl + relPath + "\"" + attr + "/>");
	}
}