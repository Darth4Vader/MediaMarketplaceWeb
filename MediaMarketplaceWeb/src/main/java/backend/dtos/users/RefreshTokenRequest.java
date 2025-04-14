package backend.dtos.users;

public class RefreshTokenRequest {
	
	private String refreshToken;

	public RefreshTokenRequest() {
		// TODO Auto-generated constructor stub
	}
	
	public RefreshTokenRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	public String getRefreshToken() {
		return refreshToken;
	}
	
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
