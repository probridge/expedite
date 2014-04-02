package com.probridge.expedite.webapp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.probridge.expedite.model.expdb.RoleInfo;

public class Utility {
	private static final Logger logger = LoggerFactory.getLogger(Utility.class);

	public static String getStringVal(Object obj) {
		return (obj == null ? null : obj.toString());
	}

	public static boolean isZeroOrNull(Integer val) {
		return (val == null || val == 0);
	}

	public static boolean isEmptyOrNull(String val) {
		return (val == null || val.isEmpty());
	}

	public static boolean gotPermissionForRole(String group, HashSet<String> roles, String roleName) {
		if (Constant.GROUP_EDITOR.equals(group)) {
			for (String eachRole : roles)
				if (eachRole.endsWith(Constant.ROLE_EDITOR_SUFFIX)) {
					String app = eachRole.substring(0, eachRole.lastIndexOf(Constant.ROLE_EDITOR_SUFFIX));
					if (roleName.startsWith(app + "-") && roleName.endsWith(Constant.ROLE_PARTICIPANT_SUFFIX))
						return true;
				}
		}
		if (Constant.GROUP_ADMIN.equals(group))
			return true;
		//
		return false;
	}

	public static void updatePermissionFile(List<RoleInfo> roles) {
		Writer out = null;
		try {
			File permissionFile = new File(Constant.permissionFilePath);
			permissionFile.createNewFile();
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(permissionFile), "UTF-8"));
			out.write("<roles>\n");
			for (RoleInfo role : roles)
				if (role.getFormName() == null)
					out.write("<role name=\"" + role.getRoleName() + "\" app=\"" + role.getAppName()
							+ "\" form=\"*\"/>\n");
			out.write("</roles>\n");
		} catch (IOException e) {
			logger.error("Could not write permission file.", e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}
}
