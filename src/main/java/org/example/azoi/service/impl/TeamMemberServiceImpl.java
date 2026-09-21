package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.model.team_model.Team;
import org.example.azoi.model.team_model.TeamMember;
import org.example.azoi.model.team_model.TeamMemberId;
import org.example.azoi.model.user_model.User;
import org.example.azoi.service.TeamMemberService;
import org.example.azoi.utils.repository.TeamMemberRepository;
import org.example.azoi.utils.repository.TeamRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public TeamMemberServiceImpl(UserRepository userRepository, TeamMemberRepository teamMemberRepository, TeamRepository teamRepository) {
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
    }

    @Override
    @Transactional
    public Result<String> addTeamMember(Long teamId, Long userId) {
        //我们要判断这个user是否已经加入过团队了
        if (getUserTeam(userId).getCode() == Result.SUCCESS)
            return new Result<>(null, Result.FAIL, "You are already join a team");

        //fixme:判断用户id是否存在
        Optional<User> u = userRepository.findById(userId);
        if(u.isEmpty())
            return new Result<>(null, Result.FAIL, "Can't find this user");

        teamMemberRepository.save(new TeamMember(new TeamMemberId(teamId, userId)));
        return new Result<>(null, Result.SUCCESS, "Member added successfully");
    }

    @Override
    @Transactional
    public Result<String> removeTeamMember(Long teamId, Long userId) {
        //我们要判断这个user是否在这个团队
        Result<Team> team = getUserTeam(userId);
        if (team.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, "You are not join any team");

        if(!Objects.equals(team.getObj().getId(), teamId))
            return new Result<>(null, Result.FAIL, "You are not belong this team");

        teamMemberRepository.delete(new TeamMember(teamId, userId));
        return new Result<>(null, Result.SUCCESS, "Member removed successfully");
    }

    @Override
    public Result<String> removeAllTeamMember(Long teamId) {
        teamMemberRepository.removeTeamMembersById_TeamId(teamId);
        return new Result<>(null, Result.SUCCESS, "ok");
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
            return new Result<>(null, Result.FAIL, "can't find your team, please report to admin.");

        Optional<Team> team = teamRepository.getTeamById(teamMemberByIdUserId.get().getId().getTeamId());

        return team.map(value -> new Result<>(value, Result.SUCCESS, "ok")).orElseGet(() -> new Result<>(null, Result.FAIL, "can't find team"));
    }
}
