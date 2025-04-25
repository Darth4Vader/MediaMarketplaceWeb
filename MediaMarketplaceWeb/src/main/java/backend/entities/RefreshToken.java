package backend.entities;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 600, unique = true, nullable = false)
    private String token;
    
    private Instant issuedAt;
    
    private Instant expiryDate;
    
    private boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

	public RefreshToken() {
		this.isRevoked = false;
	}
	
	public RefreshToken(String token, Instant expiryDate, User user) {
		this.token = token;
		this.expiryDate = expiryDate;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public String getToken() {
		return token;
	}
	
	public Instant getIssuedAt() {
		return issuedAt;
	}

	public Instant getExpiryDate() {
		return expiryDate;
	}

	public User getUser() {
		return user;
	}
	
	public boolean isRevoked() {
		return isRevoked;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public void setIssuedAt(Instant issuedAt) {
		this.issuedAt = issuedAt;
	}

	public void setExpiryDate(Instant expiryDate) {
		this.expiryDate = expiryDate;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void setRevoked(boolean isRevoked) {
		this.isRevoked = isRevoked;
	}
}
