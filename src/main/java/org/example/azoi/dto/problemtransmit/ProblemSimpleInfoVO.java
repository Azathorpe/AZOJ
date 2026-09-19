package org.example.azoi.dto.problemtransmit;

import org.example.azoi.model.problem_model.Problem;

import java.time.Instant;

//题目信息的简单化 建议在搜索页用这个实体
public class ProblemSimpleInfoVO {
    private String title;
    private Byte difficulty;
    private Byte judgeType;
    private Integer submitCount;
    private Integer acceptedCount;
    private Instant createdAt;

    public ProblemSimpleInfoVO() {
    }

    public ProblemSimpleInfoVO(Problem problem){
        this.acceptedCount = problem.getAcceptedCount();
        this.createdAt = problem.getCreatedAt();
        this.difficulty = problem.getDifficulty();
        this.submitCount = problem.getSubmitCount();
        this.judgeType = problem.getJudgeType();;
        this.title = problem.getTitle();
    }

    public ProblemSimpleInfoVO(String title, Byte difficulty, Byte judgeType, Integer submitCount, Integer acceptedCount, Instant createdAt) {
        this.title = title;
        this.difficulty = difficulty;
        this.judgeType = judgeType;
        this.submitCount = submitCount;
        this.acceptedCount = acceptedCount;
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Byte getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Byte difficulty) {
        this.difficulty = difficulty;
    }

    public Byte getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(Byte judgeType) {
        this.judgeType = judgeType;
    }

    public Integer getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Integer submitCount) {
        this.submitCount = submitCount;
    }

    public Integer getAcceptedCount() {
        return acceptedCount;
    }

    public void setAcceptedCount(Integer acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
