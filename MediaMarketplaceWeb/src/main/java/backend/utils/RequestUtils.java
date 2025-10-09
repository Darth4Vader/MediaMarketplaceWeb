package backend.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public class RequestUtils {
	
	public static HttpServletRequest getCurrentHttpRequest(){
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    if (requestAttributes instanceof ServletRequestAttributes) {
	        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	        return request;
	    }
	    return null;
	}

	public static String getClientIpForCloudflare(HttpServletRequest request) {
		String[] headerNames = {
				"CF-Connecting-IP",     // Cloudflare
				"X-Forwarded-For",      // Standard proxy header
				"X-Real-IP"             // Nginx or other proxies
			};

			for (String header : headerNames) {
				String ip = request.getHeader(header);
				if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
					// X-Forwarded-For can contain multiple IPs: client, proxy1, proxy2
					return ip.split(",")[0].trim();
				}
			}

			// Fallback
			String ip = request.getRemoteAddr();
			if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip) || "127.0.0.1".equals(ip)) {
				return "localhost";
			}

			return ip;
	}
	
	public static boolean isIpReal(String ip) {
		if(ip == null || ip.isBlank()) return false;
		if("unknown".equalsIgnoreCase(ip)) return false;
		if("localhost".equalsIgnoreCase(ip)) return false;
		if("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip) || "127.0.0.1".equals(ip)) return false;
		return true;
	}
}