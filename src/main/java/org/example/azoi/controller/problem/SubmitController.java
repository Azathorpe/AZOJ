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

    @GetMapping("/list")
    public ResponseEntity<Result<List<SubmitVO>>> getSubmitList(@RequestBody SubmitQueryDTO submitQueryDTO) {
        Result<List<SubmitVO>> result = submissionService.getSubmits(submitQueryDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @GetMapping("/{submissionId}")
    public ResponseEntity<Result<SubmitVO>> getSubmit(@PathVariable Long submissionId) {
        Result<SubmitVO> result = submissionService.getSubmit(submissionId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
