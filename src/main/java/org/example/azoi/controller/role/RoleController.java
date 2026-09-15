package org.example.azoi.controller.role;

import org.example.azoi.model.Role;
import org.springframework.web.bind.annotation.*;

/**
 * 用于控制Role相关的请求
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @GetMapping("/get")
    public String getRoles(){
        return "NOT IMPLEMENTED";
    }

    @PostMapping("/add")
    public String addRole(@RequestBody Role role){

    }

    @DeleteMapping("/delete")
    public String deleteRole(@RequestBody Role role){

    }
}
