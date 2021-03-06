package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.RoleInfoMapper;
import com.probridge.expedite.dao.expdb.UserRolesMapper;
import com.probridge.expedite.dao.expdb.UsersMapper;
import com.probridge.expedite.model.expdb.RoleInfo;
import com.probridge.expedite.model.expdb.UserRoles;
import com.probridge.expedite.model.expdb.UserRolesExample;
import com.probridge.expedite.model.expdb.Users;

/**
 * Servlet implementation class DispatchServlet
 */
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 7982070678512891402L;
	private static final String TRYAGAIN = "pages/login.jsp?other=1";
	private static final String SUCCEED = "pages/home.jsp";
	private static final String RESETPWD = "pages/reset_pwd.jsp";
	private static final Logger logger = LoggerFactory.getLogger(LoginServlet.class);

	public LoginServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean errorOccurred = false;
		logger.debug("login processing");
		HttpSession sess = request.getSession();
		String userName = null;
		String password = null;
		if ("true".equals(request.getParameter("loginToken"))) {
			userName = Utility.getStringVal(sess.getAttribute(Constant.SESSION_USER_NAME));
			password = Utility.getStringVal(sess.getAttribute(Constant.SESSION_AUTH_TOKEN));
			logger.debug("got token: " + userName);
		} else {
			userName = request.getParameter("inputEmail");
			password = request.getParameter("inputPassword");
			logger.debug("no token: " + userName);
		}
		//
		if (userName == null || password == null) {
			logger.debug("login internal error, null value received.");
			sess.removeAttribute(Constant.SESSION_AUTH_TOKEN);
			sess.removeAttribute(Constant.SESSION_AUTH_FLAG);
			sess.setAttribute("loginError", "登录发生错误，请联系我们。");
			response.sendRedirect(TRYAGAIN);
			errorOccurred = true;
		}
		// save email if login failed.
		request.setAttribute("email", userName);
		UsersMapper umapper = null;
		Users loginUser = null;
		SqlSession sqlSess = Constant.sqlSessionFactory.openSession();
		if (!errorOccurred) {
			umapper = sqlSess.getMapper(UsersMapper.class);
			loginUser = umapper.selectByPrimaryKey(userName);
			if (loginUser == null || !password.equals(loginUser.getUserPassword())) {
				logger.debug("Login failed: " + userName);
				sess.removeAttribute(Constant.SESSION_AUTH_TOKEN);
				sess.removeAttribute(Constant.SESSION_AUTH_FLAG);
				sess.setAttribute("loginError", "登录失败");
				response.sendRedirect(TRYAGAIN);
				errorOccurred = true;
			}
		}
		//
		if (!errorOccurred && !"1".equals(loginUser.getUserEnabled())) {
			logger.debug("User disabled: " + userName);
			sess.removeAttribute(Constant.SESSION_AUTH_TOKEN);
			sess.removeAttribute(Constant.SESSION_AUTH_FLAG);
			sess.setAttribute("loginError", "您的帐户暂时不可用");
			response.sendRedirect(TRYAGAIN);
			errorOccurred = true;
		}
		//
		if (!errorOccurred
				&& (loginUser.getUserExpiration() != null && loginUser.getUserExpiration().before(new Date()))) {
			logger.debug("User expired: " + userName);
			sess.removeAttribute(Constant.SESSION_AUTH_TOKEN);
			sess.removeAttribute(Constant.SESSION_AUTH_FLAG);
			sess.setAttribute("loginError", "您的帐户已经过期");
			response.sendRedirect(TRYAGAIN);
			errorOccurred = true;
		}
		//
		boolean pwdExpired = false;
		if (!errorOccurred
				&& (loginUser.getUserPwdExpiration() != null && loginUser.getUserPwdExpiration().before(new Date()))
				&& "0".equals(loginUser.getUserType())) {
			// user pwd expired - force changing password
			logger.debug("User password expired: " + userName);
			pwdExpired = true;
		}
		//
		if (!errorOccurred) {
			logger.debug("Login successful: " + userName);
			UserRolesMapper rMapper = sqlSess.getMapper(UserRolesMapper.class);
			UserRolesExample exp = new UserRolesExample();
			exp.createCriteria().andUserNameEqualTo(loginUser.getUserName()).andUserRoleExpirationIsNull();
			exp.or().andUserNameEqualTo(loginUser.getUserName()).andUserRoleExpirationGreaterThan(new Date());
			List<UserRoles> roleList = rMapper.selectByExample(exp);
			StringBuilder sb = new StringBuilder();
			//
			RoleInfoMapper riMapper = sqlSess.getMapper(RoleInfoMapper.class);
			if (roleList != null)
				for (UserRoles role : roleList) {
					// check role expiration
					RoleInfo thisRole = riMapper.selectByPrimaryKey(role.getUserRoles());
					if (thisRole != null)
						if (thisRole.getRoleExpiration() == null || thisRole.getRoleExpiration().after(new Date()))
							sb.append(role.getUserRoles()).append(",");
				}
			if (sb.length() > 0)
				sb.deleteCharAt(sb.length() - 1);
			//
			sess.setAttribute(Constant.SESSION_AUTH_FLAG, "true");
			sess.setAttribute(Constant.SESSION_USER_NAME, loginUser.getUserName());
			sess.setAttribute(Constant.SESSION_GROUP_NAME, loginUser.getUserGroup());
			sess.setAttribute(Constant.SESSION_ROLE_LIST, sb.toString());
			//
			if (pwdExpired)
				response.sendRedirect(RESETPWD);
			else
				response.sendRedirect(SUCCEED);
		}
		sqlSess.close();
		//
	}
}
