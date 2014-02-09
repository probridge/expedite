package com.probridge.expedite.webapp;

public class Constant {
	public static String configPath = null;

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
	public static final String SESSION_AUTH_FLAG = "Exp-Auth";
	public static final String SESSION_USER_NAME = "Exp-UserName";
	public static final String SESSION_GROUP_NAME = "Exp-GroupName";
	public static final String SESSION_ROLE_LIST = "Exp-RoleList";

	public static final String jAccountSuffix = "@sjtu.edu.cn";

	static {
		String prefix = "WEB-INF/";
		String path = Constant.class.getResource("/").getPath();
		configPath = path.substring(0, path.indexOf(prefix) + prefix.length());
	}
}
