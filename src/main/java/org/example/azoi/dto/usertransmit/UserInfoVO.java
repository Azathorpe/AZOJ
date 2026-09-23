package org.example.azoi.dto.usertransmit;

import org.example.azoi.model.user_model.User;

import java.time.Instant;

public class UserInfoVO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
    private Byte role;
    private Integer rating;
    private Integer solvedCount;
    private Integer submitCount;
    private Boolean isBanned;
    private String banReason;
    private Instant lastLoginAt;
    private String lastLoginIp;
    private Instant createdAt;

    public UserInfoVO() {
    }

    public UserInfoVO(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.avatarUrl = user.getAvatarUrl();
        this.role = user.getRole();
        this.rating = user.getRating();
        this.solvedCount = user.getSolvedCount();
        this.submitCount = user.getSubmitCount();
        this.isBanned = user.getIsBanned();
        this.banReason = user.getBanReason();
        this.lastLoginAt = user.getLastLoginAt();
        this.lastLoginIp = user.getLastLoginIp();
        this.createdAt = user.getCreatedAt();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
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

    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
