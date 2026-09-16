package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamUserIDDTO;
import org.example.azoi.dto.teamtransmit.TeamVO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.dto.usertransmit.UserSimpleInfoVO;
import org.example.azoi.model.Team;
import org.example.azoi.model.TeamMember;
import org.example.azoi.model.User;
import org.example.azoi.service.TeamMemberService;
import org.example.azoi.service.TeamService;
import org.example.azoi.utils.repository.TeamRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//todo: 添加管理员增删查该
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

    //获取团队，不仅仅是团队的信息，还有创建者的详细信息和成员的简单信息
    @Override
    public String getTeam(Long teamId) {
        TeamVO result = new TeamVO();

        //获取团队信息
        Optional<Team> res = teamRepository.findById(teamId);
        if (res.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "Team not found"));
        result.setTeamInfo(res.get());

        //找到创建者的信息
        List<User> creator = userRepository.getUserById(res.get().getOwnerId());
        if (creator.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "Creator not found, please report to admin."));
        result.setCreatorInfo(new UserInfoVO(creator.get(0)));

        //找到团队成员的信息
        Result<List<TeamMember>> teamMembersResult = JSON.parseObject(teamMemberService.getTeamMembers(teamId), new TypeReference<>() {});
        if (teamMembersResult.getCode() == Result.FAIL)
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "Failed to get team members"));
        //FIXME: 这里可能会有性能问题，如果团队成员很多的话，建议改成批量查询
        teamMembersResult.getObj().forEach(teamMember -> {
            List<User> member = userRepository.getUserById(teamMember.getId().getUserId());
            if (!member.isEmpty())
                result.getMembers().add(new UserSimpleInfoVO(member.get(0)));
        });

        return JSON.toJSONString(new Result<>(result, Result.SUCCESS, "ok"));
    }

    @Override
    public String createTeam(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setType(teamDTO.getType());
        team.setOwnerId(teamDTO.getOwnerId());

        Team savedTeam = teamRepository.save(team);
        return JSON.toJSONString(new Result<>(savedTeam, Result.SUCCESS, "Team created successfully"));
    }

    @Override
    public String modifyTeam(TeamDTO teamDTO) {
        Long ownerId = teamDTO.getOwnerId();

        Result<Team> targetTeam = JSON.parseObject(teamMemberService.getUserTeams(ownerId), new TypeReference<Result<Team>>() {
        });
        Team team = targetTeam.getObj();

        Result<Team> res = isUserCreatorOfTeam(ownerId, team.getId());
        if (res.getCode() == Result.FAIL)
            return JSON.toJSONString(res);

        //更新团队信息
        Team updatedTeam = res.getObj();
        updatedTeam.setName(teamDTO.getName());
        updatedTeam.setDescription(teamDTO.getDescription());
        updatedTeam.setType(teamDTO.getType());
        Team savedTeam = teamRepository.save(updatedTeam);

        return JSON.toJSONString(new Result<>(savedTeam, Result.SUCCESS, "Team updated successfully"));
    }

    @Override
    public String transferTeamOwnership(Long teamId, Long newOwnerId, Long currentOwnerId) {
        Result<Team> res = isUserCreatorOfTeam(currentOwnerId, teamId);
        if (res.getCode() == Result.FAIL)
            return JSON.toJSONString(res);
        Team team = res.getObj();

        //校验新所有者是否存在
        Optional<User> newUser = userRepository.findById(newOwnerId);
        if (newUser.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "New owner not found"));

        //校验新所有者是否为团队成员
        Result<Team> teamResult = JSON.parseObject(teamMemberService.getTeamMembers(teamId), new TypeReference<Result<Team>>() {
        });
        if (teamResult.getCode() == Result.FAIL)
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "New owner is not a member of the team"));

        //转移所有权
        team.setOwnerId(newOwnerId);
        Team savedTeam = teamRepository.save(team);
        return JSON.toJSONString(new Result<>(savedTeam, Result.SUCCESS, "Ownership transferred successfully"));
    }

    @Override
    public String removeTeam(TeamUserIDDTO teamUserIDDTO) {
        Long userId = teamUserIDDTO.getUserId();
        Long teamId = teamUserIDDTO.getTeamId();
        Result<Team> res = isUserCreatorOfTeam(userId, teamId);
        if (res.getCode() == Result.FAIL)
            return JSON.toJSONString(res);
        Team team = res.getObj();

        //删除团队
        teamRepository.delete(team);
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "Team removed successfully"));
    }

    /**
     * 判断当前用户是否是团队的创建者
     *
     * @param userId userId
     * @param teamId teamId
     * @return Result<Team> 如果是团队的创建者，返回团队对象，否则返回失败信息
     */
    private Result<Team> isUserCreatorOfTeam(Long userId, Long teamId) {
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
