package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.example.azoi.dto.Result;
import org.example.azoi.model.Team;
import org.example.azoi.model.User;
import org.example.azoi.service.TeamMemberService;
import org.example.azoi.service.TeamService;
import org.example.azoi.utils.repository.TeamRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.ref.Reference;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamMemberService teamMemberService;

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Override
    public String getTeam(Long teamId) {
        Optional<Team> res = teamRepository.findById(teamId);
        if (res.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "Team not found"));
        return JSON.toJSONString(new Result<>(res.get(), Result.SUCCESS, "ok"));
    }

    @Override
    public String createTeam(Team team, Long ownerId) {
        team.setOwnerId(ownerId);
        Team savedTeam = teamRepository.save(team);
        return JSON.toJSONString(new Result<>(savedTeam, Result.SUCCESS, "Team created successfully"));
    }

    @Override
    public String modifyTeam(User user, Team team) {
        Result<Team> res = isUserMemberOfTeam(user.getId(), team.getId());
        if (res.getCode() == Result.FAIL)
            return JSON.toJSONString(res);

        //更新团队信息
        Team updatedTeam = res.getObj();
        updatedTeam.setName(team.getName());
        updatedTeam.setDescription(team.getDescription());
        updatedTeam.setType(team.getType());
        Team savedTeam = teamRepository.save(updatedTeam);

        return JSON.toJSONString(new Result<>(savedTeam, Result.SUCCESS, "Team updated successfully"));
    }

    @Override
    public String transferTeamOwnership(Long teamId, Long newOwnerId, Long currentOwnerId) {
        Result<Team> res = isUserMemberOfTeam(currentOwnerId, teamId);
        if(res.getCode() == Result.FAIL)
            return JSON.toJSONString(res);
        Team team = res.getObj();

        //校验新所有者是否存在
        Optional<User> newUser = userRepository.findById(newOwnerId);
        if (newUser.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "New owner not found"));

        //校验新所有者是否为团队成员
        Result<Team> teamResult = JSON.parseObject(teamMemberService.getTeamMembers(teamId), new TypeReference<Result<Team>>() {});
        if(teamResult.getCode() == Result.FAIL)
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "New owner is not a member of the team"));

        //转移所有权
        team.setOwnerId(newOwnerId);
        Team savedTeam = teamRepository.save(team);
        return JSON.toJSONString(new Result<>(savedTeam, Result.SUCCESS, "Ownership transferred successfully"));
    }

    @Override
    public String removeTeam(Long teamId, Long userId) {
        Result<Team> res = isUserMemberOfTeam(userId, teamId);
        if(res.getCode() == Result.FAIL)
            return JSON.toJSONString(res);
        Team team = res.getObj();

        //删除团队
        teamRepository.delete(team);
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "Team removed successfully"));
    }

    /**
     * 判断当前用户是否是团队的创建者
     * @param userId userId
     * @param teamId teamId
     * @return Result<Team> 如果是团队的创建者，返回团队对象，否则返回失败信息
     */
    private Result<Team> isUserMemberOfTeam(Long userId, Long teamId) {
        //校验这个团队是否存在
        Optional<Team> existingTeam = teamRepository.findById(teamId);
        if (existingTeam.isEmpty())
            return new Result<>(null, Result.FAIL, "Team not found");
        Team team = existingTeam.get();

        //校验当前用户是否为团队的创建者
        if (!team.getOwnerId().equals(userId))
            return new Result<>(team, Result.FAIL, "You are not the owner");
        return new Result<>(team, Result.SUCCESS, "User is a member of the team");
    }

}
