package org.example.azoi.utils.repository;

import org.example.azoi.model.problem_model.ProblemFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProblemFileRepository extends JpaRepository<ProblemFile, Long> {
    List<ProblemFile> findAllByProblemId(Long problemId);

    long countProblemFileByProblemIdAndFileType(Long problemId, Byte fileType);

}
