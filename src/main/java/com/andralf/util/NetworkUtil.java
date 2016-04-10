package com.andralf.util;

import javax.servlet.http.HttpServletRequest;

public class NetworkUtil {

	private NetworkUtil(){}

	public static String getRemoteAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-FORWARDED-FOR");
		if (ipAddress == null) {
			ipAddress = request.getRemoteAddr();
		}
		return ipAddress;
	}
}
