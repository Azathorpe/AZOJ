package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamVO;
import org.example.azoi.model.team_model.Team;

import java.util.List;

public interface TeamService {
    Result<TeamVO> getTeam(Long teamId);

    Result<Team> createTeam(TeamDTO team, Long requesterId);

    Result<Team> modifyTeam(TeamDTO teamDTO, Long requesterId);

    Result<Team> transferTeamOwnership(Long newOwnerId, Long currentOwnerId);

    Result<Team> removeTeam(Long teamId, Long userId);

    Result<List<TeamVO>> getTeams();
}
