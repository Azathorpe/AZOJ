package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.model.user_model.Role;
import org.example.azoi.model.user_model.UserRole;
import org.example.azoi.service.RoleService;
import org.example.azoi.service.UserService;
import org.example.azoi.utils.repository.RoleRepository;
import org.example.azoi.utils.repository.UserRepository;
import org.example.azoi.utils.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Deprecated
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleServiceImpl(RoleRepository roleRepository, UserService userService, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
    }

    /**
     * Service
     * 获取所有的Roles<br/>
     * 请求地址: /role/get<br/>
     * 请求方法: /role/get
     * @return {@link List} of {@link Role}
     */
    @Override
    public Result<List<Role>> getRoles() {
        Iterable<Role> all = roleRepository.findAll();
        List<Role> list = new ArrayList<>();
        for (Role r : all)
            list.add(r);
        return new Result<>(list, Result.SUCCESS, "ok");
    }

    /**
     * Service
     * 添加新的Role(管理员接口)<br/>
     * 请求地址: /role/add<br/>
     * 请求方法: /role/add -> json
     * @param role 角色{@link Role}
     * @param requesterId 请求者id
     * @return 角色{@link Role}
     */
    @Override
    @Transactional
    public Result<Role> addRole(Role role, Long requesterId) {
        //只有管理员才能能添加新的角色
        Result<Role> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return result;
        return new Result<>(roleRepository.save(role), Result.SUCCESS, "ok");
    }

    /**
     * Service
     * 更新一个Role(管理员接口)<br/>
     * 请求地址: /role/update<br/>
     * 请求方法: /role/update
     * @param role 角色{@link Role}
     * @param requesterId 请求者id
     * @return 角色{@link Role}
     */
    @Override
    @Transactional
    public Result<Role> updateRole(Role role, Long requesterId) {
        //只有管理员才能能修改角色
        Result<Role> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return result;

        Role existing = roleRepository.findById(role.getId())
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
        existing.setName(role.getName() == null ? existing.getName() : role.getName());
        existing.setCode(role.getCode() == null ? existing.getCode() : role.getCode());
        return new Result<>(existing, Result.SUCCESS, "ok");
    }

    /**
     * Service
     * 删除一个Role(管理员接口)<br/>
     * 请求地址: /role/delete<br/>
     * 请求方法: /role/delete?roleId=x
     * @param roleId 角色Id
     * @param requesterId 请求者Id
     * @return 角色{@link Role}
     */
    @Override
    @Transactional
    public Result<Role> deleteRole(Long roleId, Long requesterId) {
        //只有管理员才能能修改角色
        Result<Role> result = checkerAdmin(requesterId);
        if(result.getCode() == Result.FAIL)
            return result;

        roleRepository.deleteById(roleId);
        return new Result<>(null, Result.SUCCESS, "role deleted");
    }

    /**
     * 检查一个角色是否是admin
     * @param requesterId 请求者Id
     * @return 实际上Result没有内容，直接查看code
     */
    private Result<Role> checkerAdmin(Long requesterId) {
        //只有管理员才能能添加新的角色
        Optional<UserRole> requester = userRoleRepository.findById_UserId((requesterId));
        if (requester.isEmpty())
            return new Result<>(null, Result.FAIL, "未找到您的信息: requester is empty");
        if (!requester.get().getRole().getName().equals(Role.ROLE_ADMIN)) {
            return new Result<>(null, Result.FAIL, "您不是管理员");
        }
        return new Result<>(null, Result.SUCCESS, "ok");
    }
}
