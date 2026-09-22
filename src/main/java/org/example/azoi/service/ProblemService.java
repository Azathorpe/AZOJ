package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemCreateDTO;
import org.example.azoi.dto.problemtransmit.ProblemInfoVO;
import org.example.azoi.dto.problemtransmit.ProblemQueryDTO;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.dto.problemtransmit.othertransmit.PageVO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemFileVO;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProblemService {
    Result<PageVO<ProblemSimpleInfoVO>> getProblems(ProblemQueryDTO problemQueryDTO);

    Result<ProblemInfoVO> getProblemById(Long problemId);

    Result<ProblemInfoVO> createProblem(ProblemCreateDTO problemCreateDTO, Long requesterId);

    Result<List<ProblemFileVO>> createProblemFile(Long problemId , MultipartFile[] files, Byte[] fileTypes, Long requesterId);

    Result<Void> removeProblem(Long problemId, Long requesterId);

    Result<Void> deleteProblem(Long problemId, Long requesterId);
}
