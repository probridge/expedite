package com.probridge.expedite.webapp.tags;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.Document;
import org.dom4j.Element;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FormDataIteratorTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory.getLogger(FormDataIteratorTag.class);
	private String appName = null;
	private String formName = null;
	private String dataExists = null;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		String searchString = "<search xmlns=\"\"><query/><page-size>10000</page-size><page-number>1</page-number><lang/></search>";
		StringEntity requestBody = new StringEntity(searchString);
		//
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpPost httpPostRequest = new HttpPost("http://localhost:8080/expedite/fr/service/persistence/search/"
					+ appName + "/" + formName);
			httpPostRequest.setHeader("Cookie", "JSESSIONID=" + sess.getId());
			httpPostRequest.setHeader("Content-Type", "application/xml");
			httpPostRequest.setEntity(requestBody);
			//
			HttpResponse httpPostResp = httpClient.execute(httpPostRequest);
			//
			HttpEntity entity = httpPostResp.getEntity();
			//
			Document document = null;
			if (entity != null) {
				InputStream inputStream = entity.getContent();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				document = Dom4jUtils.readDom4j(bis);
				bis.close();
			}
			//
			int currentData = 0;
			for (Object eachFormData : document.getRootElement().elements())
				if ("N".equalsIgnoreCase(((Element) eachFormData).attributeValue("draft")))
					currentData++;
			//
			if (("true".equalsIgnoreCase(dataExists) && currentData > 0)
					|| ("false".equalsIgnoreCase(dataExists) && currentData == 0))
				getJspBody().invoke(null);
			//
		} catch (Exception e) {
			logger.error("error while getting form data for " + appName + " - " + formName, e);
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

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getDataExists() {
		return dataExists;
	}

	public void setDataExists(String dataExists) {
		this.dataExists = dataExists;
	}
}