package com.probridge.expedite.webapp;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.dao.expdb.RoleInfoMapper;
import com.probridge.expedite.model.expdb.RoleInfo;
import com.probridge.expedite.model.expdb.RoleInfoExample;

public class Constant {
	private static final Logger logger = LoggerFactory.getLogger(Constant.class);

	public static String configPath = null;

	public static SqlSessionFactory sqlSessionFactory = null;

	public static final String ANONYMOUS = "anonymous";
	public static final String AUTH_FILTER_USER_NAME = "oxf.fr.authentication.header.username";
	public static final String AUTH_FILTER_GROUP_NAME = "oxf.fr.authentication.header.group";
	public static final String AUTH_FILTER_ROLES = "oxf.fr.authentication.header.roles";
	public static final String AUTH_FILTER_UNPROTECTED = "expedite.unprotected";

	public static final String RESOURCE_BASE_URL = "resources";

	public static final String jAccountSiteId = "jaantaicloud131015";

	/**
	 * "true(lowercase)" or NOT
	 */
	public static final String SESSION_AUTH_FLAG = "Exp-Authenticated";
	public static final String SESSION_USER_NAME = "Exp-User-Name";
	public static final String SESSION_GROUP_NAME = "Exp-Group-Name";
	public static final String SESSION_ROLE_LIST = "Exp-Role-List";
	
	public static final String jAccountSuffix = "@sjtu.edu.cn";

	public static final String SESSION_AUTH_TOKEN = "Exp-Auth-Token";

	public static final String USERINFO_APP = "survey";
	public static final String USERINFO_FORM = "userinfo";
	public static final String USERINFO_EMAIL_CONTROL = "email";
	public static final String USERINFO_EMAIL_PATH = "section-basic-info/" + USERINFO_EMAIL_CONTROL;
	
	public static final String NEWS_TITLE_CONTROL = "title";
	public static final String NEWS_TITLE_PATH = "main-section/" + NEWS_TITLE_CONTROL;

	public static final String GROUP_USER = "GroupUser";
	public static final String GROUP_EDITOR = "GroupEditor";
	public static final String GROUP_ADMIN = "GroupAdmin";

	public static final String ROLE_EDITOR_SUFFIX = "-editor";
	public static final String ROLE_PARTICIPANT_SUFFIX = "-participant";
	
	public static final String SANDBOX_DB_NAME = "sandbox";

	public static String permissionFilePath = null;

	static {
		String prefix = "WEB-INF/";
		String path = Constant.class.getResource("/").getPath();
		configPath = path.substring(0, path.indexOf(prefix) + prefix.length());
		permissionFilePath = configPath + "resources/config/form-builder-permissions.xml";
		//
		try {
			logger.info("Expedite Initializing...");
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("../mybatis.xml"));
			//
			SqlSession sqlSess = null;
			try {
				sqlSess = Constant.sqlSessionFactory.openSession();
				logger.info("Loading role information...");
				RoleInfoMapper rmapper = sqlSess.getMapper(RoleInfoMapper.class);
				RoleInfoExample exp = new RoleInfoExample();
				exp.createCriteria().andFormNameIsNull().andRoleExpirationIsNull();
				exp.or().andFormNameIsNull().andRoleExpirationGreaterThan(new Date());
				List<RoleInfo> roles = rmapper.selectByExample(exp);
				logger.info("Writing role configuraion file: " + permissionFilePath);
				Utility.updatePermissionFile(roles);
			} catch (Exception e) {
				logger.error("Error while initializing..", e);
				e.printStackTrace();
			} finally {
				if (sqlSess != null)
					sqlSess.close();
			}
		} catch (IOException e) {
			logger.error("Error getting DB connection information.", e);
			throw new RuntimeException(e);
		}
	}
}
