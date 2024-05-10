package at.ac.tuwien.sepr.groupphase.backend.dto;

public class ApplicationUserDto implements AbstractDto {

    private Long id;
    private String email;
    private String password;
    private String firstName;
    private String familyName;
    private String phoneNumber;
    private String salt;
    private int loginCount;
    private boolean accountLocked;
    private boolean  isAdmin;


    @Override
    public Long getId() {
        return id;
    }

    public ApplicationUserDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public ApplicationUserDto setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ApplicationUserDto setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ApplicationUserDto setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public ApplicationUserDto setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ApplicationUserDto setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public ApplicationUserDto setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public ApplicationUserDto setLoginCount(int loginCount) {
        this.loginCount = loginCount;
        return this;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public ApplicationUserDto setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
        return this;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public ApplicationUserDto setAdmin(boolean admin) {
        isAdmin = admin;
        return this;
    }

    public ApplicationUserDto(Long id, String email, String password, String firstName, String familyName,
        String phoneNumber, String salt, int loginCount, boolean accountLocked, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.familyName = familyName;
        this.phoneNumber = phoneNumber;
        this.salt = salt;
        this.loginCount = loginCount;
        this.accountLocked = accountLocked;
        this.isAdmin = isAdmin;
    }

    public ApplicationUserDto() {
    }
}
