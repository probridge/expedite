package com.probridge.expedite.webapp.tags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.webapp.Constant;
import com.probridge.expedite.webapp.Utility;

public class FormIteratorTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory.getLogger(FormIteratorTag.class);

	@Override
	public void doTag() throws JspException, IOException {
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		logger.debug("user roles = " + strRoleLists);

		HashSet<String> roleList = new HashSet<String>();
		if (strRoleLists != null)
			roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
		//
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpGet httpGetRequest1 = new HttpGet("http://localhost:8080/expedite/fr/service/persistence/form");
			httpGetRequest1.setHeader("Cookie", "JSESSIONID=" + sess.getId());
			HttpResponse httpResponse1 = httpClient.execute(httpGetRequest1);
			HttpEntity entity1 = httpResponse1.getEntity();
			Document formListDocument = null;
			if (entity1 != null) {
				InputStream ins = entity1.getContent();
				try {
					formListDocument = Dom4jUtils.readDom4j(ins);
				} finally {
					ins.close();
				}
			}
			List forms = formListDocument.selectNodes("//form");
			for (Object eachForm : forms) {
				if (eachForm instanceof Element) {
					String appName = ((Element) eachForm).elementText("application-name");
					String formName = ((Element) eachForm).elementText("form-name");
					String formTitle = ((Element) eachForm).elementText("title");
					String formDescription = ((Element) eachForm).elementText("description");
					if (roleList.contains(appName + Constant.ROLE_EDITOR_SUFFIX)) {
						context.setAttribute("tagAppName", appName);
						context.setAttribute("tagFormName", formName);
						context.setAttribute("tagFormTitle", Utility.isEmptyOrNull(formTitle) ? "开始" : formTitle);
						context.setAttribute("tagDescription", formDescription);
						getJspBody().invoke(null);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error processing export", e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}
}