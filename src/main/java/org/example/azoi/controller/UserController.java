package org.example.azoi.controller;

import org.example.azoi.dto.usertransmit.UserDTO;
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

    @GetMapping("/getUser")
    public String getUser(@RequestParam Long id) {
        return userServiceImpl.getCurrentUser(id);
    }

    @GetMapping("/{userId}")
    public String getUserInfo(@PathVariable Long userId) {
        return userServiceImpl.getUserInfoById(userId);
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserDTO user) {
        return userServiceImpl.registerUser(user);
    }

    @PostMapping("/delete")
    public String removeUser(@RequestParam Long userId) {
        return userServiceImpl.deleteUser(userId);
    }

    @PostMapping("/login")
    public String login(@RequestBody UserDTO user) {
        //todo: impl
        return "NOT IMPLEMENTED";
    }

    @PostMapping("/current")
    public String currentUser(@RequestBody UserDTO user) {
        return "NOT IMPLEMENTED";
    }
}
