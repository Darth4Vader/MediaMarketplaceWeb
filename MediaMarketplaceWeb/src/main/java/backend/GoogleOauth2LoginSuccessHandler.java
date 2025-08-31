package backend;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.users.LoginResponse;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.services.UserAuthenticateService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class GoogleOauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired private UserAuthenticateService userAuthenticateService;
    
    @Autowired
    private ConfigValues configValues;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        
        // get or create user in database
        User user;
		try {
			user = userAuthenticateService.getUserViaOAuth(oauth2User);
		} catch (LogValuesAreIncorrectException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		//login via ouath2
		LoginResponse login = userAuthenticateService.loginViaOAuth(user);
        
        // Set tokens in HTTP-only cookies and response body
        UserAuthenticateController.createAuthenticationResponse(response, login);

        //redirects to the frontend returnUrl or default
        HttpSession session = request.getSession(false);
        String returnUrl = "";
        Object returnUrlObj = session.getAttribute("returnUrl");
        if(returnUrlObj instanceof String && DataUtils.isNotBlank((String) returnUrlObj)) {
        	returnUrl = (String) returnUrlObj;
		}
        else {
        	returnUrl = configValues.getFrontendUrl();
        }
        response.sendRedirect(returnUrl);
    }
}