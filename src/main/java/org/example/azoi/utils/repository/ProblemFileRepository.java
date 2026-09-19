package org.example.azoi.utils.repository;

import org.example.azoi.model.problem_model.ProblemFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProblemFileRepository extends JpaRepository<ProblemFile, Long> {
}
