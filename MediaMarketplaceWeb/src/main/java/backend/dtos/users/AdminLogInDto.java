package backend.dtos.users;

/**
 * Data Transfer Object for admin login information.
 */
public class AdminLogInDto {

	/**
	 * The username of the admin attempting to log in.
	 */
	private String username;

	/**
	 * The password of the admin attempting to log in.
	 */
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}