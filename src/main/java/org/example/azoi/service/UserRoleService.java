package org.example.azoi.service;

import org.example.azoi.model.User;

/**
 * 管理用户和角色的关系
 */
public interface UserRoleService {
    /**
     * 为用户添加角色,注意 需要在注册用户时调用这个方法 {@link UserService#registerUser(User)}
     * @param userId 
     * @param roleId
     * @return 注册好角色的用户
     */
    String addUserRole(Long userId, Long roleId);

    String removeUserRole(Long userId, Long roleId);

    String getUserRoles(Long userId);

    String getRoleUsers(Long roleId);
}
