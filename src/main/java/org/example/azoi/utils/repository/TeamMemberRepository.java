package org.example.azoi.utils.repository;

import org.example.azoi.model.TeamMember;
import org.example.azoi.model.TeamMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {
    List<TeamMember> getAllById_TeamId(Long idTeamId);

    Optional<TeamMember> getTeamMemberById_UserId(Long idUserId);

    void removeTeamMembersById_TeamId(Long idTeamId);
}
