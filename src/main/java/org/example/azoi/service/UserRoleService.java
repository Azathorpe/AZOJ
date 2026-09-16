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


    /**
     * 当用户注销的时候，需要删除用户的角色
     * @param userId
     * @param roleId
     * @return
     */
    String removeUserRole(Long userId, Long roleId);


    /**
     * 清除没有被使用的角色（admin）
     * @return
     */
    String removeNeverUsedUserRole();

    String getUserRoles(Long userId);

    String getRoleUsers(Long roleId);
}
