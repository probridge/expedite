package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.UsersMapper;
import com.probridge.expedite.model.expdb.Users;

/**
 * Servlet implementation class DispatchServlet
 */
public class PasswordServlet extends HttpServlet {
	private static final long serialVersionUID = 7982070678512891402L;
	private static final Logger logger = LoggerFactory.getLogger(PasswordServlet.class);
	public static final String TRYAGAIN = "pages/reset_pwd.jsp";
	public static final String SUCCEED = "pages/home.jsp";

	public PasswordServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if ("POST".equals(request.getMethod())) {
			String identity = null;
			HttpSession sess = request.getSession();
			if ("true".equals(sess.getAttribute(Constant.SESSION_AUTH_FLAG)))
				identity = Utility.getStringVal(sess.getAttribute(Constant.SESSION_USER_NAME));
			//
			String p1 = request.getParameter("inputPassword1");
			String p2 = request.getParameter("inputPassword2");
			//
			if (Utility.isEmptyOrNull(p1) || Utility.isEmptyOrNull(p2)) {
				sess.setAttribute("error", "密码不得为空。");
				response.sendRedirect(TRYAGAIN);
				return;
			}
			//
			if (!p1.equals(p2)) {
				sess.setAttribute("error", "两次密码输入不同，请重新输入。");
				response.sendRedirect(TRYAGAIN);
				return;
			}
			//
			SqlSession sqlSess = Constant.sqlSessionFactory.openSession();
			UsersMapper umapper = sqlSess.getMapper(UsersMapper.class);
			Users thisUser = umapper.selectByPrimaryKey(identity);
			//
			if (p1.equals(thisUser.getUserPassword())) {
				sqlSess.close();
				sess.setAttribute("error", "请不要重复使用相同的密码。");
				response.sendRedirect(TRYAGAIN);
				return;
			}
			// set to 3 months later
			thisUser.setUserPassword(p1);
			if (thisUser.getUserPwdExpiration() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.MONTH, 3);
				thisUser.setUserPwdExpiration(cal.getTime());
			}
			umapper.updateByPrimaryKey(thisUser);
			sqlSess.commit();
			sqlSess.close();
			logger.info("pwd changed for user " + identity);
			sess.removeAttribute("error");
			// sqlSess
			response.sendRedirect(SUCCEED);
		}
	}
}
