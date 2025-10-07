package backend.dtos.general;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PriceDto {
	
	@JsonFormat(shape=JsonFormat.Shape.STRING)
	private BigDecimal amount;
	
	private String currency;

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}