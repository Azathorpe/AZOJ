package org.example.azoi.controller.problem;


import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.othertransmit.PageVO;
import org.example.azoi.dto.submittransmit.SubmitQueryDTO;
import org.example.azoi.dto.submittransmit.SubmitVO;
import org.example.azoi.service.SubmissionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/submission")
public class SubmissionController {
    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
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
    public Result<PageVO<SubmitVO>> getSubmitList(SubmitQueryDTO submitQueryDTO) {
        // 如果没有查询条件  那就默认查询
        return submissionService.getSubmits(submitQueryDTO == null ? new SubmitQueryDTO() : submitQueryDTO);
    }

    /**
     * 通过提交Id获取提交<br/>
     * 请求地址: /submit/{submissionId}<br/>
     * 请求方法: /submit/{submissionId}
     * @param submissionId 提交Id
     * @return {@link SubmitVO}
     */
    @GetMapping("/{submissionId}")
    public Result<SubmitVO> getSubmit(@PathVariable Long submissionId) {
        return submissionService.getSubmit(submissionId);
    }
}
