package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.User;
import org.example.azoi.model.UserRole;

import java.util.List;

/**
 * 管理用户和角色的关系
 */
public interface UserRoleService {
    /**
     * 为用户添加角色,注意 需要在注册用户时调用这个方法 {@link UserService#registerUser(User)}
     *
     * @param userId
     * @param roleId
     * @return 注册好角色的用户
     */
    Result<Object> addUserRole(Long userId, Long roleId);


    /**
     * 当用户注销的时候，需要删除用户的角色
     *
     * @param userId
     * @param roleId
     * @return
     */
    Result<Object> removeUserRole(Long userId, Long roleId);


    /**
     * 清除没有被使用的角色（admin）
     *
     * @return
     */
    Result<Object> removeNeverUsedUserRole();

    Result<List<UserRole>> getUserRoles(Long userId);

    Result<List<UserInfoVO>> getRoleUsers(Long roleId);
}
