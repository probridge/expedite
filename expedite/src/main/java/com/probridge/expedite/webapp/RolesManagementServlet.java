package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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

import com.probridge.expedite.dao.expdb.RoleInfoMapper;
import com.probridge.expedite.dao.expdb.UserRolesMapper;
import com.probridge.expedite.model.expdb.RoleInfo;
import com.probridge.expedite.model.expdb.RoleInfoExample;
import com.probridge.expedite.model.expdb.UserRolesExample;

public class RolesManagementServlet extends HttpServlet {

	private static final long serialVersionUID = -5761849563881652897L;
	private static String LIST_ROLES = "/pages/list-roles.jsp";
	private static final Logger logger = LoggerFactory.getLogger(RolesManagementServlet.class);

	public RolesManagementServlet() {
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("action");
		String forward = "";
		//
		HttpSession sess = req.getSession();
		String strGroup = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		//
		if (!(Constant.GROUP_EDITOR.equals(strGroup) || Constant.GROUP_ADMIN.equals(strGroup))) {
			resp.sendError(401, "无访问权限");
			return;
		}
		//
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		HashSet<String> roleList = new HashSet<String>();
		if (strRoleLists != null)
			roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
		//
		SqlSession sqlSess = null;
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();
			if ("delete".equals(action)) {
				String roleName = req.getParameter("roleName");
				if (!Utility.gotPermissionForRole(strGroup, roleList, roleName)) {
					resp.sendError(401, "无访问权限");
					return;
				}
				if ((Constant.SANDBOX_DB_NAME + Constant.ROLE_EDITOR_SUFFIX).equals(roleName)) {
					resp.sendError(500, "不允许删除系统角色");
					return;
				}
				RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
				rmapper.deleteByPrimaryKey(roleName);
				// also delete all user role assignment
				UserRolesMapper umapper = sqlSess.getMapper(UserRolesMapper.class);
				UserRolesExample exp2 = new UserRolesExample();
				exp2.createCriteria().andUserRolesEqualTo(roleName);
				umapper.deleteByExample(exp2);
				sqlSess.commit();
				// only update the permission file by admin(which is able to
				// change
				// editor role for app)
				if (Constant.GROUP_ADMIN.equals(strGroup)) {
					RoleInfoExample exp = new RoleInfoExample();
					exp.createCriteria().andFormNameIsNull();
					Utility.updatePermissionFile(rmapper.selectByExample(exp));
				}
			} else if ("edit".equals(action)) {
				String roleName = req.getParameter("roleName");
				if (!Utility.gotPermissionForRole(strGroup, roleList, roleName)) {
					resp.sendError(401, "无访问权限");
					return;
				}
				RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
				RoleInfo role = rmapper.selectByPrimaryKey(roleName);
				req.setAttribute("role", role);
			}
			RoleInfoExample exp = new RoleInfoExample();
			exp.setOrderByClause("\"app_name\", COALESCE(\"form_name\",'')");
			//
			if (Constant.GROUP_EDITOR.equals(strGroup)) {
				for (String eachRole : roleList) {
					if (eachRole.endsWith(Constant.ROLE_EDITOR_SUFFIX)) {
						String app = eachRole.substring(0, eachRole.lastIndexOf(Constant.ROLE_EDITOR_SUFFIX));
						exp.or().andRoleNameLike(app + "-%");
					}
				}
			}
			//
			RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
			List<RoleInfo> roles = rmapper.selectByExample(exp);
			req.setAttribute("roles", roles);
			req.setAttribute("editorSuffix", Constant.ROLE_EDITOR_SUFFIX);
			req.setAttribute("participantSuffix", Constant.ROLE_PARTICIPANT_SUFFIX);
			req.setAttribute("sandboxApp", Constant.SANDBOX_DB_NAME);
			req.setAttribute("isAdmin", Constant.GROUP_ADMIN.equals(strGroup));
			req.setAttribute("isEditor", Constant.GROUP_EDITOR.equals(strGroup));
			forward = LIST_ROLES;
		} catch (Exception e) {
			logger.error("error processing role management action " + action, e);
			sqlSess.rollback();
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
		RequestDispatcher view = req.getRequestDispatcher(forward);
		view.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		RoleInfo role = new RoleInfo();
		role.setRoleName(req.getParameter("roleName"));
		role.setAppName(req.getParameter("appName"));
		role.setFormName(req.getParameter("formName"));
		role.setDescription(req.getParameter("description"));
		try {
			role.setDataLimit(Integer.parseInt(req.getParameter("dataLimit")));
		} catch (Exception e) {
			role.setDataLimit(null);
		}
		//
		HttpSession sess = req.getSession();
		String strGroup = Utility.getStringVal(sess.getAttribute(Constant.SESSION_GROUP_NAME));
		//
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		HashSet<String> roleList = new HashSet<String>();
		if (strRoleLists != null)
			roleList.addAll(Arrays.asList(strRoleLists.split("\\s*,\\s*")));
		//
		// allow only participant role of the managing app for editors
		if (!Utility.gotPermissionForRole(strGroup, roleList, role.getRoleName())) {
			resp.sendError(401, "无访问权限");
			return;
		}
		//
		if ((Constant.SANDBOX_DB_NAME + Constant.ROLE_EDITOR_SUFFIX).equals(role.getRoleName())) {
			resp.sendError(500, "不允许修改系统角色");
			return;
		}
		//
		SqlSession sqlSess = null;
		List<RoleInfo> roles = null;
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();
			RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
			int affected = rmapper.updateByPrimaryKey(role);
			if (affected == 0) {
				rmapper.insert(role);
			}
			sqlSess.commit();
			// only update the permission file by admin(which is able to change
			// editor role for app)
			if (Constant.GROUP_ADMIN.equals(strGroup)) {
				RoleInfoExample exp = new RoleInfoExample();
				exp.createCriteria().andFormNameIsNull();
				roles = rmapper.selectByExample(exp);
				Utility.updatePermissionFile(roles);
			}
			resp.sendRedirect("roles");
		} catch (Exception e) {
			logger.error("error processing role management post.", e);
			req.setAttribute("error", e.getMessage());
			RequestDispatcher view = req.getRequestDispatcher(LIST_ROLES);
			view.forward(req, resp);
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
	}
}
