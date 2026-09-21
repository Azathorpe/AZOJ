package org.example.azoi.utils.repository;

import org.example.azoi.model.team_model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> getTeamById(Long id);

    Optional<Team> findByOwnerId(Long ownerId);
}
