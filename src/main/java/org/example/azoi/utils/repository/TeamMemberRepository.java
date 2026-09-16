package org.example.azoi.utils.repository;

import org.example.azoi.model.TeamMember;
import org.example.azoi.model.TeamMemberId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamMemberRepository extends CrudRepository<TeamMember, TeamMemberId> {
    List<TeamMember> getAllById_TeamId(Long idTeamId);

    List<TeamMember> getTeamMemberById_UserId(Long idUserId);
}
