package com.kamil.VoteCalculator.model.user;

import com.kamil.VoteCalculator.model.role.Roles;

import javax.persistence.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(nullable = false)
    String firstName;
    @Column(nullable = false)
    String secondName;
    @Column(nullable = false)
    String pesel;
    @Column(nullable = false)
    String password;
    @OneToOne(fetch = FetchType.EAGER)
    Roles roles;

    boolean blocked = false;

    boolean voidedVote = false;

    boolean disallowed = false;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Roles getRoles() {
        return roles;
    }

    public void setRoles(Roles roles) {
        this.roles = roles;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isVoidedVote() {
        return voidedVote;
    }

    public void setVoidedVote(boolean voidedVote) {
        this.voidedVote = voidedVote;
    }

    public boolean isDisallowed() {
        return disallowed;
    }

    public void setDisallowed(boolean disallowed) {
        this.disallowed = disallowed;
    }
}

