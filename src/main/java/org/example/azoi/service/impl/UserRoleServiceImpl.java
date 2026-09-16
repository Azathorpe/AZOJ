package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.Role;
import org.example.azoi.model.User;
import org.example.azoi.model.UserRole;
import org.example.azoi.model.UserRoleId;
import org.example.azoi.service.UserRoleService;
import org.example.azoi.utils.repository.RoleRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.example.azoi.utils.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public String addUserRole(Long userId, Long roleId) {
        User user = userRepository.getReferenceById(userId);
        Role role = roleRepository.getReferenceById(roleId);
        UserRole ur = new UserRole();
        ur.setId(new UserRoleId(userId, roleId));
        ur.setUser(user);
        ur.setRole(role);
        userRoleRepository.save(ur);

        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "User role added successfully"));
    }

    @Override
    @Transactional
    public String removeUserRole(Long userId, Long roleId) {
        userRoleRepository.deleteById(new UserRoleId(userId, roleId));
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "User role removed successfully"));
    }

    @Override
    public String removeNeverUsedUserRole() {
        //todo: IMPLement logic to remove never used user roles
        return "";
    }

    @Override
    @Transactional
    public String getUserRoles(Long userId) {
        //通过获取Userid 然后查UserRole获取Roleid 然后查Role表获取Role信息
        //所以我们应该通过连接表来查询
        List<UserRole> result = userRoleRepository.getUserRoleById_UserId(userId);
        if(result.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "No roles found for user"));
        return JSON.toJSONString(new Result<>(result, Result.SUCCESS, "ok"));
    }

    @Override
    @Transactional
    public String getRoleUsers(Long roleId) {
        List<User> result = userRoleRepository.findUsersByRoleId(roleId);
        List<UserInfoVO> users = result.stream()
                .map(UserInfoVO::new)
                .toList();
        if(users.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "No users found for role"));
        return JSON.toJSONString(new Result<>(users, Result.SUCCESS, "ok"));
    }
}
