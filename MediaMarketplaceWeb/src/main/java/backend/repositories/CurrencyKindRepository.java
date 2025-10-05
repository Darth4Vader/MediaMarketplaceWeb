package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.CurrencyKind;

@Repository
public interface CurrencyKindRepository extends JpaRepository<CurrencyKind, Long> {
	
	public Optional<CurrencyKind> findByCode(String code);
	
}