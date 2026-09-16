package org.example.azoi.dto.usertransmit;

import org.example.azoi.model.User;

import java.time.Instant;

public class UserSimpleInfoVO {
    private String username;
    private Integer rating;
    private Instant lastLoginAt;
    private Boolean isBanned;
    private Integer solvedCount;
    private Integer submitCount;
    private String avatarUrl;

    public UserSimpleInfoVO() {
    }

    public UserSimpleInfoVO(User user) {
        this.username = user.getUsername();
        this.rating = user.getRating();
        this.lastLoginAt = user.getLastLoginAt();
        this.isBanned = user.getIsBanned();
        this.solvedCount = user.getSolvedCount();
        this.submitCount = user.getSubmitCount();
        this.avatarUrl = user.getAvatarUrl();
    }

    public UserSimpleInfoVO(String username, Integer rating, Instant lastLoginAt, Boolean isBanned, Integer solvedCount, Integer submitCount, String avatarUrl) {
        this.username = username;
        this.rating = rating;
        this.lastLoginAt = lastLoginAt;
        this.isBanned = isBanned;
        this.solvedCount = solvedCount;
        this.submitCount = submitCount;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
