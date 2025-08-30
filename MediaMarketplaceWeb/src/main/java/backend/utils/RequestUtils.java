package backend.utils;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {

	public static String getClientIpForCloudflare(HttpServletRequest request) {
	    String remoteip = request.getHeader("CF-Connecting-IP");
	    if (remoteip == null) {
	        remoteip = request.getHeader("X-Forwarded-For");
	    }
	    if (remoteip == null) {
	        remoteip = request.getRemoteAddr();
	    }
	    return remoteip;
	}
}