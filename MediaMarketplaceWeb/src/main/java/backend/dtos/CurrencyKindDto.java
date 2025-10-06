package backend.dtos;

import java.util.List;

public class CurrencyKindDto {
	
	public record CountryDto(String code, String name) {}
	
	private String currencyName;
	
	private String currencyCode;
	
	private String currencySymbol;
	
	private CountryDto mainCountry;
	
	private List<CountryDto> countries;

	public String getCurrencyName() {
		return currencyName;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}

	public CountryDto getMainCountry() {
		return mainCountry;
	}

	public List<CountryDto> getCountries() {
		return countries;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}

	public void setMainCountry(CountryDto mainCountry) {
		this.mainCountry = mainCountry;
	}

	public void setCountries(List<CountryDto> countries) {
		this.countries = countries;
	}
}