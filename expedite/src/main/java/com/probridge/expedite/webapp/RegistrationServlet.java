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
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 7982070678512891402L;
	private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
	public static final String TRYAGAIN = "pages/register.jsp";
	public static final String SUCCEED = "pages/login.jsp?other=1";

	public RegistrationServlet() {
		super();
	}

	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if ("POST".equals(request.getMethod())) {
			HttpSession sess = request.getSession();
			String email = request.getParameter("inputEmail");
			//
			String p1 = request.getParameter("inputPassword1");
			String p2 = request.getParameter("inputPassword2");
			//
			if (Utility.isEmptyOrNull(p1) || Utility.isEmptyOrNull(p2)) {
				sess.setAttribute("error", "密码不得为空。");
				response.sendRedirect(TRYAGAIN);
				return;
			}

			if (email.toLowerCase().endsWith(Constant.jAccountSuffix)) {
				sess.setAttribute("error", "校内用户请使用统一认证平台直接登录。");
				response.sendRedirect(TRYAGAIN);
				return;
			}

			if (!p1.equals(p2)) {
				sess.setAttribute("error", "两次密码输入不同，请重新输入。");
				response.sendRedirect(TRYAGAIN);
				return;
			}
			//
			SqlSession sqlSess = Constant.sqlSessionFactory.openSession();
			UsersMapper umapper = sqlSess.getMapper(UsersMapper.class);
			Users thisUser = umapper.selectByPrimaryKey(email);
			//
			if (thisUser != null) {
				sqlSess.close();
				sess.setAttribute("error", "用户名已经存在。");
				response.sendRedirect(TRYAGAIN);
				return;
			}
			thisUser = new Users();
			thisUser.setUserName(email);
			thisUser.setUserPassword(p1);
			thisUser.setUserEnabled("1");
			thisUser.setUserType("0");
			thisUser.setUserGroup(Constant.GROUP_USER);

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.MONTH, 3);
			thisUser.setUserPwdExpiration(cal.getTime());
			//
			umapper.insert(thisUser);
			sqlSess.commit();
			sqlSess.close();
			//
			sess.removeAttribute("error");
			sess.setAttribute("loginInfo", "用户注册成功，请登录。");
			logger.info(email + " user registered.");
			response.sendRedirect(SUCCEED);
			return;
		}
	}
}
