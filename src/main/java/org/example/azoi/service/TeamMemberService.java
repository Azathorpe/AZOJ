package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamUserIDDTO;
import org.example.azoi.model.Team;
import org.example.azoi.model.TeamMember;

import java.util.List;

public interface TeamMemberService {
    Result<String> addTeamMember(TeamUserIDDTO teamUserIDDTO);

    Result<String> removeTeamMember(TeamUserIDDTO teamUserIDDTO);

    Result<String> removeAllTeamMember(Long teamId);

    Result<List<TeamMember>> getTeamMembers(Long teamId);

    Result<Boolean> isTeamMember(Long userId, Long teamId);

    Result<Team> getUserTeam(Long userId);
}
