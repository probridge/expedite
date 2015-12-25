package com.probridge.expedite.webapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.orbeon.oxf.xml.dom4j.Dom4jUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.probridge.expedite.dao.expdb.RoleInfoMapper;
import com.probridge.expedite.dao.expdb.UserRolesMapper;
import com.probridge.expedite.dao.expdb.UsersMapper;
import com.probridge.expedite.model.expdb.RoleInfo;
import com.probridge.expedite.model.expdb.RoleInfoExample;
import com.probridge.expedite.model.expdb.UserRoles;
import com.probridge.expedite.model.expdb.UserRolesExample;
import com.probridge.expedite.model.expdb.UserRolesKey;
import com.probridge.expedite.model.expdb.Users;
import com.probridge.expedite.model.expdb.UsersExample;

public class UserRoleManagementServlet extends HttpServlet {

	private static final long serialVersionUID = -5761849563881652897L;
	private static String LIST_ASSIGNMENT = "/pages/list-assignment.jsp";
	private static final Logger logger = LoggerFactory.getLogger(UserRoleManagementServlet.class);

	public UserRoleManagementServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		String forward = "";
		//
		HttpSession sess = req.getSession();
		String strGroup = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		//
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		HashSet<String> roleList = new HashSet<String>();
		if (strRoleLists != null)
			roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
		//
		if (!(Constant.GROUP_EDITOR.equals(strGroup) || Constant.GROUP_ADMIN.equals(strGroup))) {
			resp.sendError(401, "无访问权限");
			return;
		}
		// editor can only list/operate role that he has access to, not allowed
		// to list user
		SqlSession sqlSess = null;
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();
			if ("remove".equals(action)) {
				String userName = req.getParameter("userName");
				String roleName = req.getParameter("roleName");
				if (!Utility.gotPermissionForRole(strGroup, roleList, roleName)) {
					resp.sendError(401, "无访问权限");
					return;
				}
				if ((Constant.SANDBOX_DB_NAME + Constant.ROLE_EDITOR_SUFFIX).equals(roleName)) {
					resp.sendError(500, "不允许删除系统角色");
					return;
				}
				//
				UserRolesMapper um = sqlSess.getMapper(UserRolesMapper.class);
				UserRolesKey toDelete = new UserRolesKey();
				toDelete.setUserName(userName);
				toDelete.setUserRoles(roleName);
				um.deleteByPrimaryKey(toDelete);
				sqlSess.commit();
			}
			if ("removeall".equals(action)) {
				String roleName = req.getParameter("roleName");
				if (!Utility.gotPermissionForRole(strGroup, roleList, roleName)) {
					resp.sendError(401, "无访问权限");
					return;
				}
				if ((Constant.SANDBOX_DB_NAME + Constant.ROLE_EDITOR_SUFFIX).equals(roleName)) {
					resp.sendError(500, "不允许删除系统角色");
					return;
				}
				//
				UserRolesMapper um = sqlSess.getMapper(UserRolesMapper.class);
				UserRolesExample exp = new UserRolesExample();
				exp.createCriteria().andUserRolesEqualTo(roleName);
				um.deleteByExample(exp);
				sqlSess.commit();
			}
			String listUser = req.getParameter("listUser");
			String listRole = req.getParameter("listRole");
			//
			UserRolesMapper urm = sqlSess.getMapper(UserRolesMapper.class);
			UserRolesExample exp = new UserRolesExample();
			if (listUser != null && listUser.length() > 0) {
				// only allow admin to list user's roles
				if (!Constant.GROUP_ADMIN.equals(strGroup)) {
					resp.sendError(401, "无访问权限");
					return;
				}
				exp.createCriteria().andUserNameEqualTo(listUser);
				req.setAttribute("listBy", "user");
			} else if (listRole != null && listRole.length() > 0) {
				// editors (and admins) can list users in the role that has
				// access
				if (!Utility.gotPermissionForRole(strGroup, roleList, listRole)) {
					resp.sendError(401, "无访问权限");
					return;
				}
				exp.createCriteria().andUserRolesEqualTo(listRole);
				req.setAttribute("listBy", "role");
			} else {
				throw new Exception("invalid request");
			}
			List<UserRoles> assignment = urm.selectByExample(exp);
			req.setAttribute("assignments", assignment);
			req.setAttribute("sandboxEditor", Constant.SANDBOX_DB_NAME + Constant.ROLE_EDITOR_SUFFIX);
			forward = LIST_ASSIGNMENT;
		} catch (Exception e) {
			logger.error("error processing assignment management action " + action, e);
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
		populate(req);
		RequestDispatcher view = req.getRequestDispatcher(forward);
		view.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		//
		HttpSession sess = req.getSession();
		String strGroup = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		//
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		HashSet<String> roleList = new HashSet<String>();
		if (strRoleLists != null)
			roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
		// filter out normal user group
		if (!(Constant.GROUP_EDITOR.equals(strGroup) || Constant.GROUP_ADMIN.equals(strGroup))) {
			resp.sendError(401, "无访问权限");
			return;
		}
		//
		String[] userNames = req.getParameterValues("userName");
		String[] roleNames = req.getParameterValues("roleName");
		//
		Date expiration = null;
		try {
			expiration = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("expiration"));
		} catch (Exception e) {
			expiration = null;
		}

		boolean xform = false;
		//
		if (userNames == null && roleNames == null) {
			BufferedInputStream bis = new BufferedInputStream(req.getInputStream());
			Document document = null;
			try {
				document = Dom4jUtils.readDom4j(bis);
				userNames = document.selectSingleNode("//selectedUser").getText().split("\\s*[\\s,]\\s*");
				roleNames = new String[] { document.selectSingleNode("//selectedRole").getText() };
				if ((userNames == null || userNames[0].length() == 0)
						|| (roleNames == null || roleNames[0].length() == 0))
					throw new SAXException("empty element");
				//
				xform = true;
			} catch (SAXException | DocumentException e) {
				logger.error("XML parsing error", e);
				resp.sendError(500, e.getMessage());
			}
		}
		//
		SqlSession sqlSess = null;
		//
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();
			UserRolesMapper urm = sqlSess.getMapper(UserRolesMapper.class);
			for (String eachUser : userNames)
				for (String eachRole : roleNames) {
					UserRoles assignment = new UserRoles();
					assignment.setUserName(eachUser);
					assignment.setUserRoles(eachRole);
					assignment.setUserRoleExpiration(expiration);
					// editors (and admins) can add new mapping of users to
					// manageable
					// role
					if (Utility.gotPermissionForRole(strGroup, roleList, assignment.getUserRoles())) {
						urm.deleteByPrimaryKey(assignment);
						urm.insert(assignment);
						sqlSess.commit();
					} // skip unauthorized operation
				}
			//
			if (!xform)
				resp.sendRedirect("assign?listUser=" + req.getParameter("listUser") + "&listRole="
						+ req.getParameter("listRole"));
			else
				resp.setStatus(200);
		} catch (Exception e) {
			logger.error("error processing assignment management post.", e);
			if (!xform) {
				req.setAttribute("error", e.getMessage());
				RequestDispatcher view = req.getRequestDispatcher(LIST_ASSIGNMENT);
				view.forward(req, resp);
			} else
				resp.sendError(500, e.getMessage());
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
	}

	private void populate(HttpServletRequest req) {
		SqlSession sqlSess = null;
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();
			UsersMapper um = sqlSess.getMapper(UsersMapper.class);
			List<Users> users = um.selectByExample(new UsersExample());
			req.setAttribute("users", users);
			//
			RoleInfoExample exp = new RoleInfoExample();
			exp.setOrderByClause("\"app_name\", COALESCE(\"form_name\",'')");
			//
			RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
			List<RoleInfo> roles = rmapper.selectByExample(exp);
			req.setAttribute("roles", roles);
			//
		} catch (Exception e) {
			logger.error("error while populating basic info", e);
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
	}
}
