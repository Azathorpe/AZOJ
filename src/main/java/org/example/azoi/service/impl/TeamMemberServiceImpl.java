package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.azoi.dto.Result;
import org.example.azoi.model.Team;
import org.example.azoi.model.TeamMember;
import org.example.azoi.model.TeamMemberId;
import org.example.azoi.service.TeamMemberService;
import org.example.azoi.utils.repository.TeamMemberRepository;
import org.example.azoi.utils.repository.TeamRepository;
import org.springframework.stereotype.Service;

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
    public String addTeamMember(Long teamId, Long userId) {
        teamMemberRepository.save(new TeamMember(new TeamMemberId(teamId, userId)));
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "Member added successfully"));
    }

    @Override
    public String removeTeamMember(Long teamId, Long userId) {
        //TODO: 增加校验模块
        teamMemberRepository.delete(new TeamMember(new TeamMemberId(teamId, userId)));
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "Member removed successfully"));
    }

    @Override
    public String getTeamMembers(Long teamId) {
        Optional<TeamMember> res = teamMemberRepository.getAllById_TeamId(teamId);
        if(res.isPresent())
            return JSON.toJSONString(new Result<>(res.get(), Result.SUCCESS, "ok"));
        return JSON.toJSONString(new Result<>(null, Result.FAIL, "not found this team"));
    }

    @Override
    public String getUserTeams(Long userId) {
        //找到teamid
        Optional<TeamMember> teamMembers = teamMemberRepository.getTeamMemberById_UserId(userId);
        if(teamMembers.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "User is not a member of any team"));
        Optional<Team> res = teamRepository.getTeamById(teamMembers.get().getId().getTeamId());
        if(res.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "Team not found"));
        return JSON.toJSONString(new Result<>(res.get(), Result.SUCCESS, "ok"));
    }
}
