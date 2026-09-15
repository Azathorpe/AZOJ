package org.example.azoi.model;

import org.jspecify.annotations.NullMarked;

@NullMarked

public class TeamMemberId {
    private Long teamId = 0L;

    private Long userId = 0L;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}