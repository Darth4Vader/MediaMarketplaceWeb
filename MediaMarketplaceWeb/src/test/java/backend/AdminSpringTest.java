package backend;

import backend.dtos.users.LogInDto;

public class AdminSpringTest extends BaseAuthenticationSpringTest {
	
    private static boolean setUpIsDone = false;
    private static String accessToken;
    private static String refreshToken;
    
	@Override
	protected LogInDto getLoginDto() {
		LogInDto loginDto = new LogInDto();
		loginDto.setEmail("bilbo");
		loginDto.setPassword("bag2");
		return loginDto;
	}

	@Override
	protected boolean isSetUpIsDone() {
		return setUpIsDone;
	}

	@Override
	protected String getAccessToken() {
		return accessToken;
	}
	
	@Override
    protected String getRefreshToken() {
		return refreshToken;
	}
	
	@Override
	protected void setSetUpIsDone(boolean setUpIsDone) {
		AdminSpringTest.setUpIsDone = setUpIsDone;
	}

	@Override
	protected void setAccessToken(String accessToken) {
		AdminSpringTest.accessToken = accessToken;
	}
	
	@Override
    protected void setRefreshToken(String refreshToken) {
		AdminSpringTest.refreshToken = refreshToken;
    }
}