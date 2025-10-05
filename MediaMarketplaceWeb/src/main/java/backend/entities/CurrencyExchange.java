package backend.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency_exchanges")
public class CurrencyExchange {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "from_currency_code")
	private CurrencyKind fromCurrencyKind;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "to_currency_code")
	private CurrencyKind toCurrencyKind;
	
	@Column(nullable = false, precision = 19, scale = 6)
	private BigDecimal rate; // e.g. 3.5 means 1 unit of fromCurrency = 3.5 units of toCurrency
	
	@Column(nullable = false)
	private LocalDateTime lastUpdated;

	public Long getId() {
		return id;
	}

	public CurrencyKind getFromCurrencyKind() {
		return fromCurrencyKind;
	}

	public CurrencyKind getToCurrencyKind() {
		return toCurrencyKind;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public LocalDateTime getLastUpdated() {
		return lastUpdated;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setFromCurrencyKind(CurrencyKind fromCurrencyKind) {
		this.fromCurrencyKind = fromCurrencyKind;
	}

	public void setToCurrencyKind(CurrencyKind toCurrencyKind) {
		this.toCurrencyKind = toCurrencyKind;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public void setLastUpdated(LocalDateTime lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
}
