package org.example.azoi.controller.problem;


import org.example.azoi.dto.Result;
import org.example.azoi.dto.problemtransmit.ProblemCreateDTO;
import org.example.azoi.dto.problemtransmit.ProblemInfoVO;
import org.example.azoi.dto.problemtransmit.ProblemQueryDTO;
import org.example.azoi.dto.problemtransmit.ProblemSimpleInfoVO;
import org.example.azoi.service.ProblemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/problem")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    // Tips: GET方法 不需要在入参加上RequestBody,Spring会自动把拼接的参数绑定进去，没有的字段就是null
    @GetMapping("/list")
    public ResponseEntity<Result<List<ProblemSimpleInfoVO>>> getProblemListBy(ProblemQueryDTO problemQueryDTO){
        Result<List<ProblemSimpleInfoVO>> result = problemService.getProblems(problemQueryDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @GetMapping("/{problemId}")
    public ResponseEntity<Result<ProblemInfoVO>> getProblemInfo(@PathVariable Long problemId){
        Result<ProblemInfoVO> result = problemService.getProblem(problemId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @PostMapping("/create")
    public ResponseEntity<Result<ProblemInfoVO>> createProblem(@RequestBody ProblemCreateDTO problemCreateDTO){
        Result<ProblemInfoVO> result = problemService.createProblem(problemCreateDTO);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @DeleteMapping("/soft/{problemId}")
    //软删除题目
    public ResponseEntity<Result<Void>> removeProblem(@PathVariable Long problemId){
        Result<Void> result = problemService.removeProblem(problemId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }

    @DeleteMapping("/delete/{problemId}")
    public ResponseEntity<Result<Void>> deleteProblem(@PathVariable Long problemId){
        Result<Void> result = problemService.deleteProblem(problemId);
        return result.getCode() == Result.SUCCESS
                ? ResponseEntity.status(HttpStatus.OK).body(result)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
