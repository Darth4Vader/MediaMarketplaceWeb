package backend;

import backend.dtos.users.LogInDto;

public class AdminSpringTest extends BaseAuthenticationSpringTest {
	
    private static boolean setUpIsDone = false;
    private static String authToken;
    
	@Override
	protected LogInDto getLoginDto() {
		LogInDto loginDto = new LogInDto();
		loginDto.setUsername("bilbo");
		loginDto.setPassword("bag2");
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
		AdminSpringTest.setUpIsDone = setUpIsDone;
	}

	@Override
	protected void setAuthToken(String authToken) {
		AdminSpringTest.authToken = authToken;
	}
}