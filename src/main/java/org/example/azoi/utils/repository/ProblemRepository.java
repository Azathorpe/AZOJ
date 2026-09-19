package org.example.azoi.utils.repository;

import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.model.problem_model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Arrays;
import java.util.List;

public interface ProblemRepository extends JpaRepository<Problem, Long>, JpaSpecificationExecutor<Problem> {
}
