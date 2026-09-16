package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.azoi.dto.Result;
import org.example.azoi.model.User;
import org.example.azoi.model.UserRole;
import org.example.azoi.model.UserRoleId;
import org.example.azoi.service.UserRoleService;
import org.example.azoi.utils.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Transactional
    public String addUserRole(Long userId, Long roleId) {
        UserRole userRole = new UserRole();
        userRole.setId(new UserRoleId(userId, roleId));
        userRoleRepository.save(userRole);
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "User role added successfully"));
    }

    @Override
    @Transactional
    public String removeUserRole(Long userId, Long roleId) {
        userRoleRepository.deleteById(new UserRoleId(userId, roleId));
        return JSON.toJSONString(new Result<>(null, Result.SUCCESS, "User role removed successfully"));
    }

    @Override
    @Transactional
    public String getUserRoles(Long userId) {
        List<UserRole> userRoleByIdUserId = userRoleRepository.getUserRoleById_UserId(userId);
        if(userRoleByIdUserId.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "No roles found for user"));
        return JSON.toJSONString(new Result<>(userRoleByIdUserId, Result.SUCCESS, "ok"));
    }

    @Override
    @Transactional
    public String getRoleUsers(Long roleId) {
        List<UserRole> roleUsers = userRoleRepository.getUserRolesById_RoleId(roleId);
        if(roleUsers.isEmpty())
            return JSON.toJSONString(new Result<>(null, Result.FAIL, "No users found for role"));
        return JSON.toJSONString(new Result<>(roleUsers, Result.SUCCESS, "ok"));
    }
}
