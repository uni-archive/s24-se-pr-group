package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.time.LocalDateTime;

@Entity
public class EmailChangeToken extends AbstractEntity {

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "new_email", nullable = false)
    private String newEmail;

    @Column(name = "current_email", nullable = false)
    private String currentEmail;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    public String getToken() {
        return token;
    }

    public EmailChangeToken setToken(String token) {
        this.token = token;
        return this;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public EmailChangeToken setNewEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public EmailChangeToken setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
        return this;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public EmailChangeToken setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }
}
