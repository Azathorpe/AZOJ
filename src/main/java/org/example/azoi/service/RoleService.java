package org.example.azoi.service;

import org.example.azoi.model.Role;

public interface RoleService {
    String getRoles();

    String addRole(Role role);

    String updateRole(Role role);

    String deleteRole(Long roleId);
}
