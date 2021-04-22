package com.finacial.model;

import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "Verification_token")
@NamedQueries({
        @NamedQuery(name = "Verification_token.updateStatus", query = "UPDATE VerificationToken v SET v.status =:statusId WHERE v.id=:vftId")
})
public class VerificationToken {
    @Value("${EXPIRATION}")
    private int expiration;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;
    @OneToOne
    private Account user;
    private Date expiryDate;

    private Long status;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public VerificationToken(String token, @NotBlank Account user) {
        this.token = token;
        this.user = user;
    }

    public VerificationToken() {
    }

    public VerificationToken(String token, Account user, Date expiryDate, Long status) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}
