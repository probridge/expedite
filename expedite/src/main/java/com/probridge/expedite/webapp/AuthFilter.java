package com.probridge.expedite.webapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.ibatis.session.SqlSession;
import org.dom4j.Document;
import org.dom4j.Element;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.RoleInfoMapper;
import com.probridge.expedite.model.expdb.RoleInfo;
import com.probridge.expedite.model.expdb.RoleInfoExample;

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
		//
		String strGroupName = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		servletRequest.setAttribute("group", "user");
		if (Constant.GROUP_EDITOR.equals(strGroupName))
			servletRequest.setAttribute("group", "editor");
		else if (Constant.GROUP_ADMIN.equals(strGroupName))
			servletRequest.setAttribute("group", "admin");
		// Process header
		if (httpRequest.getHeader(userNameHeader) == null) {
			if (sess.getAttribute(Constant.SESSION_USER_NAME) != null)
				httpRequest.addHeader(userNameHeader,
						Utility.getStringVal(sess.getAttribute(Constant.SESSION_USER_NAME)));
			else
				httpRequest.addHeader(userNameHeader, "Anonymous");
		} else
			logger.debug("Got existing header " + userNameHeader + " = " + httpRequest.getHeader(userNameHeader));
		//
		if (httpRequest.getHeader(groupNameHeader) == null) {
			if (sess.getAttribute(Constant.SESSION_GROUP_NAME) != null)
				httpRequest.addHeader(groupNameHeader,
						Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME)));
			else
				httpRequest.addHeader(groupNameHeader, "Anonymous");
		} else
			logger.debug("Got existing header " + groupNameHeader + " = " + httpRequest.getHeader(groupNameHeader));
		//
		if (httpRequest.getHeader(rolesHeader) == null) {
			if (sess.getAttribute(Constant.SESSION_ROLE_LIST) != null)
				httpRequest.addHeader(rolesHeader, Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST)));
			else
				httpRequest.addHeader(rolesHeader, "Anonymous");
		} else
			logger.debug("Got existing header " + rolesHeader + " = " + httpRequest.getHeader(rolesHeader));
		// check new form action
		if (uri.endsWith("/new")) {
			String[] uriArray = uri.split("/");
			String formName = uriArray[uriArray.length - 2];
			String appName = uriArray[uriArray.length - 3];
			// get max allowed data instance
			int maxAllowed = -1;
			SqlSession sqlSess = Constant.sqlSessionFactory.openSession();
			try {
				RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
				//
				RoleInfoExample rExp = new RoleInfoExample();
				rExp.createCriteria().andAppNameEqualTo(appName).andFormNameEqualTo(formName);
				List<RoleInfo> rList = rmapper.selectByExample(rExp);
				//
				if (rList.size() == 0 && (!"orbeon".equals(appName) && !"builder".equals(formName)))
					maxAllowed = 1;
				//
				String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
				HashSet<String> roleList = new HashSet<String>();
				if (strRoleLists != null)
					roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
				//
				for (RoleInfo eachRoleInfo : rList)
					for (String eachRoleHave : roleList)
						if (eachRoleInfo.getRoleName().equals(eachRoleHave))
							if (eachRoleInfo.getDataLimit() != null)
								maxAllowed = Math.max(maxAllowed, eachRoleInfo.getDataLimit());
				//
			} finally {
				if (sqlSess != null)
					sqlSess.close();
			}
			//
			if (maxAllowed > -1) {
				String searchString = "<search xmlns=\"\"><query/><page-size>10000</page-size><page-number>1</page-number><lang /></search>";
				StringEntity requestBody = new StringEntity(searchString);
				//
				HttpClient httpClient = new DefaultHttpClient();
				try {
					HttpPost httpPostRequest = new HttpPost(
							"http://localhost:8080/expedite/fr/service/persistence/search/" + appName + "/" + formName);
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
					if (currentData >= maxAllowed)
						httpResponse.sendError(401, "表单数据超过允许的范围");
					//
				} catch (Exception e) {
					logger.error("error processing export", e);
				} finally {
					httpClient.getConnectionManager().shutdown();
				}
			}
		}
		//
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
			if ((!"/".equals(eachUri) && uri.startsWith(eachUri)) || ("/".equals(eachUri) && uri.equals(eachUri)))
				return true;
		}
		return false;
	}
}
