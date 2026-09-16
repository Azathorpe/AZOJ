package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamUserIDDTO;
import org.example.azoi.dto.teamtransmit.TeamVO;
import org.example.azoi.model.Team;

public interface TeamService {
    Result<TeamVO> getTeam(Long teamId);

    Result<Team> createTeam(TeamDTO team);

    Result<Team> modifyTeam(TeamDTO teamDTO);

    Result<Team> transferTeamOwnership(Long teamId, Long newOwnerId, Long currentOwnerId);

    Result<Team> removeTeam(TeamUserIDDTO teamUserIDDTO);
}
