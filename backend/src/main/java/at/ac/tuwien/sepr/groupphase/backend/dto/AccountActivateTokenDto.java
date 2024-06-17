package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;

public class AccountActivateTokenDto implements AbstractDto {

    private Long id;
    private String token;
    private String email;
    private LocalDateTime expiryDate;

    public AccountActivateTokenDto(Long id, String token, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public AccountActivateTokenDto() {
    }

    public String getEmail() {
        return email;
    }

    public AccountActivateTokenDto setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }

    public AccountActivateTokenDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getToken() {
        return token;
    }

    public AccountActivateTokenDto setToken(String token) {
        this.token = token;
        return this;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public AccountActivateTokenDto setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }
}
