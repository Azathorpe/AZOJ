package org.example.azoi.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.dto.usertransmit.UserLoginVO;
import org.example.azoi.model.team_model.Role;
import org.example.azoi.model.user_model.User;
import org.example.azoi.model.user_model.UserRole;
import org.example.azoi.model.user_model.UserRoleId;
import org.example.azoi.service.RoleService;
import org.example.azoi.service.UserRoleService;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.JwtUtil;
import org.example.azoi.utils.repository.UserRepository;
import org.example.azoi.utils.repository.UserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 实际处理所有的用户请求(s, no s)
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserRoleService userRoleService, UserRoleRepository userRoleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRoleRepository = userRoleRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Result<User> getUserById(Long id) {
        Optional<User> userById = userRepository.getUserById(id);
        if (userById.isEmpty())
            return new Result<>(null, Result.FAIL, "User not found");
        return new Result<>(userById.get(), Result.SUCCESS, "ok");
    }

    @Override
    @Transactional
    public Result<List<UserInfoVO>> registerUsers(List<UserDTO> user, Long requesterId) {

        List<UserInfoVO> res = new ArrayList<>();
        for (UserDTO ud : user)
            res.add(registerUser(ud).getObj());

        return new Result<>(res, Result.SUCCESS, "ok");
    }

    @Override
    public Result<UserInfoVO> getUserInfoById(Long id) {
        Result<User> userResult = getUserById(id);
        if (userResult.getCode() == Result.SUCCESS)
            return new Result<>(new UserInfoVO(userResult.getObj()), Result.SUCCESS, "ok");
        else
            return new Result<>(null, Result.FAIL, "User not found");
    }

    @Override
    public Result<UserCurrentVO> getCurrentUser(Long id) {
        Result<User> userResult = getUserById(id);
        if (userResult.getCode() == Result.SUCCESS)
            return new Result<>(new UserCurrentVO(userResult.getObj()), Result.SUCCESS, "ok");
        else
            return new Result<>(null, Result.FAIL, "User not found");
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long id, Long requesterId) {
        //只有本人和管理员可以删除用户
        if (!Objects.equals(id, requesterId)) {
            //如果不是本人 检查是不是管理员
            Result<Void> result = checkerAdmin(requesterId);
            if(result.getCode() == Result.FAIL)
                return new Result<>(null, Result.FAIL, result.getMsg());
        }

        userRepository.deleteById(id);
        //在删除用户的时候，也要把他和Role的关系删除掉
        userRoleRepository.deleteById_UserId(id);
        return new Result<>(null, Result.SUCCESS, "User deleted");
    }

    @Override
    @Transactional
    public Result<UserLoginVO> loginUser(UserDTO user, HttpServletRequest HR) {
        List<User> usersByUsername = userRepository.getUsersByUsername(user.getUsername());
        for (User u : usersByUsername) {
            if (passwordEncoder.matches(user.getPassword(), u.getPasswordHash())) {
                u.setLastLoginAt(Instant.now());
                u.setLastLoginIp(getClientIp(HR));
                User loginedUser = userRepository.save(u);
                //生成jwt
                String token = jwtUtil.generateToken(loginedUser.getId(), loginedUser.getUsername());
                return new Result<>(new UserLoginVO(token, new UserInfoVO(loginedUser)), Result.SUCCESS, "User logged in");
            }
        }
        return new Result<>(null, Result.FAIL, "User not found or password incorrect");
    }

    @Override
    @Transactional
    public Result<UserInfoVO> registerUser(UserDTO user) {
        User saver = new User();
        saver.setUsername(user.getUsername());
        saver.setEmail(user.getEmail());
        saver.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        return registerUser(saver);
    }

    @Override
    @Transactional
    public Result<UserInfoVO> registerUser(User user) {
        Result<Boolean> res = checkUsernameAndEmail(user);
        if (res.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, res.getMsg());

        User save = userRepository.save(user);

        //注册了User之后，也同样需要把Role注册一下，默认先注册成普通用户 也就是id为1的普通用户
        userRoleRepository.save(
                new UserRole(
                        new UserRoleId(
                                user.getId(),
                                Role.ROLE_NORMAL_id
                        )
                )
        );

        return new Result<>(new UserInfoVO(save), Result.SUCCESS, "success");
    }

    @Override
    @Transactional
    public Result<Boolean> resetPassword(Long id) {
        //todo: 重置密码
        return new Result<>(null, Result.FAIL, "not impl");
    }


    private Result<Boolean> checkUsernameAndEmail(User user) {
        if (userRepository.existsUserByUsername(user.getUsername()))
            return new Result<>(Boolean.FALSE, Result.FAIL, "username exists");

        if (userRepository.existsUserByEmail(user.getEmail()))
            return new Result<>(Boolean.FALSE, Result.FAIL, "email exists");
        return new Result<>(Boolean.TRUE, Result.SUCCESS, "ok");
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理时取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 检查一个角色是否是admin
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Void> checkerAdmin(Long requesterId) {
        //只有管理员才能能添加新的角色
        Optional<UserRole> requester = userRoleRepository.findById_UserId((requesterId));
        if (requester.isEmpty())
            return new Result<>(null, Result.FAIL, "未找到您的信息: requester is empty");
        if (!requester.get().getRole().getName().equals(Role.ROLE_ADMIN)) {
            return new Result<>(null, Result.FAIL, "您不是管理员");
        }
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
