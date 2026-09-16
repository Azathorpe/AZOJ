package org.example.azoi.utils.repository;

import org.example.azoi.model.UserRole;
import org.example.azoi.model.UserRoleId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRoleRepository extends CrudRepository<UserRole, UserRoleId> {
    List<UserRole> getUserRoleById_UserId(Long idUserId);

    List<UserRole> getUserRolesById_RoleId(Long idRoleId);
}
