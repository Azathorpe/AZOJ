package org.example.azoi.service.impl;

import org.example.azoi.dto.Result;
import org.example.azoi.model.team_model.Role;
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
    public Result<List<Role>> getRoles() {
        Iterable<Role> all = roleRepository.findAll();
        List<Role> list = new ArrayList<>();
        for(Role r : all)
            list.add(r);
        return new Result<>(list, Result.SUCCESS, "ok");
    }

    @Override
    @Transactional
    public Result<Role> addRole(Role role) {
        return new Result<>(roleRepository.save(role), Result.SUCCESS, "ok");
    }

    @Override
    @Transactional
    public Result<Role> updateRole(Role role) {
        Role existing = roleRepository.findById(role.getId())
                .orElseThrow(() -> new RuntimeException("NOT FOUND"));
        existing.setName(role.getName() == null ? existing.getName() : role.getName());
        existing.setCode(role.getCode() == null ? existing.getCode() : role.getCode());
        return new Result<>(existing, Result.SUCCESS, "ok");
    }

    @Override
    @Transactional
    public Result<String> deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
        return new Result<>("role deleted", Result.SUCCESS, "ok");
    }
}
