package org.example.azoi.model;

import org.jspecify.annotations.NullMarked;

@NullMarked

public class UserRoleId {
    private Long userId = 0L;

    private Long roleId = 0L;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

}