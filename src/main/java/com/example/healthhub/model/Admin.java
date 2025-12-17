package com.example.healthhub.model;

import javax.persistence.*;
import javax.validation.constraints.*;

@Entity
@Table(name = "admin")
public class Admin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Name is required.")
    @Column(nullable = false)
    private String name;

    @NotEmpty(message = "Username is required.")
    @Column(unique = true, nullable = false)
    private String username;

    @NotEmpty(message = "Password is required.")
    @Column(nullable = false)
    private String password;

    @Email(message = "Invalid email format.")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Admin() {}

    public Admin(String name, String username, String password, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}