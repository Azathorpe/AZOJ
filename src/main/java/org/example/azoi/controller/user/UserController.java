package org.example.azoi.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.Role;
import org.example.azoi.model.UserRole;
import org.example.azoi.service.UserRoleService;
import org.example.azoi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用于控制单个user相关的请求
 */

//TODO: 添加更改用户的角色功能
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRoleService userRoleService;

    public UserController(UserService userService, UserRoleService userRoleService) {
        this.userService = userService;
        this.userRoleService = userRoleService;
    }

    //FIXME: 要不要把这里改成userId
    @GetMapping("/getUser")
    public ResponseEntity<Result<UserCurrentVO>> getUser(@RequestParam Long id) {
        Result<UserCurrentVO> result = userService.getCurrentUser(id);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Result<UserInfoVO>> getUserInfo(@PathVariable Long userId) {
        Result<UserInfoVO> result = userService.getUserInfoById(userId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    //Tips: 一个用户只能有一个角色（指admin或者啥的） 但是一种角色很多人都可能有
    //所以getUserRole返回Role 而 getRoleUser返回List<UserInfoVO>

    @GetMapping("/getUserRole")
    public ResponseEntity<Result<Role>> getUserRole(@RequestParam Long userId) {
        Result<Role> result = userRoleService.getUserRoles(userId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @GetMapping("/getRoleUser")
    public ResponseEntity<Result<List<UserInfoVO>>> getRoleUser(@RequestParam Long roleId) {
        Result<List<UserInfoVO>> result = userRoleService.getRoleUsers(roleId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PostMapping("/register")
    public ResponseEntity<Result<UserInfoVO>> registerUser(@RequestBody UserDTO user) {
        Result<UserInfoVO> result = userService.registerUser(user);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    ///TODO：可以考虑是否是用软删除的方式
    ///FIXME: 删除用户时应该校验一下
    @DeleteMapping("/delete")
    public ResponseEntity<Result<String>> removeUser(@RequestParam Long userId) {
        Result<String> result = userService.deleteUser(userId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Result<Boolean>> login(@RequestBody UserDTO user, HttpServletRequest httpServletRequest) {
        Result<Boolean> result = userService.loginUser(user, httpServletRequest);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
}
