package backend;

import backend.dtos.users.LogInDto;

public class UserSpringTest extends BaseAuthenticationSpringTest {
	
    private static boolean setUpIsDone = false;
    private static String accessToken;
    private static String refreshToken;
    
	@Override
	protected LogInDto getLoginDto() {
		LogInDto loginDto = new LogInDto();
		loginDto.setEmail("frodo");
		loginDto.setPassword("bag");
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
	protected void setSetUpIsDone(boolean setUpIsDone1) {
		setUpIsDone = setUpIsDone1;
	}

	@Override
	protected void setAccessToken(String accessToken1) {
		accessToken = accessToken1;
	}
	
	@Override
    protected void setRefreshToken(String refreshToken1) {
		refreshToken = refreshToken1;
    }
}