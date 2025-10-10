package backend.dtos.general;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PriceDto {
	
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigDecimal amount;
	
	private CurrencyDto currency;

	public BigDecimal getAmount() {
		return amount;
	}

	public CurrencyDto getCurrency() {
		return currency;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setCurrency(CurrencyDto currency) {
		this.currency = currency;
	}
}