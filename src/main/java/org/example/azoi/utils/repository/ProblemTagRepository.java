package org.example.azoi.utils.repository;

import org.example.azoi.model.problem_model.ProblemTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemTagRepository extends JpaRepository<ProblemTag, Long> {
    List<ProblemTag> findById_TagId(Long idTagId);

    void deleteAllById_ProblemId(Long idProblemId);
}
