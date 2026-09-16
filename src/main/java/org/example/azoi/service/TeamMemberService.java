package org.example.azoi.service;

public interface TeamMemberService {
    String addTeamMember(Long teamId, Long userId);

    String removeTeamMember(Long teamId, Long userId);

    String getTeamMembers(Long teamId);

    String getUserTeams(Long userId);
}
