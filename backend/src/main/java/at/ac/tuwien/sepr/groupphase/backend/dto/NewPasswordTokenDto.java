package at.ac.tuwien.sepr.groupphase.backend.dto;

import java.time.LocalDateTime;

public class NewPasswordTokenDto implements AbstractDto {

    private Long id;
    private String token;
    private String email;
    private LocalDateTime expiryDate;

    public NewPasswordTokenDto(Long id, String token, String email, LocalDateTime expiryDate) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.expiryDate = expiryDate;
    }

    public NewPasswordTokenDto() {
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public NewPasswordTokenDto setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public NewPasswordTokenDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getToken() {
        return token;
    }

    public NewPasswordTokenDto setToken(String token) {
        this.token = token;
        return this;
    }

    @Override
    public Long getId() {
        return id;
    }

    public NewPasswordTokenDto setId(Long id) {
        this.id = id;
        return this;
    }
}
