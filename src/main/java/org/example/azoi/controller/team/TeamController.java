package org.example.azoi.controller.team;

import com.alibaba.fastjson.JSON;
import org.example.azoi.dto.teamtransmit.TeamDTO;
import org.example.azoi.dto.teamtransmit.TeamUserIDDTO;
import org.example.azoi.model.User;
import org.example.azoi.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//用于控制team相关的请求
//FIXME: 这里需要校验和更改的地方挺多的
@RestController
@RequestMapping("/team")
public class TeamController {

    @Autowired
    TeamService teamService;

    @GetMapping("/{teamId}")
    public String getTeam(@PathVariable Long teamId) {
        return JSON.toJSONString(teamService.getTeam(teamId));
    }

    @PostMapping("/joinTeam")
    public String joinTeam(@RequestBody TeamUserIDDTO teamUserIDDTO) {
        return "join team: (not impl)" + teamUserIDDTO.getTeamId();
    }

    @PostMapping("/create")
    public String createTeam(@RequestBody TeamDTO teamDTO) {
        return JSON.toJSONString(teamService.createTeam(teamDTO));
    }

    @PostMapping("/modify")
    public String modifyTeam(@RequestBody TeamDTO teamDTO) {
        return JSON.toJSONString(teamService.modifyTeam(teamDTO));
    }

    @DeleteMapping("/remove")
    public String removeTeam(@RequestBody TeamUserIDDTO teamUserIDDTO) {
        return JSON.toJSONString(teamService.removeTeam(teamUserIDDTO));
    }


}
