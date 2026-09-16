package org.example.azoi.controller.team;

import org.example.azoi.model.Team;
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
        return teamService.getTeam(teamId);
    }

    @PostMapping("/joinTeam")
    public String joinTeam(@RequestBody Long teamId, @RequestBody User user) {
        return "join team: " + teamId;
    }

    @PostMapping("/create")
    public String createTeam(@RequestBody Team team, @RequestBody Long ownerId) {
        return teamService.createTeam(team, ownerId);
    }

    @PostMapping("/modify")
    public String modifyTeam(@RequestBody Team team, @RequestBody User user) {
        return teamService.modifyTeam(user, team);
    }

    @DeleteMapping("/remove")
    public String removeTeam(@RequestBody Long teamId, @RequestBody User user) {
        return teamService.removeTeam(teamId, user.getId());
    }


}
