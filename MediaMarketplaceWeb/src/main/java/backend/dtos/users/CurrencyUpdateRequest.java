package backend.dtos.users;

import jakarta.validation.constraints.NotEmpty;

public class CurrencyUpdateRequest {
	
	@NotEmpty
	private String currencyCode;
	
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
}