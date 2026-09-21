package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamVO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.dto.usertransmit.UserSimpleInfoVO;
import org.example.azoi.model.team_model.Role;
import org.example.azoi.model.team_model.Team;
import org.example.azoi.model.team_model.TeamMember;
import org.example.azoi.model.team_model.TeamMemberId;
import org.example.azoi.model.user_model.User;
import org.example.azoi.model.user_model.UserRole;
import org.example.azoi.service.TeamService;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.TeamMemberRepository;
import org.example.azoi.utils.repository.TeamRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.example.azoi.utils.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeamServiceImpl implements TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final TeamMemberRepository teamMemberRepository;

    public TeamServiceImpl(TeamRepository teamRepository, UserRepository userRepository, UserRoleRepository userRoleRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    //获取团队，不仅仅是团队的信息，还有创建者的详细信息和成员的简单信息
    @Override
    public Result<TeamVO> getTeam(Long teamId) {
        TeamVO result = new TeamVO();

        //获取团队信息
        Optional<Team> res = teamRepository.findById(teamId);
        if (res.isEmpty())
            return new Result<>(null, Result.FAIL, "Team not found");
        result.setTeamInfo(res.get());

        //找到创建者的信息
        Optional<User> creator = userRepository.getUserById(res.get().getOwnerId());
        if (creator.isEmpty())
            return new Result<>(null, Result.FAIL, "Creator not found, please report to admin.");
        result.setCreatorInfo(new UserInfoVO(creator.get()));

        //找到团队成员的信息
        List<TeamMember> teamMembersResult = teamMemberRepository.findTeamMembersById_TeamId(teamId);

        //FIXME: 这里可能会有性能问题，如果团队成员很多的话，建议改成批量查询
        teamMembersResult.forEach(teamMember -> {
            Optional<User> member = userRepository.getUserById(teamMember.getId().getUserId());
            member.ifPresent(user -> result.getMembers().add(new UserSimpleInfoVO(user)));
        });

        return new Result<>(result, Result.SUCCESS, "ok");
    }

    @Override
    @Transactional
    public Result<Team> createTeam(TeamDTO teamDTO, Long requesterId) {
        // 如果找不到这个团队，那么就用请求者作为团长
        if (teamDTO.getOwnerId() == null)
            teamDTO.setOwnerId(requesterId);

        //团长校验：必须存在才能创建
        userRepository.getUserById(teamDTO.getOwnerId()).orElseThrow(
                () -> new BusinessException("创建团队时，找不到团长的Id")
        );
        //你只能是一个团的人
        Optional<TeamMember> member = teamMemberRepository.findById_UserId(requesterId);
        if (member.isPresent())
            return new Result<>(null, Result.FAIL, "你已经加入了一个团队");

        Team team = new Team();
        team.setName(teamDTO.getName());
        team.setDescription(teamDTO.getDescription());
        team.setType(teamDTO.getType());
        team.setOwnerId(teamDTO.getOwnerId());

        Team savedTeam = teamRepository.save(team);

        //将创建者添加为团队成员
        teamMemberRepository.save(new TeamMember(new TeamMemberId(team.getId(), team.getOwnerId())));

        return new Result<>(savedTeam, Result.SUCCESS, "Team created successfully");
    }

    @Override
    @Transactional
    public Result<Team> modifyTeam(TeamDTO teamDTO, Long requesterId) {
        // 如果找不到这个团队，那么就用请求者作为团长
        log.info("{}", teamDTO.getOwnerId());
        if (teamDTO.getOwnerId() == null) {
            teamDTO.setOwnerId(requesterId);
        } else {
            //如果你带了参数传 那么你必须是管理员
            if (checkerAdmin(requesterId).getCode() == Result.FAIL)
                return new Result<>(null, Result.FAIL, "您想更改其他团队的话，您必须是管理员");
        }

        Long ownerId = teamDTO.getOwnerId();

        //逻辑： 通过ownerId找到team
        Optional<TeamMember> teamMemberByIdUserId = teamMemberRepository.getTeamMemberById_UserId(teamDTO.getOwnerId());
        if (teamMemberByIdUserId.isEmpty())
            return new Result<>(null, Result.FAIL, "找不到您的团队，请联系管理员");

        Result<Team> targetTeam = teamRepository.getTeamById(teamMemberByIdUserId.get().getId().getTeamId())
                .map(value -> new Result<>(value, Result.SUCCESS, "ok"))
                .orElseGet(() -> new Result<>(null, Result.FAIL, "can't find team"));

        if (targetTeam.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, targetTeam.getMsg());
        Team team = targetTeam.getObj();

        //查看这个所有者是否真的拥有这个Team
        Result<Team> res = isUserOwnerOfTeam(ownerId, team.getId());
        if (res.getCode() == Result.FAIL)
            return res;

        //是的话就更新她的team
        //更新团队信息
        //如果没有传信息的话 就不改
        Team updatedTeam = res.getObj();
        updatedTeam.setName(teamDTO.getName() == null ? team.getName() : teamDTO.getName());
        updatedTeam.setDescription(teamDTO.getDescription() == null ? team.getDescription() : teamDTO.getDescription());
        updatedTeam.setType(teamDTO.getType() == null ? team.getType() : teamDTO.getType());
        Team savedTeam = teamRepository.save(updatedTeam);

        return new Result<>(savedTeam, Result.SUCCESS, "Team updated successfully");
    }

    @Override
    @Transactional
    public Result<Team> transferTeamOwnership(Long newOwnerId, Long currentOwnerId) {
        //查找当前用户是否拥有team
        Team teamByOId = teamRepository.findByOwnerId(currentOwnerId)
                .orElseThrow(() -> new BusinessException("该用户未拥有一个团队"));

        Long teamId = teamByOId.getId();

        Result<Team> res = isUserOwnerOfTeam(currentOwnerId, teamId);
        if (res.getCode() == Result.FAIL)
            return res;
        Team team = res.getObj();

        //校验新所有者是否存在
        Optional<User> newUser = userRepository.findById(newOwnerId);
        if (newUser.isEmpty())
            return new Result<>(null, Result.FAIL, "未找到这个用户");

        //校验新所有者是否为团队成员
        Result<Void> isMember = isTeamMember(newOwnerId, teamId);
        if (isMember.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, "目标用户不是该团队成员");

        //转移所有权
        team.setOwnerId(newOwnerId);
        Team savedTeam = teamRepository.save(team);
        return new Result<>(savedTeam, Result.SUCCESS, "Ownership transferred successfully");
    }

    @Override
    @Transactional
    public Result<Team> removeTeam(Long teamId, Long userId) {
        Result<Team> res = isUserOwnerOfTeam(userId, teamId);
        if (res.getCode() == Result.FAIL)
            return res;
        Team team = res.getObj();

        //删除团队
        teamRepository.delete(team);

        //删掉所有成员的数据
        teamMemberRepository.removeAllById_TeamId(teamId);

        return new Result<>(null, Result.SUCCESS, "Team removed successfully");
    }

    @Override
    public Result<List<TeamVO>> getTeams() {
        List<TeamVO> res = new ArrayList<>();
        for (Team team : teamRepository.findAll()) {
            res.add(getTeam(team.getId()).getObj());
            log.info("{}", getTeam(team.getId()));
        }
        return new Result<>(res, Result.SUCCESS, "ok");
    }

    /**
     * 判断当前用户是否是团队的所有者
     *
     * @param userId userId
     * @param teamId teamId
     * @return Result<Team> 如果是团队的所有者，返回团队对象，否则返回失败信息
     */
    private Result<Team> isUserOwnerOfTeam(Long userId, Long teamId) {
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

    public Result<Void> isTeamMember(Long userId, Long teamId) {
        Optional<TeamMember> teamMember = teamMemberRepository.getTeamMemberById_UserId(userId);
        if (teamMember.isEmpty())
            return new Result<>(null, Result.FAIL, "can't find relationship, report to admin");

        if (teamMember.get().getId().getTeamId().equals(teamId))
            return new Result<>(null, Result.SUCCESS, "ok");
        return new Result<>(null, Result.FAIL, "team member not found");
    }

    /**
     * 检查一个角色是否是admin
     *
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Role> checkerAdmin(Long requesterId) {
        //只有管理员才能能添加新的角色
        Optional<UserRole> requester = userRoleRepository.findById_UserId((requesterId));
        if (requester.isEmpty())
            return new Result<>(null, Result.FAIL, "未找到您的信息: requester is empty");
        if (!requester.get().getRole().getName().equals(Role.ROLE_ADMIN)) {
            return new Result<>(null, Result.FAIL, "您不是管理员");
        }
        return new Result<>(null, Result.SUCCESS, "ok");
    }

}
