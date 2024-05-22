package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

import at.ac.tuwien.sepr.groupphase.backend.persistence.entity.type.UserType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;

import java.util.List;
import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = jakarta.persistence.DiscriminatorType.STRING)
public class ApplicationUser extends AbstractEntity {

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "FAMILY_NAME")
    private String familyName;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "SALT")
    private String salt;

    @Column(name = "LOGIN_COUNT")
    private int loginCount;

    @Column(name = "ACCOUNT_LOCKED")
    private boolean accountLocked;

    @Column(name = "USER_TYPE")
    private UserType type = UserType.CUSTOMER;

    @Column(name = "SUPER_ADMIN", updatable = false)
    private boolean superAdmin;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "USER_NEWS", joinColumns = {@JoinColumn(name = "USER_ID")}, inverseJoinColumns = {@JoinColumn(name = "NEWS_ID")})
    private List<News> news;


    public ApplicationUser() {
    }

    public ApplicationUser(String email, String password, String firstName, String familyName, String phoneNumber,
                           String salt, int loginCount, boolean accountLocked, UserType type, boolean superAdmin) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.familyName = familyName;
        this.phoneNumber = phoneNumber;
        this.salt = salt;
        this.loginCount = loginCount;
        this.accountLocked = accountLocked;
        this.type = type;
        this.superAdmin = superAdmin;
    }

    public String getEmail() {
        return email;
    }

    public ApplicationUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ApplicationUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ApplicationUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getFamilyName() {
        return familyName;
    }

    public ApplicationUser setFamilyName(String familyName) {
        this.familyName = familyName;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ApplicationUser setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getSalt() {
        return salt;
    }

    public ApplicationUser setSalt(String salt) {
        this.salt = salt;
        return this;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public ApplicationUser setLoginCount(int loginCount) {
        this.loginCount = loginCount;
        return this;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public ApplicationUser setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
        return this;
    }

    public UserType getType() {
        return type;
    }

    public ApplicationUser setType(UserType type) {
        this.type = type;
        return this;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public ApplicationUser setOrders(List<Order> orders) {
        this.orders = orders;
        return this;
    }

    public List<News> getNews() {
        return news;
    }

    public ApplicationUser setNews(List<News> news) {
        this.news = news;
        return this;
    }

    public boolean isAdmin() {
        return type.equals(UserType.ADMIN);
    }

    public boolean isSuperAdmin() {
        return superAdmin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ApplicationUser that = (ApplicationUser) o;
        return loginCount == that.loginCount && accountLocked == that.accountLocked && Objects.equals(email, that.email) && Objects.equals(password, that.password) && Objects.equals(firstName, that.firstName)
            && Objects.equals(familyName, that.familyName) && Objects.equals(phoneNumber, that.phoneNumber) && Objects.equals(salt, that.salt) && Objects.equals(orders, that.orders) && Objects.equals(news, that.news);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password, firstName, familyName, phoneNumber, salt, loginCount, accountLocked, orders, news);
    }

    @Override
    public String toString() {
        return "ApplicationUserResponse{" + "email='" + email + '\'' + ", password='" + password + '\'' + ", firstName='" + firstName + '\'' + ", familyName='" + familyName + '\'' + ", phoneNumber='" + phoneNumber + '\'' + ", salt='" + salt
            + '\'' + ", loginCount=" + loginCount + ", accountLocked=" + accountLocked + '}';
    }
}
