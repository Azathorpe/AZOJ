package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.model.team_model.Team;

public interface TeamMemberService {
    Result<String> addTeamMember(Long teamId, Long userId);

    Result<String> removeTeamMember(Long teamId, Long userId);

    Result<String> quitTeam(Long requesterId);

    Result<Team> getUserTeam(Long userId);
}
