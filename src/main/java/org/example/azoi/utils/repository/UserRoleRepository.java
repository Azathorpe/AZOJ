package org.example.azoi.utils.repository;

import org.example.azoi.model.user_model.User;
import org.example.azoi.model.user_model.UserRole;
import org.example.azoi.model.user_model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    Optional<UserRole> getUserRoleById_UserId(Long idUserId);

    List<UserRole> getUserRolesById_RoleId(Long idRoleId);

    //不带条件的Query
    @Query("""
        SELECT ur, r
        FROM UserRole ur
        LEFT JOIN ur.role r
        """)
    List<Object[]> findAllWithRoleLeft();

    @Query("""
            SELECT DISTINCT u
            FROM UserRole ur
            JOIN ur.user u
            WHERE ur.id.roleId = :roleId
            """)
    List<User> findUsersByRoleId(@Param("roleId") Long roleId);

    void deleteById_UserId(Long idUserId);

    Optional<UserRole> findById_UserId(Long idUserId);

    Optional<UserRole> getUserRolesById_UserId(Long idUserId);
}
