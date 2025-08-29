package backend.dtos.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public class ResetPasswordTokenRequest {
	
	private String email;
	
    @JsonProperty(required = true)
    @NotNull(message="Please provide a valid redirect URL")
	private String redirectUrl;
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
}
