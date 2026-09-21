package org.example.azoi.dto.usertransmit;

public class UserLoginVO {
    private String token;
    private UserInfoVO userInfo;

    public UserLoginVO() {
    }

    public UserLoginVO(String token, UserInfoVO userInfo) {
        this.token = token;
        this.userInfo = userInfo;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserInfoVO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfoVO userInfo) {
        this.userInfo = userInfo;
    }
}
