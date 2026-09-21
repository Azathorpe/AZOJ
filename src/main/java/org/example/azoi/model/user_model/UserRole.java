package org.example.azoi.model.user_model;

import jakarta.persistence.*;
import org.example.azoi.model.team_model.Role;

@Entity
@Table(name = "user_roles", schema = "azoi")
public class UserRole {
    @EmbeddedId
    private UserRoleId id;

    public UserRole() {
    }

    public UserRole(UserRoleId id) {
        this.id = id;
    }

    public UserRole(UserRoleId id, User user, Role role) {
        this.id = id;
        this.user = user;
        this.role = role;
    }

    public UserRoleId getId() {
        return id;
    }

    public void setId(UserRoleId id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}