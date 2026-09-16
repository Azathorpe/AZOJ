package org.example.azoi.utils.repository;

import org.example.azoi.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> getTeamById(Long id);
}
