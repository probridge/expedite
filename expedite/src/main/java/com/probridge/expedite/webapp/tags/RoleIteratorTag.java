package com.probridge.expedite.webapp.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.RoleInfoMapper;
import com.probridge.expedite.model.expdb.RoleInfo;
import com.probridge.expedite.model.expdb.RoleInfoExample;
import com.probridge.expedite.webapp.Constant;
import com.probridge.expedite.webapp.Utility;

public class RoleIteratorTag extends SimpleTagSupport {

	private static final Logger logger = LoggerFactory.getLogger(RoleIteratorTag.class);
	private String mode;

	@Override
	public void doTag() throws JspException, IOException {
		PageContext context = (PageContext) getJspContext();
		HttpSession sess = context.getSession();
		String strRoleLists = Utility.getStringVal(sess.getAttribute(Constant.SESSION_ROLE_LIST));
		logger.debug("user roles = " + strRoleLists);
		String[] roleList = {};
		if (strRoleLists != null)
			roleList = strRoleLists.split("\\s*,\\s*");
		//
		SqlSession sqlSess = null;
		try {
			sqlSess = Constant.sqlSessionFactory.openSession();
			RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
			for (String eachRole : roleList) {
				if (("participant".equals(mode) && eachRole.endsWith(Constant.ROLE_PARTICIPANT_SUFFIX))
						|| ("editor".equals(mode) && eachRole.endsWith(Constant.ROLE_EDITOR_SUFFIX))) {
					RoleInfo role = rmapper.selectByPrimaryKey(eachRole);
					context.setAttribute("tagRoleName", role.getRoleName());
					context.setAttribute("tagAppName", role.getAppName());
					context.setAttribute("tagFormName", role.getFormName());
					context.setAttribute("tagDescription", role.getDescription());
					getJspBody().invoke(null);
				}
			}
			if ("forms".equals(mode)) {
				RoleInfoExample exp = new RoleInfoExample();
				exp.setOrderByClause("\"app_name\", COALESCE(\"form_name\",'')");
				for (String eachRole : roleList) {
					if (eachRole.endsWith(Constant.ROLE_EDITOR_SUFFIX)) {
						String app = eachRole.substring(0, eachRole.lastIndexOf(Constant.ROLE_EDITOR_SUFFIX));
						exp.or().andRoleNameLike(app + "-%");
					}
				}
				List<RoleInfo> formRoleList = rmapper.selectByExample(exp);
				for (RoleInfo role : formRoleList) {
					if (!role.getRoleName().endsWith(Constant.ROLE_EDITOR_SUFFIX)) {
						context.setAttribute("tagRoleName", role.getRoleName());
						context.setAttribute("tagAppName", role.getAppName());
						context.setAttribute("tagFormName", role.getFormName());
						context.setAttribute("tagDescription", role.getDescription());
						getJspBody().invoke(null);
					}
				}
			}
		} catch (Exception e) {
			logger.error("error processing role iterator tag", e);
		} finally {
			if (sqlSess != null)
				sqlSess.close();
		}
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}
}