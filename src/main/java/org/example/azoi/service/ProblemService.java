package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemCreateDTO;
import org.example.azoi.dto.problemtransmit.ProblemInfoVO;
import org.example.azoi.dto.problemtransmit.ProblemQueryDTO;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;

import java.util.List;

public interface ProblemService {
    Result<List<ProblemSimpleInfoVO>> getProblems(ProblemQueryDTO problemQueryDTO);

    Result<ProblemInfoVO> getProblem(Long problemId);

    Result<ProblemInfoVO> createProblem(ProblemCreateDTO problemCreateDTO);
}
