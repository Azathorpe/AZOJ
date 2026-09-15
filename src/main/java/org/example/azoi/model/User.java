package org.example.azoi.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@NullMarked
@Table(name = "users", schema = "azoi")
public class User {
    @Id
    private Long id = 0L;

    private String username = "";

    private String email = "";

    private String passwordHash = "";

    @Nullable
    private String nickname;

    @Nullable
    private String avatarUrl;

    private Byte role = 0;

    private Integer rating = 0;

    private Integer solvedCount = 0;

    private Integer submitCount = 0;

    private Boolean isBanned = false;

    @Nullable
    private String banReason;

    @Nullable
    private Instant lastLoginAt;

    @Nullable
    private String lastLoginIp;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @Nullable
    private Instant deletedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    @Nullable
    public String getNickname() {
        return nickname;
    }

    public void setNickname(@Nullable String nickname) {
        this.nickname = nickname;
    }

    @Nullable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(@Nullable String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Byte getRole() {
        return role;
    }

    public void setRole(Byte role) {
        this.role = role;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getSolvedCount() {
        return solvedCount;
    }

    public void setSolvedCount(Integer solvedCount) {
        this.solvedCount = solvedCount;
    }

    public Integer getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Integer submitCount) {
        this.submitCount = submitCount;
    }

    public Boolean getIsBanned() {
        return isBanned;
    }

    public void setIsBanned(Boolean isBanned) {
        this.isBanned = isBanned;
    }

    @Nullable
    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(@Nullable String banReason) {
        this.banReason = banReason;
    }

    @Nullable
    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(@Nullable Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    @Nullable
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(@Nullable String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nullable
    public Instant getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(@Nullable Instant deletedAt) {
        this.deletedAt = deletedAt;
    }

}