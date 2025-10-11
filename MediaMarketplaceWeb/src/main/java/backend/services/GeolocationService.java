package backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import backend.dtos.general.IpApiResponse;
import backend.utils.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class GeolocationService {
	
	private static final Logger SESSION_LOGGER = LoggerFactory.getLogger("myapp.logging.session");
	
	private static final String GEO_SEARCHED = "geoSearched";
	
	private static final String IPAPI_URL_TEMPLATE = "https://ipinfo.io/%s/json/";
	
	public String getCountryOfSession(HttpServletRequest request) {
		if(request == null) return null;
		// if we use AWS Cloudfront, then it will get the country from there
		String countryCode = request.getHeader("CloudFront-Viewer-Country");
		// for logging information, we will print the country code and the ip address
		if(countryCode == null || countryCode.isEmpty()) {
			// if cloudfront does not work, then we will the geolocation service
			countryCode = getGeolocationCountry(request.getSession(), request);
		}
		SESSION_LOGGER.info("Client IP: {} has Country code : {}", RequestUtils.getClientIpForCloudflare(request), countryCode);
		return countryCode;
	}
	
	private String getGeolocationCountry(HttpSession session, HttpServletRequest request) {
		if(session != null) {
			loadGeolocationInformation(session, request);
			return (String)session.getAttribute("Country");
		}
		return null;
	}
	
	private void loadGeolocationInformation(HttpSession session, HttpServletRequest request) {
    	if(session != null && session.getAttribute("Country") == null) {
        	if(session.getAttribute(GEO_SEARCHED) == null) {
        		session.setAttribute(GEO_SEARCHED, "TRUE");
        		String clientIp = RequestUtils.getClientIpForCloudflare(request);
        		if(RequestUtils.isIpReal(clientIp)) {
	        		IpApiResponse geo = fetchGeolocation(clientIp);
	        		String country = geo != null ? geo.getCountry() : null;
	        		session.setAttribute("Country", country);
        		}
        	}
    	}
	}

	private IpApiResponse fetchGeolocation(String ip) {
		String url = String.format(IPAPI_URL_TEMPLATE, ip);
		try {
			// Make POST request using RestClient
			RestClient restClient = RestClient.create();
			ResponseEntity<IpApiResponse> response = restClient.get()
					.uri(url)
					.accept(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(IpApiResponse.class); // Convert the response to IpApiResponse
			return response.getBody();
		} catch (Exception e) {
			// Handle errors (e.g., logging), return null or default response as fallback
			e.printStackTrace();
			return null;
		}
	}
}