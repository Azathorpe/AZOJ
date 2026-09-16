package org.example.azoi.controller.role;

import com.alibaba.fastjson.JSON;
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

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/get")
    public String getRoles() {
        return JSON.toJSONString(roleService.getRoles());
    }

    @PostMapping("/add")
    public String addRole(@RequestBody Role role) {
        return JSON.toJSONString(roleService.addRole(role));
    }

    @PostMapping("/update")
    public String updateRole(@RequestBody Role role) {
        return JSON.toJSONString(roleService.updateRole(role));
    }

    @DeleteMapping("/delete")
    public String deleteRole(@RequestBody Role role) {
        return JSON.toJSONString((roleService.deleteRole(role.getId())));
    }
}
