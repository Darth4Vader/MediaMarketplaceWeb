package backend.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import backend.DataUtils;
import backend.dtos.general.TurnstileResponse;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.exceptions.enums.UserLogInfo;

@Service
public class CloudflareTurnstileService {
	
	private static final String SITEVERIFY_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify";
	
	@Value("${cloudflare.turnstile.secret-key}")
	private String secretKey;

	public TurnstileResponse validateToken(String token, String remoteip) throws LogValuesAreIncorrectException {
		checkTurnstileTokenExceptions(token);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("secret", secretKey);
		params.add("response", token);
		if (remoteip != null) {
			params.add("remoteip", remoteip);
		}
		
		try {
			// Make POST request using RestClient
			RestClient restClient = RestClient.create();
			ResponseEntity<TurnstileResponse> response = restClient.post()
					.uri(SITEVERIFY_URL)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
					.body(params)
					.retrieve()
					.toEntity(TurnstileResponse.class); // Convert the response to TurnstileResponse
			return response.getBody();
		} catch (Exception e) {
			e.printStackTrace();
			TurnstileResponse errorResponse = new TurnstileResponse();
			errorResponse.setSuccess(false);
			errorResponse.setErrorCodes(List.of("internal-error"));
			return errorResponse;
		}
	}
	
	private static void checkTurnstileTokenExceptions(String turnstileToken) throws LogValuesAreIncorrectException {
		Map<UserLogInfo, String> logInfo = new HashMap<>();
		if (DataUtils.isBlank(turnstileToken)) {
			logInfo.put(UserLogInfo.HUMAN_VERIFICATION, "Please verify that you are a human");
		}
        if (!logInfo.isEmpty()) {
            throw new LogValuesAreIncorrectException(logInfo, "One or more values are missing");
        }
	}
}