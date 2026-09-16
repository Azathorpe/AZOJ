package org.example.azoi.controller.user;

import com.alibaba.fastjson.JSON;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.service.UserRoleService;
import org.example.azoi.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 用于控制单个user相关的请求
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    public UserController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    @GetMapping("/getUser")
    public String getUser(@RequestParam Long id) {
        return JSON.toJSONString(userService.getCurrentUser(id));
    }

    @GetMapping("/{userId}")
    public String getUserInfo(@PathVariable Long userId) {
        return JSON.toJSONString(userService.getUserInfoById(userId));
    }

    @GetMapping("/getUserRole")
    public String getUserRole(@RequestParam Long userId) {
        return JSON.toJSONString(userRoleService.getUserRoles(userId));
    }

    @GetMapping("/getRoleUser")
    public String getRoleUser(@RequestParam Long roleId) {
        return JSON.toJSONString(userRoleService.getRoleUsers(roleId));
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserDTO user) {
        return JSON.toJSONString(userService.registerUser(user));
    }

    @DeleteMapping("/delete")
    public String removeUser(@RequestParam Long userId) {
        return JSON.toJSONString(userService.deleteUser(userId));
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) {
        return JSON.toJSONString(userService.loginUser(user));
    }
}
