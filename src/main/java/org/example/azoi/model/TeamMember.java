package org.example.azoi.model;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@NullMarked
@Table(name = "team_members", schema = "azoi")
public class TeamMember {
    @Nullable
    @Id
    @Embedded.Nullable
    private TeamMemberId id;

    private Byte role = 0;

    private Instant joinedAt = Instant.now();

    public TeamMember(@Nullable TeamMemberId id, Byte role, Instant joinedAt) {
        this.id = id;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    @Nullable
    public TeamMemberId getId() {
        return id;
    }

    public void setId(@Nullable TeamMemberId id) {
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