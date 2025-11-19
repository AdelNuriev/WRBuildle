package ru.itis.wr.entities;

import ru.itis.wr.helper.GuessResult;

import java.time.LocalDateTime;

public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private Role role;
    private Integer coins;
    private Integer experience;
    private Integer level;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public User() {}

    public User(Long id, String username, String email, String passwordHash, String salt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = Role.USER;
        this.coins = 0;
        this.experience = 0;
        this.level = 1;
        this.createdAt = LocalDateTime.now();
    }

    public User(Long id,
                String username,
                String email,
                String passwordHash,
                String salt,
                Role role,
                Integer coins,
                Integer experience,
                Integer level,
                LocalDateTime createdAt,
                LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
        this.coins = coins;
        this.experience = experience;
        this.level = level;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Integer getCoins() { return coins; }
    public void setCoins(Integer coins) { this.coins = coins; }
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    public void earnCoins(GuessResult result) {
        coins += (int) (result.getScoreEarned() * 0.25);
    }
}
