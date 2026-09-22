package org.example.azoi.controller.problem;


import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemCreateDTO;
import org.example.azoi.dto.problemtransmit.ProblemInfoVO;
import org.example.azoi.dto.problemtransmit.ProblemQueryDTO;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.dto.problemtransmit.othertransmit.PageVO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemFileVO;
import org.example.azoi.dto.submittransmit.SubmitDTO;
import org.example.azoi.dto.submittransmit.SubmitVO;
import org.example.azoi.service.ProblemService;
import org.example.azoi.service.SubmissionService;
import org.example.azoi.utils.anno.CurrentUser;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/problem")
public class ProblemController {
    private final ProblemService problemService;
    private final SubmissionService submissionService;

    public ProblemController(ProblemService problemService, SubmissionService submissionService) {
        this.problemService = problemService;
        this.submissionService = submissionService;
    }

    /**
     * 获取所有的题目(筛选)<br/>
     * 请求地址: /problem/list<br/>
     * 请求方法: /problem/list -> json {@link ProblemQueryDTO}
     * @param problemQueryDTO 查询(可选)
     * @return 查询结果{@link List} of {@link ProblemSimpleInfoVO}
     */
    // Tips: GET方法 不需要在入参加上RequestBody,Spring会自动把拼接的参数绑定进去，没有的字段就是null
    @GetMapping("/list")
    public ResponseEntity<Result<PageVO<ProblemSimpleInfoVO>>> getProblemListBy(
            ProblemQueryDTO problemQueryDTO) {
        Result<PageVO<ProblemSimpleInfoVO>> result = problemService.getProblems(problemQueryDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 通过题目Id获取某个问题<br/>
     * 请求地址: /problem/{problemId}<br/>
     * 请求方法: /problem/{problemId}
     * @param problemId 问题Id
     * @return 问题详细信息{@link ProblemInfoVO}
     */
    @GetMapping("/{problemId}")
    public ResponseEntity<Result<ProblemInfoVO>> getProblemInfo(
            @PathVariable Long problemId) {
        Result<ProblemInfoVO> result = problemService.getProblemById(problemId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 创建问题<br/>
     * 请求地址: /problem/create<br/>
     * 请求方法: /problem/create -> json {@link ProblemCreateDTO}<br/>
     * Tips: createBy可以不传，Service会校验内容，不传默认上传者为作者
     * @param problemCreateDTO 创建问题的DTO{@link ProblemCreateDTO}
     * @param requestUserId 请求者Id
     * @return 问题的信息
     */
    @PostMapping("/create")
    public ResponseEntity<Result<ProblemInfoVO>> createProblem(
            @RequestBody ProblemCreateDTO problemCreateDTO,
            @CurrentUser Long requestUserId) {
        Result<ProblemInfoVO> result = problemService.createProblem(problemCreateDTO, requestUserId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 提交某个问题的测试文件(管理员|作者接口)<br/>
     * 请求地址: /problem/files<br/>
     * 请求方法: /problem/files
     * @param problemId 问题Id
     * @param files 测试文件s
     * @param fileTypes 测试文件类型
     * @param requestUserId 请求者Id
     * @return 放进去的测试文件s
     */
    @PostMapping("/{problemId}/files")
    @JsonSetter(nulls = Nulls.SKIP)
    public ResponseEntity<Result<List<ProblemFileVO>>> createProblemFile(
            @PathVariable Long problemId,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("fileTypes") Byte[] fileTypes,
            @CurrentUser Long requestUserId) {
        Result<List<ProblemFileVO>> result = problemService.createProblemFile(problemId, files, fileTypes, requestUserId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 通过题目Id回答某个问题(提交文件)<br/>
     * 请求地址: /problem/submitanswer<br/>
     * 请求方法: /problem/submitanswer
     * @param problemId 问题Id
     * @param contestId 测试Id(可空)
     * @param language 语言
     * @param file 解题文件
     * @param requesterId 请求者Id
     * @return 提交后的信息
     */
    @PostMapping("/submitanswer")
    public ResponseEntity<Result<SubmitVO>> submitProblem(
            @RequestParam Long problemId,
            @RequestParam(required = false) Long contestId,
            @RequestParam String language,
            MultipartFile file,
            @CurrentUser Long requesterId) {
        Result<SubmitVO> result = submissionService.submitAnswer(requesterId, problemId, contestId, language, file);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 通过题目Id回答某个问题(提交代码)<br/>
     * 请求地址: /problem/submitcode<br/>
     * 请求方法: /problem/submitcode
     * @param submitDTO 提交DTO{@link SubmitDTO}
     * @param requesterId 请求者Id
     * @return 提交后的信息
     */
    @PostMapping("/submitcode")
    public ResponseEntity<Result<SubmitVO>> submitProblemCode(
            @RequestBody SubmitDTO submitDTO,
            @CurrentUser Long requesterId) {
        Result<SubmitVO> result = submissionService.submitCode(submitDTO, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 通过题目Id软删除一个问题(管理员|作者接口)<br/>
     * 请求地址: /problem/soft/{problemId}<br/>
     * 请求方法: /problem/soft/{problemId}
     * @param problemId 问题Id
     * @param requesterId 请求者Id
     * @return 无
     */
    @DeleteMapping("/soft/{problemId}")
    //软删除题目
    public ResponseEntity<Result<Void>> removeProblem(
            @PathVariable Long problemId,
            @CurrentUser Long  requesterId) {
        Result<Void> result = problemService.removeProblem(problemId, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    /**
     * 通过题目Id删除一个问题(管理员|作者接口)<br/>
     * 请求地址: /problem/delete/{problemId}<br/>
     * 请求方法: /problem/delete/{problemId}
     * @param problemId 问题Id
     * @param requesterId 请求者Id
     * @return 无
     */
    @DeleteMapping("/delete/{problemId}")
    public ResponseEntity<Result<Void>> deleteProblem(
            @PathVariable Long problemId,
            @CurrentUser Long requesterId) {
        Result<Void> result = problemService.deleteProblem(problemId, requesterId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
