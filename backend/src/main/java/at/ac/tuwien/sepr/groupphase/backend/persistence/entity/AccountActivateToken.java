package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class AccountActivateToken extends AbstractEntity {

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public String getToken() {
        return token;
    }

    public AccountActivateToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public AccountActivateToken setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public AccountActivateToken setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }
}
