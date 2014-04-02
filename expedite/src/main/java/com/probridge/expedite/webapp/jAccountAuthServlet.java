package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.Hashtable;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.UsersMapper;
import com.probridge.expedite.model.expdb.Users;

import edu.sjtu.jaccount.JAccountManager;

/**
 * Servlet implementation class DispatchServlet
 */
public class jAccountAuthServlet extends HttpServlet {
	private static final long serialVersionUID = 7982070678512891402L;
	private static final Logger logger = LoggerFactory
			.getLogger(jAccountAuthServlet.class);

	public jAccountAuthServlet() {
		super();
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		logger.debug("trigger external auth processing");
		JAccountManager jam = new JAccountManager(Constant.jAccountSiteId,
				Constant.configPath);
		logger.debug("checking jaccount, return url = "
				+ request.getRequestURI());
		@SuppressWarnings("rawtypes")
		Hashtable ht = jam.checkLogin(request, response, request.getSession(),
				request.getRequestURI());
		if (ht != null && ht.get("uid") != null) {
			HttpSession sess = request.getSession();

			String uid = ht.get("uid").toString().toLowerCase()
					+ Constant.jAccountSuffix;
			String userDesc = ht.get("dept") + " " + ht.get("chinesename");

			logger.debug("login jaccount uid=" + uid);
			logger.debug("login jaccount userDesc=" + userDesc);

			String token = new Sha512Hash(uid, "expedite_salt").toHex()
					.substring(0, 20);

			SqlSession sqlSess = Constant.sqlSessionFactory.openSession();
			UsersMapper umapper = sqlSess.getMapper(UsersMapper.class);
			Users loginUser = umapper.selectByPrimaryKey(uid);
			//
			if (loginUser == null) {
				logger.info("new jAccount user, creating entries in local db: "
						+ uid);
				loginUser = new Users();
				loginUser.setUserName(uid);
				loginUser.setUserPassword(token);
				loginUser.setUserEnabled("1");
				loginUser.setUserType("1");
				loginUser.setUserGroup(Constant.GROUP_USER);
				loginUser.setUserDescription(userDesc);
				umapper.insert(loginUser);
				sqlSess.commit();
			}
			sqlSess.close();
			//
			logger.debug("setting token and continue the login.");
			sess.setAttribute(Constant.SESSION_USER_NAME, uid);
			sess.setAttribute(Constant.SESSION_AUTH_TOKEN, token);
			response.sendRedirect("pages/login.jsp");
		}
	}
}
