package org.example.azoi.controller.team;

import org.example.azoi.model.User;
import org.springframework.web.bind.annotation.*;

//用于控制team相关的请求
@RestController
@RequestMapping("/team")
public class TeamController {
    @GetMapping("/{teamId}")
    public String getTeam(@PathVariable String teamId) {
        return "get team: " + teamId;
    }

    @PostMapping("/create")
    public String createTeam() {
        return "create team";
    }

    @PostMapping("/modify")
    public String modifyTeam(@RequestBody String teamId) {
        return "modify team: " + teamId;
    }

    @DeleteMapping("/remove")
    public String removeTeam(@RequestBody String teamId, @RequestBody User user) {
        //TODO: 记得只能让创建者或者admin删除团队 做个校验
        return "remove team: " + teamId;
    }


}
