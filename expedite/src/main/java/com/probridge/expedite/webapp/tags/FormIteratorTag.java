package com.probridge.expedite.webapp.tags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

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
import org.orbeon.oxf.util.SecureUtils;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.webapp.Constant;
import com.probridge.expedite.webapp.ExpediteContextListener;
import com.probridge.expedite.webapp.Utility;

public class FormIteratorTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory.getLogger(FormIteratorTag.class);
	private String appName = null;
	private String mode = null;
	private String requiredPermission = null;

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
			String uri = "http://localhost:8080/expedite/fr/service/persistence/form";
			//
			if (appName != null)
				uri += "/" + appName;
			HttpGet httpGetRequest1 = new HttpGet(uri);
			httpGetRequest1.setHeader("Cookie", "JSESSIONID=" + sess.getId());
			httpGetRequest1.setHeader("Orbeon-Client", "servlet");
			//
			String token = SecureUtils.randomHexId();
			ExpediteContextListener.getContext().setAttribute("orbeon-token", token);
			httpGetRequest1.setHeader("orbeon-token", token);
			//
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
					String available = ((Element) eachForm).elementText("available");
					Element permissions = ((Element) eachForm).element("permissions");
					List permission = null;
					if (permissions != null)
						permission = permissions.selectNodes("permission");
					//
					if ("editor".equals(mode) && roleList.contains(appName + Constant.ROLE_EDITOR_SUFFIX)
							&& Utility.isEmptyOrNull(available)) {
						context.setAttribute("tagAppName", appName);
						context.setAttribute("tagFormName", formName);
						context.setAttribute("tagFormTitle", Utility.isEmptyOrNull(formTitle) ? "未命名" : formTitle);
						context.setAttribute("tagDescription", formDescription);
						getJspBody().invoke(null);
					}
					if (!"editor".equals(mode) && Utility.isEmptyOrNull(available)) {
						boolean skip = false;
						//
						if (!Utility.isEmptyOrNull(requiredPermission) && permission != null) {
							for (Object eachPermission : permission) {
								if (eachPermission instanceof Element) {
									String ops = ((Element) eachPermission).attributeValue("operations");
									if (ops != null && ops.indexOf(requiredPermission) >= 0) {
										// find required permission definition
										List sub = ((Element) eachPermission).selectNodes("user-role");
										if (sub != null && sub.size() > 0) {
											skip = true;
											String allowedRoleList = ((Element) sub.get(0)).attributeValue("any-of");
											if (allowedRoleList != null) {
												StringTokenizer st = new StringTokenizer(allowedRoleList);
												while (st.hasMoreTokens()) {
													boolean found = roleList.contains(st.nextToken());
													if (found) {
														skip = false;
														break;
													}
												}
											}
										}
									}
								}
							}
						}
						//
						if (skip)
							continue;
						context.setAttribute("tagAppName", appName);
						context.setAttribute("tagFormName", formName);
						context.setAttribute("tagFormTitle", Utility.isEmptyOrNull(formTitle) ? "未命名" : formTitle);
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

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getRequiredPermission() {
		return requiredPermission;
	}

	public void setRequiredPermission(String requiredPermission) {
		this.requiredPermission = requiredPermission;
	}
}