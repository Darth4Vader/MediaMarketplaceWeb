package backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import backend.entities.AccountVerificationToken;
import backend.entities.User;

@Repository
public interface AccountVerificationTokenRepository extends JpaRepository<AccountVerificationToken, Long> {

	Optional<AccountVerificationToken> findByToken(String token);
	
	Optional<AccountVerificationToken> findByUser(User user);
}