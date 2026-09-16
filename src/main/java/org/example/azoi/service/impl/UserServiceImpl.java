package org.example.azoi.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.User;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * 实际处理所有的用户请求(s, no s)
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    RoleServiceImpl roleService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Result<User> getUserById(Long id){
        Optional<User> userById = userRepository.getUserById(id);
        if(userById.isEmpty())
            return new Result<User>(null, Result.FAIL, "User not found");
        return new Result<>(userById.get(), Result.SUCCESS, "ok");
    }

    @Override
    public Result<UserInfoVO> getUserInfoById(Long id) {
        Result<User> userResult = getUserById(id);
        if(userResult.getCode() == Result.SUCCESS)
            return new Result<>(new UserInfoVO(userResult.getObj()), Result.SUCCESS, "ok");
        else
            return new Result<>(null, Result.FAIL, "User not found");
    }

    @Override
    public Result<UserCurrentVO> getCurrentUser(Long id) {
        Result<User> userResult = getUserById(id);
        if(userResult.getCode() == Result.SUCCESS)
            return new Result<>(new UserCurrentVO(userResult.getObj()), Result.SUCCESS, "ok");
        else
            return new Result<>(null, Result.FAIL, "User not found");
    }

    @Override
    @Transactional
    public Result<String> deleteUser(Long id) {
        userRepository.deleteById(id);
        return new Result<>(null,  Result.SUCCESS, "User deleted");
    }

    @Override
    public Result<Object> loginUser(UserDTO user, HttpServletRequest HR) {
        List<User> usersByUsername = userRepository.getUsersByUsername(user.getUsername());
        for(User u : usersByUsername){
            if(passwordEncoder.matches(user.getPassword(), u.getPasswordHash())) {
                u.setLastLoginAt(Instant.now());
                u.setLastLoginIp(getClientIp(HR));
                userRepository.save(u);
                return new Result<>(null, Result.SUCCESS, "User logged in");
            }
        }
        return  new Result<>(null, Result.FAIL, "User not found or password incorrect");
    }

    @Override
    @Transactional
    public Result<UserInfoVO> registerUser(UserDTO user){
        User saver = new User();
        saver.setUsername(user.getUsername());
        saver.setEmail(user.getEmail());
        saver.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        return registerUser(saver);
    }

    @Override
    @Transactional
    public Result<UserInfoVO> registerUser(User user){
        Result<Boolean> res = checkUsernameAndEmail(user);
        if(res.getCode() == Result.FAIL)
            return new Result<>(null, Result.FAIL, res.getMsg());

        User save = userRepository.save(user);
        return new Result<>(new UserInfoVO(save), Result.SUCCESS, "success");
    }

    @Override
    public Result<Boolean> resetPassword(Long id) {
        //todo: 重置密码
        return new Result<>(null, Result.FAIL, "not impl");
    }


    private Result<Boolean> checkUsernameAndEmail(User user){
        if (userRepository.existsUserByUsername(user.getUsername()))
            return new Result<>(Boolean.FALSE, Result.FAIL, "username exists");

        if(userRepository.existsUserByEmail(user.getEmail()))
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
}
