package com.probridge.expedite.webapp;

public class Utility {

	public static String getStringVal(Object obj) {
		return (obj == null ? null : obj.toString());
	}

	public static boolean isZeroOrNull(Integer val) {
		return (val == null || val == 0);
	}

	public static boolean isEmptyOrNull(String val) {
		return (val == null || val.isEmpty());
	}

}
