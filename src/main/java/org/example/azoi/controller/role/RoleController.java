package org.example.azoi.controller.role;

import org.example.azoi.model.Role;
import org.example.azoi.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用于控制Role相关的请求
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("/get")
    public String getRoles() {
        return roleService.getRoles();
    }

    @PostMapping("/add")
    public String addRole(@RequestBody Role role) {
        return "NOT IMPLEMENTED";
    }

    @PostMapping("/update")
    public String updateRole(@RequestBody Role role) {
        return "NOT IMPLEMENTED";
    }

    @DeleteMapping("/delete")
    public String deleteRole(@RequestBody Role role) {
        return "NOT IMPLEMENTED";
    }
}
