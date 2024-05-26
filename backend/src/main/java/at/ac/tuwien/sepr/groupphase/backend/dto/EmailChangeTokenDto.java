package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;

public class EmailChangeTokenDto implements AbstractDto {

    private Long id;
    private String token;
    private String newEmail;
    private String currentEmail;
    private LocalDateTime expiryDate;

    public EmailChangeTokenDto(Long id, String token, String newEmail, String currentEmail, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.newEmail = newEmail;
        this.currentEmail = currentEmail;
        this.expiryDate = expiryDate;
    }

    public EmailChangeTokenDto() {
    }

    public String getToken() {
        return token;
    }

    public EmailChangeTokenDto setToken(String token) {
        this.token = token;
        return this;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public EmailChangeTokenDto setNewEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public EmailChangeTokenDto setCurrentEmail(String currentEmail) {
        this.currentEmail = currentEmail;
        return this;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public EmailChangeTokenDto setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }

    public EmailChangeTokenDto setId(Long id) {
        this.id = id;
        return this;
    }
}
