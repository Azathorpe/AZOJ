package org.example.azoi.dto.teamtransmit;

public class TeamUserIDDTO {
    Long teamId;
    Long userId;

    public TeamUserIDDTO() {
    }

    public TeamUserIDDTO(Long teamId, Long userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

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
