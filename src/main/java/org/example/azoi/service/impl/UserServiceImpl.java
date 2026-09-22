package org.example.azoi.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.dto.usertransmit.UserLoginVO;
import org.example.azoi.model.user_model.Role;
import org.example.azoi.model.user_model.User;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.JwtUtil;
import org.example.azoi.utils.exception.BusinessException;
import org.example.azoi.utils.repository.RoleRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    @Value("${azoi.storage.root}")
    private String rootPath;
    @Value("${azoi.storage.avatar-dir}")
    private String avatarDir;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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
    public Result<Void> setUserRole(Long requesterId, Long targetUserId, Byte role) {
        Result<Void> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return result;

        roleRepository.findById(role.longValue()).orElseThrow(
                () -> new BusinessException("未找到对应的角色")
        );

        userRepository.findById(targetUserId).ifPresent(user -> user.setRole(role));
        return new Result<>(null, Result.SUCCESS, "ok");
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
            return new Result<>(null, Result.FAIL, "找不到该用户");
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long id, Long requesterId) {
        //只有本人和管理员可以删除用户
        if (!Objects.equals(id, requesterId)) {
            //如果不是本人 检查是不是管理员
            Result<Void> result = checkerAdmin(requesterId);
            if (result.getCode() == Result.FAIL)
                return new Result<>(null, Result.FAIL, result.getMsg());
        }

        userRepository.deleteById(id);
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
        return new Result<>(null, Result.FAIL, "用户名错误或者密码错误");
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

        Optional<Role> role = roleRepository.findById(Role.ROLE_NORMAL_id);
        if (role.isEmpty())
            throw new BusinessException("角色为空，请务必联系管理员");

        return new Result<>(new UserInfoVO(save), Result.SUCCESS, "success");
    }

    @Override
    public Result<Void> uploadUserAvatar(MultipartFile file, Long requesterId) {
        //校验file是否存在
        if (file == null || file.isEmpty())
            throw new BusinessException("头像文件不能为空");

        Path avatarPath = Paths.get(rootPath, avatarDir);
        //检查头像文件夹是否存在
        try {
            Files.createDirectories(avatarPath);
        } catch (IOException e) {
            throw new BusinessException("头像文件夹创建失败，请联系管理员");
        }

        avatarPath = avatarPath.resolve(requesterId + ".jpg");
        try {
            file.transferTo(avatarPath);
        } catch (IOException e) {
            throw new BusinessException("头像文件写入失败，请联系管理员");
        }

        //更新数据库
        User user = userRepository.findById(requesterId).orElseThrow(
                () -> new BusinessException("为找到用户信息，请联系管理员")
        );
        user.setAvatarUrl(Paths.get(avatarDir, requesterId + ".jpg").toString());
        log.info("User: {} , new avatar path: {}", user.getUsername(), avatarPath);
        userRepository.save(user);

        return new Result<>(null, Result.SUCCESS, "success");
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
     *
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Void> checkerAdmin(Long requesterId) {
        //只有管理员才能能添加新的角色
        User user = userRepository.findById(requesterId)
                .orElseThrow(() -> new BusinessException("未找到您的信息: requesterId not found: " + requesterId));

        Role role = roleRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException("未找到该角色: roleId not found:"));

        if (!role.getName().equals(Role.ROLE_ADMIN))
            return new Result<>(null, Result.FAIL, "您不是管理员");
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
