package org.example.azoi.service;

import org.example.azoi.model.Team;
import org.example.azoi.model.User;

public interface TeamService {
    String getTeam(Long teamId);

    String createTeam(Team team, Long ownerId);

    String modifyTeam(User user, Team team);

    String transferTeamOwnership(Long teamId, Long newOwnerId, Long currentOwnerId);

    String removeTeam(Long teamId, Long userId);
}
