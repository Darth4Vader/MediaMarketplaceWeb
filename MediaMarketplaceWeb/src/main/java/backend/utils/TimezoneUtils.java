package backend.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class TimezoneUtils {
	
	public static HttpServletRequest getCurrentHttpRequest(){
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    if (requestAttributes instanceof ServletRequestAttributes) {
	        HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	        return request;
	    }
	    return null;
	}
	
	public static ZoneId getRequestTimezone() {
		HttpServletRequest request = getCurrentHttpRequest();
		if(request != null) {
			Cookie timezoneCookie = WebUtils.getCookie(request, "timezone");
			if(timezoneCookie != null) {
				String timezoneValue = timezoneCookie.getValue();
				try {
					return ZoneId.of(timezoneValue);
				}
				catch (Exception e) {
					// avoid invalid timezone
				}
			}
		}
		return null;
	}
	
	public static LocalDateTime convertToRequestTimezone(LocalDateTime utcDateTime) {
		if(utcDateTime == null) {
			return null;
		}
		ZoneId timezone = getRequestTimezone();
		if(timezone != null) {
			return utcDateTime
					.atZone(ZoneOffset.UTC) // Convert to UTC first
					.withZoneSameInstant(timezone)
					.toLocalDateTime();
		}
		return utcDateTime;
	}

}
