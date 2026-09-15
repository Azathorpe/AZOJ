package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.userdto.UserDTO;
import org.example.azoi.model.User;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 通过id获取用户
     * @param id id
     * @return 用户
     */
    @Override
    public String getUserById(Long id){
        List<User> userById = userRepository.getUserById(id);
        if(userById.isEmpty())
            return JSON.toJSONString(new Result<User>(null, Result.FAIL, "User not found"));
        return JSON.toJSONString(new Result<>(userById.get(0), Result.SUCCESS, "ok"));
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
        return JSON.toJSONString(new Result<>(save, Result.SUCCESS, "success"));
    }
}
