package org.example.azoi.controller.role;

import org.example.azoi.dto.Result;
import org.example.azoi.model.Role;
import org.example.azoi.service.RoleService;
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

    @GetMapping("/get")
    public ResponseEntity<Result<List<Role>>> getRoles() {
        Result<List<Role>> result = roleService.getRoles();
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PostMapping("/add")
    public ResponseEntity<Result<Role>> addRole(@RequestBody Role role) {
        Result<Role> result = roleService.addRole(role);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.CREATED).body(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    @PostMapping("/update")
    public ResponseEntity<Result<Role>> updateRole(@RequestBody Role role) {
        Result<Role> result = roleService.updateRole(role);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    ///TODO：可以考虑是否是用软删除的方式
    ///fixme: 是否要统一风格 使用parma的形式传参
    @DeleteMapping("/delete")
    public ResponseEntity<Result<String>> deleteRole(@RequestBody Role role) {
        Result<String> result = roleService.deleteRole(role.getId());
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
