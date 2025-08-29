package backend.exceptions;

import backend.dtos.users.VerifyAccountDto;

public class UserNotVerifiedException extends Exception {
	
	private VerifyAccountDto verifyAccountDto;
	private String redirectUrl;

	public UserNotVerifiedException() {
		// TODO Auto-generated constructor stub
	}
	
	public UserNotVerifiedException(VerifyAccountDto verifyAccountDto, String redirectUrl, String message) {
		this(message);
		this.verifyAccountDto = verifyAccountDto;
		this.redirectUrl = redirectUrl;
	}

	public UserNotVerifiedException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public UserNotVerifiedException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public UserNotVerifiedException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public UserNotVerifiedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}
	
	public VerifyAccountDto getVerifyAccountDto() {
		return verifyAccountDto;
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
}
