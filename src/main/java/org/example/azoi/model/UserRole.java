package org.example.azoi.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@NullMarked
@Table(name = "user_roles", schema = "azoi")
public class UserRole {
    @Nullable
    @Id
    @Embedded.Nullable
    private UserRoleId id;

    public UserRole(@Nullable UserRoleId id) {
        this.id = id;
    }

    @Nullable
    public UserRoleId getId() {
        return id;
    }

    public void setId(@Nullable UserRoleId id) {
        this.id = id;
    }

}