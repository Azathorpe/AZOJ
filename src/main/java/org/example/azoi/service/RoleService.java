package org.example.azoi.service;

import org.example.azoi.model.Role;
import org.example.azoi.model.User;

/**
 * 管理角色的存在和增删改查
 */
public interface RoleService {
    String getRoles();

    String addRole(Role role);

    String updateRole(Role role);

    String deleteRole(Long roleId);
}
