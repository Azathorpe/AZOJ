package org.example.azoi.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "team_members", schema = "azoi")
public class TeamMember {
    @EmbeddedId
    private TeamMemberId id;

    @Column(name = "role", nullable = false)
    private Byte role = 1;

    @Column(name = "joined_at", nullable = false)
    private Instant joinedAt;

    public TeamMember() {
    }

    public TeamMember(TeamMemberId id) {
        this.id = id;
    }

    public TeamMember(Long teamId, Long userId){
        this.id = new TeamMemberId(teamId, userId);
    }

    public TeamMember(TeamMemberId id, Byte role, Instant joinedAt) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    @PrePersist
    public void onCreated() {
        if (joinedAt == null)
            joinedAt = Instant.now();
    }

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