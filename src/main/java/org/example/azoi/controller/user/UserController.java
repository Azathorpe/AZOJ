package org.example.azoi.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.dto.usertransmit.UserLoginVO;
import org.example.azoi.model.user_model.Role;
import org.example.azoi.service.UserRoleService;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.JwtUtil;
import org.example.azoi.utils.anno.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用于控制单个user相关的请求
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 获取一个用户的详细信息<br/>
     * 请求地址: /user/me<br/>
     * 请求方法: /user/me<br/>
     *
     * @param requesterId 请求者的id
     * @return 该用户的详细信息{@link UserCurrentVO}
     */
    @GetMapping("/me")
    public ResponseEntity<Result<UserCurrentVO>> getUser(
            @CurrentUser Long requesterId) {
        Result<UserCurrentVO> result = userService.getCurrentUser(requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 获取一个用户的简单细细<br/>
     * 请求地址: /user/{userId}<br/>
     * 请求方法: /user/{userId}<br/>
     *
     * @param userId      查询用户的id
     * @param requesterId 请求者的id
     * @return 该用户的简单信息{@link UserInfoVO}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Result<UserInfoVO>> getUserInfo(
            @PathVariable Long userId,
            @CurrentUser Long requesterId) {
        Result<UserInfoVO> result = userService.getUserInfoById(userId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
    }

    /*
      获取一个用户的角色(一般是admin或者normal， 后面可以把它变成一个称号之类的来用)<br/>
      请求地址: /user/getUserRole<br/>
      请求方法: /user/getUserRole?userId=x<br/>

      @param userId      查询用户的id
     * @param requesterId 请求者的id
     * @return 角色{@link Role}
     */
//    @GetMapping("/getUserRole")
//    public ResponseEntity<Result<Role>> getUserRole(
//            @RequestParam Long userId,
//            @CurrentUser Long requesterId) {
//        Result<Role> result = userRoleService.getUserRoles(userId);
//        return result.getCode() == Result.SUCCESS
//                ? ResponseEntity.ok(result)
//                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
//    }

    /*
      获取带有某种角色的用户
      请求地址: /user/getRoleUser
      请求方法: /user/getRoleUser?roleId=x

      @param roleId      查询角色的id
     * @param requesterId 请求者的id
     * @return List<UserInfoVO> {@link List} of {@link UserInfoVO}
     */
//    @GetMapping("/getRoleUser")
//    public ResponseEntity<Result<List<UserInfoVO>>> getRoleUser(
//            @RequestParam Long roleId,
//            @CurrentUser Long requesterId) {
//        Result<List<UserInfoVO>> result = userRoleService.getRoleUsers(roleId);
//        return result.getCode() == Result.SUCCESS
//                ? ResponseEntity.ok(result)
//                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
//    }

    /**
     * 注册用户
     *
     * @param user 用户DTO{@link UserDTO}<br/>
     *             请求地址: /user/register<br/>
     *             请求方法: /user/register  -> json:<br/><br/>
     *             "username" : "Azazel",<br/>
     *             "password" : "123456",<br/>
     *             "email" : "123456@Az.com"<br/>
     * @return 用户注册后的简单信息{@link UserInfoVO}
     */
    @PostMapping("/register")
    public ResponseEntity<Result<UserInfoVO>> registerUser(
            @RequestBody UserDTO user) {
        Result<UserInfoVO> result = userService.registerUser(user);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 上传一个用户的头像
     * 请求地址: /user/avatar</br>
     * 请求方法: /user/avatar  -> json:</br></br>
     * @param avatar 头像文件
     * @param requesterId 请求者Id
     * @return 无
     */
    @PostMapping("/avatar")
    public ResponseEntity<Result<Void>> uploadAvatar(
            MultipartFile avatar,
            @CurrentUser Long requesterId){
        Result<Void> result = userService.uploadUserAvatar(avatar, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 用户登录</br>
     * 请求地址: /user/login</br>
     * 请求方法: /user/login  -> json:</br></br>
     * "username" : "Azazel",<br/>
     * "password" : "123456",<br/>
     * "email" : "123456@Az.com"<br/>
     *
     * @param user 用户DTO
     * @param httpServletRequest HttpRequest（用户获取ip）
     * @return 是否成功
     */
    @PostMapping("/login")
    public ResponseEntity<Result<UserLoginVO>> login(
            @RequestBody UserDTO user,
            HttpServletRequest httpServletRequest) {
        Result<UserLoginVO> result = userService.loginUser(user, httpServletRequest);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 删除一个用户<br/>
     * 请求地址: /user/delete<br/>
     * 请求方法: /user/delete?userId=x
     *
     * @param userId      查询用户的id
     * @param requesterId 请求者的id
     * @return String 我也不知道是啥，祈祷不会出错🙏
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Result<String>> removeUser(
            @RequestParam Long userId,
            @CurrentUser Long requesterId) {
        Result<String> result = userService.deleteUser(userId, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
