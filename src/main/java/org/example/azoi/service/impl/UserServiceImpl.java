package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserCurrentVO;
import org.example.azoi.dto.usertransmit.UserDTO;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.User;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.ref.Reference;
import java.util.List;

/**
 * 实际处理所有的用户请求(s, no s)
 */
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String getUserById(Long id){
        List<User> userById = userRepository.getUserById(id);
        if(userById.isEmpty())
            return JSON.toJSONString(new Result<User>(null, Result.FAIL, "User not found"));
        return JSON.toJSONString(new Result<>(userById.get(0), Result.SUCCESS, "ok"));
    }

    @Override
    public String getUserInfoById(Long id) {
        Result<User> userResult = JSON.parseObject(getUserById(id), new TypeReference<Result<User>>() {
        });
        if(userResult.getCode() == Result.SUCCESS)
            return JSON.toJSONString(new Result<>(new UserInfoVO(userResult.getObj()), Result.SUCCESS, "ok"));
        else
            return JSON.toJSONString(new Result<User>(null, Result.FAIL, "User not found"));
    }

    @Override
    public String getCurrentUser(Long id) {
        Result<User> userResult = JSON.parseObject(getUserById(id), new TypeReference<Result<User>>() {
        });
        if(userResult.getCode() == Result.SUCCESS)
            return JSON.toJSONString(new Result<>(new UserCurrentVO(userResult.getObj()), Result.SUCCESS, "ok"));
        else
            return JSON.toJSONString(new Result<User>(null, Result.FAIL, "User not found"));
    }

    @Override
    @Transactional
    public String deleteUser(Long id) {
        userRepository.deleteById(id);
        return JSON.toJSONString(new Result<>(null,  Result.SUCCESS, "User deleted"));
    }

    @Override
    @Transactional
    public String registerUser(UserDTO user){
        User saver = new User();
        saver.setUsername(user.getUsername());
        saver.setEmail(user.getEmail());
        saver.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        return registerUser(saver);
    }

    @Override
    @Transactional
    public String registerUser(User user){
        User save = userRepository.save(user);
        return JSON.toJSONString(new Result<>(new UserInfoVO(save), Result.SUCCESS, "success"));
    }

    @Override
    public String resetPassword(Long id) {
        return "";
    }


}
