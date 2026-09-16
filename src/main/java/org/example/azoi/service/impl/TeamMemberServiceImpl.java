package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.model.Team;
import org.example.azoi.model.TeamMember;
import org.example.azoi.model.TeamMemberId;
import org.example.azoi.service.TeamMemberService;
import org.example.azoi.utils.repository.TeamMemberRepository;
import org.example.azoi.utils.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public TeamMemberServiceImpl(TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public Result<String> addTeamMember(Long teamId, Long userId) {
        teamMemberRepository.save(new TeamMember(new TeamMemberId(teamId, userId)));
        return new Result<>(null, Result.SUCCESS, "Member added successfully");
    }

    @Override
    @Transactional
    public Result<String> removeTeamMember(Long teamId, Long userId) {
        //TODO: 增加校验模块
        teamMemberRepository.delete(new TeamMember(teamId, userId));
        return new Result<>(null, Result.SUCCESS, "Member removed successfully");
    }

    @Override
    public Result<List<TeamMember>> getTeamMembers(Long teamId) {
        List<TeamMember> res = teamMemberRepository.getAllById_TeamId(teamId);
        return new Result<>(res, Result.SUCCESS, "ok");
    }

    @Override
    public Result<Boolean> isTeamMember(Long userId, Long teamId) {
        Optional<TeamMember> teamMember = teamMemberRepository.getTeamMemberById_UserId(userId);
        if(teamMember.isEmpty())
            return new Result<>(Boolean.FALSE, Result.FAIL, "can't find relationship, report to admin");

        if (teamMember.get().getId().getTeamId().equals(teamId))
            return new Result<>(Boolean.TRUE, Result.SUCCESS, "ok");
        return new Result<>(Boolean.FALSE, Result.SUCCESS, "team member not found");
    }

    @Override
    public Result<Team> getUserTeam(Long userId) {
        //找到teamid
        Optional<TeamMember> teamMemberByIdUserId = teamMemberRepository.getTeamMemberById_UserId(userId);
        if(teamMemberByIdUserId.isEmpty())
            return new Result<>(null, Result.FAIL, "can't find connection, please report to admin.");

        Optional<Team> team = teamRepository.getTeamById(teamMemberByIdUserId.get().getId().getTeamId());

        return team.map(value -> new Result<>(value, Result.SUCCESS, "ok")).orElseGet(() -> new Result<>(null, Result.FAIL, "can't find team"));
    }
}
