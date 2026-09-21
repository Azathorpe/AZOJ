package org.example.azoi.controller.user;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.anno.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用于控制多个users相关的请求
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    //批量注册用户
    public ResponseEntity<Result<List<UserInfoVO>>> register(
            @RequestBody List<UserDTO> users,
            @CurrentUser Long requesterId) {
        Result<List<UserInfoVO>> result = userService.registerUsers(users, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
