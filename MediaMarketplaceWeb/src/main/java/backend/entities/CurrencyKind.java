package backend.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "currency_kinds")
public class CurrencyKind {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 3)
	private String code; // e.g. "USD"
	
	@Column(nullable = false)
	private String name; // e.g. "United States Dollar"
	
	private String symbol; // e.g. "$"

	public Long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}	
}