package org.example.azoi.utils.repository;

import org.example.azoi.model.team_model.TeamMember;
import org.example.azoi.model.team_model.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {
    List<TeamMember> getAllById_TeamId(Long idTeamId);

    Optional<TeamMember> getTeamMemberById_UserId(Long idUserId);

    void removeTeamMembersById_TeamId(Long idTeamId);

    Optional<TeamMember> findById_UserId(Long idUserId);

    List<TeamMember> findTeamMembersById_TeamId(Long idTeamId);

    void removeAllById_TeamId(Long idTeamId);

    void deleteById_UserId(Long idUserId);
}
