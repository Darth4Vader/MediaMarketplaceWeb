package backend.utils;

import java.math.BigDecimal;
import java.util.Locale;

import org.joda.money.Money;

import backend.dtos.general.CountryDto;
import backend.dtos.general.CurrencyDto;
import backend.dtos.general.PriceDto;
import backend.entities.CurrencyKind;

public class I18nUtils {
	
	public static final String DEFAULT_CURRENCY = "USD";
	public static final String DEFAULT_COUNTRY = "US";
	
	public static Locale getLocaleForCountry(String countryCode) {
		if(countryCode == null || countryCode.isEmpty()) {
			countryCode = DEFAULT_COUNTRY;
		}
		for (Locale locale : Locale.getAvailableLocales()) {
			if (locale.getCountry().equalsIgnoreCase(countryCode) && !locale.getLanguage().isEmpty()) {
				return locale;
			}
		}
		return Locale.of("", countryCode);  // fallback with no language
	}
	
	public static CountryDto convertCountryToDto(String countryCode) {
		CountryDto dto = new CountryDto();
		dto.setCode(countryCode);
		Locale locale = getLocaleForCountry(countryCode);
		dto.setName(locale.getDisplayCountry(Locale.ENGLISH)); // always in English
		return dto;
	}
	
	public static PriceDto convertMoneyToDto(Money money, CurrencyKind currencyKind) {
		return convertMoneyToDto(money.getAmount(), currencyKind);
	}
	
	public static PriceDto convertMoneyToDto(BigDecimal amount, CurrencyKind currencyKind) {
		PriceDto dto = new PriceDto();
		dto.setAmount(amount);
		CurrencyDto currencyDto = new CurrencyDto();
		currencyDto.setCode(currencyKind.getCode());
		currencyDto.setName(currencyKind.getName());
		currencyDto.setSymbol(currencyKind.getSymbol());
		dto.setCurrency(currencyDto);
		return dto;
	}

}