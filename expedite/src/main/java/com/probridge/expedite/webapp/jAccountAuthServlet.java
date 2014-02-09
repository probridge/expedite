package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.jaccount.JAccountManager;

/**
 * Servlet implementation class DispatchServlet
 */
public class jAccountAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 7982070678512891402L;
	private static final Logger logger = LoggerFactory.getLogger(jAccountAuthServlet.class);

	public jAccountAuthServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		logger.debug("trigger external auth processing");
		JAccountManager jam = new JAccountManager(Constant.jAccountSiteId, Constant.configPath);
		@SuppressWarnings("rawtypes")
		Hashtable ht = jam.checkLogin(request, response, request.getSession(), request.getRequestURI());
		if (ht != null && ht.get("uid") != null) {
			HttpSession sess = request.getSession();

			String uid = ht.get("uid").toString().toLowerCase() + Constant.jAccountSuffix;
			String userDesc = ht.get("dept") + " " + ht.get("chinesename");
			
			logger.debug("jaccount uid=" + uid );
			logger.debug("jaccount userDesc=" + userDesc );
			
			sess.setAttribute(Constant.SESSION_AUTH_FLAG, "true");
			sess.setAttribute(Constant.SESSION_USER_NAME, "abc@sjtu.edu.cn");
			sess.setAttribute(Constant.SESSION_GROUP_NAME, "students");
			sess.setAttribute(Constant.SESSION_ROLE_LIST, "user,admin");			
			/*
			// check user db - insert user
			//
			SqlSession session = VBoxConfig.sqlSessionFactory.openSession();
			UsersMapper mapper = session.getMapper(UsersMapper.class);
			Users thisUser = mapper.selectByPrimaryKey(uid);
			String encodedPwd = new Sha512Hash(uid, "jaccount_salt").toHex().substring(0, 20);
			if (thisUser == null) {
				thisUser = new Users();
				thisUser.setUserName(uid);
				thisUser.setUserEnabled("1");
				thisUser.setUserDescription(userDesc);
				thisUser.setUserExpiration(null);
				thisUser.setUserPassword(encodedPwd);
				thisUser.setUserPwdExpire(null);
				thisUser.setUserRole("ROLE_USER");
				thisUser.setUserType("1");
				thisUser.setUserVhdName(null);
				thisUser.setUserVhdQuota(null);
				thisUser.setUserHypervisorId(null);
				mapper.insert(thisUser);
				session.commit();
			}
			//
			UsernamePasswordToken loginToken = new UsernamePasswordToken(uid, encodedPwd);
			loginToken.setRememberMe(true);
			Subject currentUser = SecurityUtils.getSubject();
			try {
				currentUser.login(loginToken);
			} catch (Exception e) {
				request.setAttribute("error", "登录发生错误，请联系我们。" + e.getMessage());
				return ERROR;
			}
			// perform login
			return SUCCEED;
			*/
		} else {
			jam.logout(request, response, request.getRequestURI());
			return;
		}
	}
}
