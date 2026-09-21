package org.example.azoi.controller.problem;


import org.example.azoi.dto.Result;
import org.example.azoi.dto.submittransmit.SubmitDTO;
import org.example.azoi.dto.submittransmit.SubmitQueryDTO;
import org.example.azoi.dto.submittransmit.SubmitVO;
import org.example.azoi.service.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/submit")
public class SubmitController {
    private final SubmissionService submissionService;

    public SubmitController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    /**
     * 获取所有的提交(查询)<br/>
     * 请求地址: /submit/list<br/>
     * 请求方法: /submit/list
     * @param submitQueryDTO 查询(可空)
     * @return {@link List} of {@link SubmitVO}
     */
    @GetMapping("/list")
    public ResponseEntity<Result<List<SubmitVO>>> getSubmitList(SubmitQueryDTO submitQueryDTO) {
        // 如果没有查询条件  那就默认查询
        Result<List<SubmitVO>> result = submissionService.getSubmits(submitQueryDTO == null ? new SubmitQueryDTO() : submitQueryDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 通过提交Id获取提交<br/>
     * 请求地址: /submit/{submissionId}<br/>
     * 请求方法: /submit/{submissionId}
     * @param submissionId 提交Id
     * @return {@link SubmitVO}
     */
    @GetMapping("/{submissionId}")
    public ResponseEntity<Result<SubmitVO>> getSubmit(@PathVariable Long submissionId) {
        Result<SubmitVO> result = submissionService.getSubmit(submissionId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
