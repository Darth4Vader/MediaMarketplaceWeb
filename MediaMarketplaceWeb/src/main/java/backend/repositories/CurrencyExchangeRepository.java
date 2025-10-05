package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.CurrencyExchange;
import backend.entities.CurrencyKind;

@Repository
public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long> {
	
	public Optional<CurrencyExchange> findByFromCurrencyKindAndToCurrencyKind(CurrencyKind fromCurrencyKind, CurrencyKind toCurrencyKind);
	
    Optional<CurrencyExchange> findTopByOrderByLastUpdatedDesc();
}