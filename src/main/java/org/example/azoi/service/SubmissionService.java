package org.example.azoi.service;

import org.example.azoi.dto.Result;
import org.example.azoi.dto.submittransmit.SubmitDTO;
import org.example.azoi.dto.submittransmit.SubmitQueryDTO;
import org.example.azoi.dto.submittransmit.SubmitVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmissionService {
    Result<SubmitVO> submitAnswer(Long userId, Long problemId, Long contestId, String language, MultipartFile file);

    Result<SubmitVO> submitCode(SubmitDTO submitDTO);

    Result<List<SubmitVO>> getSubmits(SubmitQueryDTO query);

    Result<SubmitVO> getSubmit(Long submissionId);
}
