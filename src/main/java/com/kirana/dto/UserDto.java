package com.kirana.dto;

import java.util.Set;

public class UserDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String preferredLanguage;
    private Set<String> roles;

    public UserDto() {}

    public UserDto(Long id, String fullName, String email, String phone, String preferredLanguage, Set<String> roles) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.preferredLanguage = preferredLanguage;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
