package org.example.azoi.utils.repository;

import org.example.azoi.model.user_model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
