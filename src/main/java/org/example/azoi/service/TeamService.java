package org.example.azoi.service;

import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamUserIDDTO;
import org.example.azoi.model.Team;
import org.example.azoi.model.User;

public interface TeamService {
    String getTeam(Long teamId);

    String createTeam(TeamDTO team);

    String modifyTeam(TeamDTO teamDTO);

    String transferTeamOwnership(Long teamId, Long newOwnerId, Long currentOwnerId);

    String removeTeam(TeamUserIDDTO teamUserIDDTO);
}
