package backend;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import backend.controllers.UserAuthenticateController;
import backend.dtos.users.LoginResponse;
import backend.dtos.users.UserInformationDto;
import backend.entities.User;
import backend.exceptions.LogValuesAreIncorrectException;
import backend.repositories.UserRepository;
import backend.services.RefreshTokenService;
import backend.services.TokenService;
import backend.services.UserAuthenticateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleOauth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired private UserRepository userRepository;
    @Autowired private UserAuthenticateService userAuthenticateService;
    @Autowired private TokenService tokenService;
    @Autowired private RefreshTokenService refreshTokenService;
    
    @Autowired
    private ConfigValues configValues;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("Nammmm");
        System.out.println(oauth2User);
        System.out.println(oauth2User.getAttributes());
        String email = oauth2User.getAttribute("email");
        
        // Try to find the user by email, otherwise register a new one
        User user = userAuthenticateService.findUserByEmail(email)
        		.orElseGet(() -> {
                    UserInformationDto userInfo = new UserInformationDto();
                    userInfo.setEmail(email);
            		try {
						return userAuthenticateService.registerViaOAuth(userInfo);
					} catch (LogValuesAreIncorrectException e) {
						throw new RuntimeException(e);
					}
                });
        
        // oauth verified email, so we set the user as verified
        userAuthenticateService.verifyAccount(user);
        
        //update user info from oauth2 provider
        user.setName(oauth2User.getAttribute("name"));
        user.setProfilePicture(oauth2User.getAttribute("picture"));
        userRepository.save(user);

        // Generate oauth2 tokens
        String accessToken = tokenService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setAccessToken(accessToken);
        loginResponse.setRefreshToken(refreshToken);
        
        // Set tokens in HTTP-only cookies and response body
        UserAuthenticateController.createAuthenticationResponse(response, loginResponse);

        // redirect back to frontend after successful login
        response.sendRedirect(configValues.getFrontendUrl());
    }

}
