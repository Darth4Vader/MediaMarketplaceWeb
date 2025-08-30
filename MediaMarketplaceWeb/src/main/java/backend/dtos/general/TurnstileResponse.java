package backend.dtos.general;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TurnstileResponse {
	
	private boolean success;
	
	@JsonProperty("error-codes")
	private List<String> errorCodes;
	
	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public List<String> getErrorCodes() {
		return errorCodes;
	}
	
	public void setErrorCodes(List<String> errorCodes) {
		this.errorCodes = errorCodes;
	}
}