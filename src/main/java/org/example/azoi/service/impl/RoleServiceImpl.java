package org.example.azoi.service.impl;

import com.alibaba.fastjson.JSON;
import org.example.azoi.model.Role;
import org.example.azoi.model.User;
import org.example.azoi.service.RoleService;
import org.example.azoi.utils.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public String addRole(Role role) {
        return JSON.toJSONString(roleRepository.save(role));
    }

    @Override
    @Transactional
    public String updateRole(Role role) {
        Role existing = roleRepository.findById(role.getId())
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
        existing.setName(role.getName());
        existing.setCode(role.getCode());
        return JSON.toJSONString(existing);
    }

    @Override
    @Transactional
    public String deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
        return JSON.toJSONString("role deleted");
    }
}
