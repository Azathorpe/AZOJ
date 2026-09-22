package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.model.user_model.Role;

import java.util.List;

/**
 * 管理角色的存在和增删改查
 */
@Deprecated
public interface RoleService {
    Result<List<Role>> getRoles();

    Result<Role> addRole(Role role, Long requesterId);

    Result<Role> updateRole(Role role, Long requesterId);

    Result<Role> deleteRole(Long roleId, Long requesterId);
}
