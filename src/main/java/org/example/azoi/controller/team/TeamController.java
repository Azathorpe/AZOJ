package org.example.azoi.controller.team;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamVO;
import org.example.azoi.model.team_model.Team;
import org.example.azoi.service.TeamMemberService;
import org.example.azoi.service.TeamService;
import org.example.azoi.utils.anno.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//用于控制team相关的请求
//FIXME: 这里需要校验和更改的地方挺多的
@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;
    private final TeamMemberService teamMemberService;

    public TeamController(TeamService teamService, TeamMemberService teamMemberService) {
        this.teamService = teamService;
        this.teamMemberService = teamMemberService;
    }

    /**
     * 获取一个Team的信息<br/>
     * 请求地址: /user/{teamId}<br/>
     * 请求方法: /user/{teamId}<br/>
     *
     * @param teamId 团队的id
     * @return Team的信息{@link TeamVO}
     */
    @GetMapping("/{teamId}")
    public ResponseEntity<Result<TeamVO>> getTeam(@PathVariable Long teamId) {
        Result<TeamVO> result = teamService.getTeam(teamId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * TODO: 后面把这个改成Query查询
     * 获取所有的Team<br/>
     * 请求地址: /user/getTeams<br/>
     * 请求方法: /user/getTeams<br/>
     *
     * @return 所有的Team {@link List} of {@link TeamVO}
     */
    @GetMapping("/getTeams")
    public ResponseEntity<Result<List<TeamVO>>> getTeams() {
        Result<List<TeamVO>> result = teamService.getTeams();
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /**
     * 加入一个Team<br/>
     * 请求地址: /user/joinTeam<br/>
     * 请求方法: /user/joinTeam?teamId=x<br/>
     *
     * @param teamId 团队Id
     * @param userId 用户Id
     * @return 我也不知道是啥，祈祷不会出错🙏
     */
    @PostMapping("/joinTeam")
    public ResponseEntity<Result<String>> joinTeam(
            @RequestParam Long teamId,
            @CurrentUser Long userId) {
        Result<String> result = teamMemberService.addTeamMember(teamId, userId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 退出一个Team<br/>
     * 请求地址: /user/quit<br/>
     * 请求方法: /user/quit?teamId=x<br/>
     *
     * @param teamId 团队Id
     * @param userId 用户Id
     * @return 我也不知道是啥，祈祷不会出错🙏
     */
    @PostMapping("/quit")
    public ResponseEntity<Result<String>> quitTeam(
            @RequestParam Long teamId,
            @CurrentUser Long userId) {
        Result<String> result = teamMemberService.removeTeamMember(teamId, userId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 创建一个Team<br/>
     * 请求地址: /user/create<br/>
     * 请求方法: /user/create<br/>
     *
     * @param teamDTO     teamDTO{@link TeamDTO}
     * @param requesterId 请求者id
     * @return Team{@link Team}
     */
    @PostMapping("/create")
    public ResponseEntity<Result<Team>> createTeam(
            @RequestBody TeamDTO teamDTO,
            @CurrentUser Long requesterId) {
        Result<Team> result = teamService.createTeam(teamDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 修改一个Team<br/>
     * 请求地址: /user/modify<br/>
     * 请求方法: /user/modify<br/>
     *
     * @param teamDTO     {@link TeamDTO}
     * @param requesterId 请求者id
     * @return Team{@link Team}
     */
    @PostMapping("/modify")
    public ResponseEntity<Result<Team>> modifyTeam(
            @RequestBody TeamDTO teamDTO,
            @CurrentUser Long requesterId) {
        Result<Team> result = teamService.modifyTeam(teamDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /// TODO：可以考虑是否是用软删除的方式
    /**
     * 删除一个Team<br/>
     * 请求地址: /user/modify<br/>
     * 请求方法: /user/modify<br/>
     *
     * @param teamId      团队id
     * @param requesterId 请求者id
     * @return Team{@link Team}
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Result<Team>> removeTeam(
            @RequestParam Long teamId,
            @CurrentUser Long requesterId) {
        Result<Team> result = teamService.removeTeam(teamId, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }


}
