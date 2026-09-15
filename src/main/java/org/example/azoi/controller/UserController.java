package org.example.azoi.controller;

import org.example.azoi.dto.userdto.UserDTO;
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
        return userServiceImpl.getUserById(id);
    }

    @GetMapping("/{userId}")
    public String getUserInfo(@PathVariable Long userId) {
        return userServiceImpl.getUserById(userId);
    }

    @PostMapping("/register")
    public String registerUser(@RequestBody UserDTO user) {
        return userServiceImpl.registerUser(user);
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
