package org.example.azoi.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles", schema = "azoi")
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    //TODO [逆向工程] 从数据库生成列
}