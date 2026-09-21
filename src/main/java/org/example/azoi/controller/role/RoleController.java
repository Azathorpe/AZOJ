package org.example.azoi.controller.role;

import org.example.azoi.dto.Result;
import org.example.azoi.model.team_model.Role;
import org.example.azoi.service.RoleService;
import org.example.azoi.utils.anno.CurrentUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用于控制Role相关的请求
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * 获取所有的Roles<br/>
     * 请求地址: /role/get<br/>
     * 请求方法: /role/get
     * @return {@link List} of {@link Role}
     */
    @GetMapping("/get")
    public ResponseEntity<Result<List<Role>>> getRoles(
            @CurrentUser Long userId) {
        Result<List<Role>> result = roleService.getRoles();
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 添加新的Role(管理员接口)<br/>
     * 请求地址: /role/add<br/>
     * 请求方法: /role/add -> json
     * @param role 角色{@link Role}
     * @param requesterId 请求者id
     * @return 角色{@link Role}
     */
    @PostMapping("/add")
    public ResponseEntity<Result<Role>> addRole(
            @RequestBody Role role,
            @CurrentUser Long requesterId) {
        Result<Role> result = roleService.addRole(role, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 更新一个Role(管理员接口)<br/>
     * 请求地址: /role/update<br/>
     * 请求方法: /role/update
     * @param role 角色{@link Role}
     * @param requesterId 请求者id
     * @return 角色{@link Role}
     */
    @PostMapping("/update")
    public ResponseEntity<Result<Role>> updateRole(
            @RequestBody Role role,
            @CurrentUser Long requesterId) {
        Result<Role> result = roleService.updateRole(role, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    /**
     * 删除一个Role(管理员接口)<br/>
     * 请求地址: /role/delete<br/>
     * 请求方法: /role/delete?roleId=x
     * @param role 角色{@link Role}
     * @param requesterId 请求者Id
     * @return 角色{@link Role}
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Result<Role>> deleteRole(
            @RequestBody Role role,
            @CurrentUser Long requesterId) {
        Result<Role> result = roleService.deleteRole(role.getId(), requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
