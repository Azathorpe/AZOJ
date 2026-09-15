package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.azoi.model.Role;
import org.example.azoi.service.RoleService;
import org.example.azoi.utils.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public String getRoles() {
        Iterable<Role> all = roleRepository.findAll();
        List<Role> list = new ArrayList<Role>();
        for(Role r : all)
            list.add(r);
        return JSON.toJSONString(list);
    }

    @Override
    public String addRole(Role role) {
        return "";
    }

    @Override
    public String updateRole(Role role) {
        return "";
    }

    @Override
    public String deleteRole(Long roleId) {
        return "";
    }
}
