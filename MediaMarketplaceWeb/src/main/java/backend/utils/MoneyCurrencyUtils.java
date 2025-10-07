package backend.utils;

import java.math.BigDecimal;

import org.joda.money.Money;

import backend.dtos.general.PriceDto;
import backend.entities.CurrencyKind;

public class MoneyCurrencyUtils {
	
	public static PriceDto convertMoneyToDto(Money money, CurrencyKind currencyKind) {
		return convertMoneyToDto(money.getAmount(), currencyKind);
	}
	
	public static PriceDto convertMoneyToDto(BigDecimal amount, CurrencyKind currencyKind) {
		PriceDto dto = new PriceDto();
		dto.setAmount(amount);
		dto.setCurrency(currencyKind.getSymbol());
		return dto;
	}

}