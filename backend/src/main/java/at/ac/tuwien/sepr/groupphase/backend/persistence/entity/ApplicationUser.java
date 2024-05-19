package at.ac.tuwien.sepr.groupphase.backend.persistence.entity;

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

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "USER_NEWS", joinColumns = {@JoinColumn(name = "USER_ID")}, inverseJoinColumns = {@JoinColumn(name = "NEWS_ID")})
    private List<News> news;


    public ApplicationUser() {
    }

    public ApplicationUser(String email, String password, String firstName, String familyName, String phoneNumber, String salt, int loginCount, boolean accountLocked) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.familyName = familyName;
        this.phoneNumber = phoneNumber;
        this.salt = salt;
        this.loginCount = loginCount;
        this.accountLocked = accountLocked;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }

    public boolean isAdmin() {
        return false;
    }

    public boolean isSuperAdmin() {
        return false;
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
