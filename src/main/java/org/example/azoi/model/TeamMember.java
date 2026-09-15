package org.example.azoi.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "team_members", schema = "azoi")
public class TeamMember {
    @EmbeddedId
    private TeamMemberId id;

    @Column(name = "role", nullable = false)
    private Byte role;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public TeamMemberId getId() {
        return id;
    }

    public void setId(TeamMemberId id) {
        this.id = id;
    }

    public Byte getRole() {
        return role;
    }

    public void setRole(Byte role) {
        this.role = role;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Instant joinedAt) {
        this.joinedAt = joinedAt;
    }

}