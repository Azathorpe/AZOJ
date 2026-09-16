package org.example.azoi.controller.user;

import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.service.impl.UserRoleServiceImpl;
import org.example.azoi.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用于控制单个user相关的请求
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserRoleServiceImpl userRoleService;

    @GetMapping("/getUser")
    public String getUser(@RequestParam Long id) {
        return userServiceImpl.getCurrentUser(id);
    }

    @GetMapping("/{userId}")
    public String getUserInfo(@PathVariable Long userId) {
        return userServiceImpl.getUserInfoById(userId);
    }

    @GetMapping("/getUserRole")
    public String getUserRole(@RequestParam Long userId) {
        return userRoleService.getUserRoles(userId);
    }

    @GetMapping("/getRoleUser")
    public String getRoleUser(@RequestParam Long roleId) {
        return userRoleService.getRoleUsers(roleId);
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserDTO user) {
        return userServiceImpl.registerUser(user);
    }

    @DeleteMapping("/delete")
    public String removeUser(@RequestParam Long userId) {
        return userServiceImpl.deleteUser(userId);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) {
        return userServiceImpl.loginUser(user);
    }
}
