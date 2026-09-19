package org.example.azoi.utils.repository;

import org.example.azoi.model.problem_model.ProblemSample;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemSampleRepository extends JpaRepository<ProblemSample, Long> {
}
