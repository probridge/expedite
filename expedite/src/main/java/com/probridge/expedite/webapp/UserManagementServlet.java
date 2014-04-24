package com.probridge.expedite.webapp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.UserRolesMapper;
import com.probridge.expedite.dao.expdb.UsersMapper;
import com.probridge.expedite.model.expdb.UserRolesExample;
import com.probridge.expedite.model.expdb.UserRolesKey;
import com.probridge.expedite.model.expdb.Users;
import com.probridge.expedite.model.expdb.UsersExample;

public class UserManagementServlet extends HttpServlet {

	private static final long serialVersionUID = -5761849563881652897L;
	private static String INSERT_OR_EDIT = "/pages/edit-user.jsp";
	private static String LIST_USER = "/pages/list-user.jsp";
	private static final Logger logger = LoggerFactory.getLogger(UserManagementServlet.class);

	public UserManagementServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		String forward = "";
		//
		HttpSession sess = req.getSession();
		String strGroup = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		//
		if (!Constant.GROUP_ADMIN.equals(strGroup)) {
			resp.sendError(401, "无访问权限");
			return;
		}

		SqlSession sqlSess = null;
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();

			if ("delete".equals(action)) {
				String userName = req.getParameter("userName");
				UsersMapper um = sqlSess.getMapper(UsersMapper.class);
				um.deleteByPrimaryKey(userName);
				// also delete all user role assignment
				UserRolesMapper umapper = sqlSess.getMapper(UserRolesMapper.class);
				UserRolesExample exp2 = new UserRolesExample();
				exp2.createCriteria().andUserNameEqualTo(userName);
				umapper.deleteByExample(exp2);
				//
				sqlSess.commit();
				List<Users> users = um.selectByExample(new UsersExample());
				req.setAttribute("users", users);
				forward = LIST_USER;
			} else if ("edit".equals(action)) {
				String userName = req.getParameter("userName");
				UsersMapper um = sqlSess.getMapper(UsersMapper.class);
				Users user = um.selectByPrimaryKey(userName);
				req.setAttribute("user", user);
				req.setAttribute("GroupAdmin", Constant.GROUP_ADMIN);
				req.setAttribute("GroupEditor", Constant.GROUP_EDITOR);
				req.setAttribute("GroupUser", Constant.GROUP_USER);
				forward = INSERT_OR_EDIT;
			} else if ("new".equals(action)) {
				Users user = new Users();
				user.setUserEnabled("1");
				user.setUserType("0");
				user.setUserGroup("GroupUser");
				req.setAttribute("user", user);
				req.setAttribute("GroupAdmin", Constant.GROUP_ADMIN);
				req.setAttribute("GroupEditor", Constant.GROUP_EDITOR);
				req.setAttribute("GroupUser", Constant.GROUP_USER);
				forward = INSERT_OR_EDIT;
			} else {
				UsersMapper um = sqlSess.getMapper(UsersMapper.class);
				List<Users> users = um.selectByExample(new UsersExample());
				req.setAttribute("users", users);
				forward = LIST_USER;
			}
		} catch (Exception e) {
			logger.error("error processing user management action " + action, e);
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
		RequestDispatcher view = req.getRequestDispatcher(forward);
		view.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession sess = req.getSession();
		String strGroup = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		//
		if (!Constant.GROUP_ADMIN.equals(strGroup)) {
			resp.sendError(401, "无访问权限");
			return;
		}
		//
		req.setCharacterEncoding("utf-8");
		//
		Users user = new Users();
		user.setUserName(req.getParameter("userName"));
		user.setUserPassword(req.getParameter("userPassword"));
		user.setUserDescription(req.getParameter("userDescription"));
		user.setUserEnabled(req.getParameter("userEnabled"));
		user.setUserGroup(req.getParameter("userGroup"));
		user.setUserType(req.getParameter("userType"));
		//
		Date userExpiration = null;
		Date userPwdExpiration = null;
		SqlSession sqlSess = null;
		//
		try {
			try {
				userExpiration = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("userExpiration"));
			} catch (ParseException e) {
				// ignore illegal data format
			}
			try {
				userPwdExpiration = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("userPwdExpiration"));
			} catch (ParseException e) {
				// ignore illegal data format
			}
			user.setUserPwdExpiration(userPwdExpiration);
			user.setUserExpiration(userExpiration);
			logger.info("adding user: " + user.getUserName() + ", " + user.getUserPassword() + ", "
					+ user.getUserDescription() + ", " + user.getUserEnabled() + ", " + user.getUserGroup() + ", "
					+ user.getUserType() + ", " + user.getUserExpiration() + ", " + user.getUserPwdExpiration());
			sqlSess = Constant.sqlSessionFactory.openSession();
			UsersMapper um = sqlSess.getMapper(UsersMapper.class);
			int affected = um.updateByPrimaryKey(user);
			if (affected == 0) {
				um.insert(user);
			}
			//
			if (Constant.GROUP_EDITOR.equals(user.getUserGroup())) {
				// add sandbox editor role
				UserRolesMapper urm = sqlSess.getMapper(UserRolesMapper.class);
				UserRolesKey assignment = new UserRolesKey();
				assignment.setUserName(user.getUserName());
				assignment.setUserRoles(Constant.SANDBOX_DB_NAME + Constant.ROLE_EDITOR_SUFFIX);
				urm.deleteByPrimaryKey(assignment);
				urm.insert(assignment);
			}
			sqlSess.commit();
			//
			resp.sendRedirect("users");
		} catch (Exception e) {
			logger.error("error processing user management post.", e);
			req.setAttribute("error", e.getMessage());
			RequestDispatcher view = req.getRequestDispatcher(LIST_USER);
			view.forward(req, resp);
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
	}
}
