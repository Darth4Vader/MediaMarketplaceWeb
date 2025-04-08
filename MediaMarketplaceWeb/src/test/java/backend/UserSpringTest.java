package backend;

import backend.dtos.users.LogInDto;

public class UserSpringTest extends BaseAuthenticationSpringTest {
	
	private static boolean setUpIsDone = false;
	private static String authToken;
	
	@Override
	protected LogInDto getLoginDto() {
		LogInDto loginDto = new LogInDto();
		loginDto.setUsername("frodo");
		loginDto.setPassword("bag");
		return loginDto;
	}

	@Override
	protected boolean isSetUpIsDone() {
		return setUpIsDone;
	}

	@Override
	protected String getAuthToken() {
		return authToken;
	}
	
	@Override
	protected void setSetUpIsDone(boolean setUpIsDone) {
		UserSpringTest.setUpIsDone = setUpIsDone;
	}

	@Override
	protected void setAuthToken(String authToken) {
		UserSpringTest.authToken = authToken;
	}
}