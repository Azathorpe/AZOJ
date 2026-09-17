package org.example.azoi.controller.team;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamUserIDDTO;
import org.example.azoi.dto.teamtransmit.TeamVO;
import org.example.azoi.model.Team;
import org.example.azoi.service.TeamService;
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

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Result<TeamVO>> getTeam(@PathVariable Long teamId) {
        Result<TeamVO> result = teamService.getTeam(teamId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @GetMapping("/getTeams")
    public ResponseEntity<Result<List<TeamVO>>> getTeams(){
        Result<List<TeamVO>> result = teamService.getTeams();
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    @PostMapping("/joinTeam")
    public ResponseEntity<String> joinTeam(@RequestBody TeamUserIDDTO teamUserIDDTO) {
        //todo: 使用序列化而不是String
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                .body("join team: (not impl)" + teamUserIDDTO.getTeamId());
    }

    @PostMapping("/create")
    public ResponseEntity<Result<Team>> createTeam(@RequestBody TeamDTO teamDTO) {
        Result<Team> result = teamService.createTeam(teamDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PostMapping("/modify")
    public ResponseEntity<Result<Team>> modifyTeam(@RequestBody TeamDTO teamDTO) {
        Result<Team> result = teamService.modifyTeam(teamDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    ///TODO：可以考虑是否是用软删除的方式
    @DeleteMapping("/remove")
    public ResponseEntity<Result<Team>> removeTeam(@RequestBody TeamUserIDDTO teamUserIDDTO) {
        Result<Team> result = teamService.removeTeam(teamUserIDDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }


}
