package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class NewPasswordToken extends AbstractEntity {

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "EMAIL", nullable = false)
    private String email;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDateTime expiryDate;

    public String getToken() {
        return token;
    }

    public NewPasswordToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public NewPasswordToken setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public NewPasswordToken setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }
}
