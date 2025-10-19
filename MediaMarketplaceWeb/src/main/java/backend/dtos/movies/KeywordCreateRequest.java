package backend.dtos.movies;

import jakarta.validation.constraints.NotBlank;

public class KeywordCreateRequest {

	@NotBlank
	private String name;

	@NotBlank
	private String mediaID;

	public KeywordCreateRequest() {
		// Default constructor
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMediaID() {
		return mediaID;
	}

	public void setMediaID(String mediaID) {
		this.mediaID = mediaID;
	}
}