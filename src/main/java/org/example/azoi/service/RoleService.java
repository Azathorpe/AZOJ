package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.model.Role;
import org.example.azoi.model.User;

import java.util.List;

/**
 * 管理角色的存在和增删改查
 */
public interface RoleService {
    Result<List<Role>> getRoles();

    Result<Role> addRole(Role role);

    Result<Role> updateRole(Role role);

    Result<String> deleteRole(Long roleId);
}
