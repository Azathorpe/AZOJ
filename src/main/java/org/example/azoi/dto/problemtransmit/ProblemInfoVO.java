package org.example.azoi.dto.problemtransmit;

import jakarta.persistence.*;
import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.model.problem_model.Problem;

import java.time.Instant;

//题目信息+作者的VO
public class ProblemInfoVO {
    private String title;
    private String description;
    private String inputFormat;
    private String outputFormat;
    private String hint;
    private Byte difficulty;
    private Integer timeLimit;
    private Integer memoryLimit;
    private Integer outputLimit;
    private Byte judgeType;
    private Boolean isVisible;
    private Integer submitCount;
    private Integer acceptedCount;
    private Instant createdAt;
    private Instant updatedAt;

    private UserInfoVO createdBy;

    public ProblemInfoVO() {
    }

    public ProblemInfoVO(Problem problem) {
        this.title = problem.getTitle();
        this.description = problem.getDescription();
        this.inputFormat = problem.getInputFormat();
        this.outputFormat = problem.getOutputFormat();
        this.hint = problem.getHint();
        this.difficulty = problem.getDifficulty();
        this.timeLimit = problem.getTimeLimit();
        this.memoryLimit = problem.getMemoryLimit();
        this.outputLimit = problem.getOutputLimit();
        this.judgeType = problem.getJudgeType();
        this.isVisible = problem.getIsVisible();
        this.submitCount = problem.getSubmitCount();
        this.acceptedCount = problem.getAcceptedCount();
        this.createdAt = problem.getCreatedAt();
        this.updatedAt = problem.getUpdatedAt();
    }

    public ProblemInfoVO(Problem problem, UserInfoVO createdBy) {
        this(problem);
        this.createdBy = createdBy;
    }

    public ProblemInfoVO(String title, String description, String inputFormat, String outputFormat, String hint, Byte difficulty, Integer timeLimit, Integer memoryLimit, Integer outputLimit, Byte judgeType, Boolean isVisible, Integer submitCount, Integer acceptedCount, Instant createdAt, Instant updatedAt, UserInfoVO createdBy) {
        this.title = title;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.hint = hint;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.outputLimit = outputLimit;
        this.judgeType = judgeType;
        this.isVisible = isVisible;
        this.submitCount = submitCount;
        this.acceptedCount = acceptedCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Byte getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Byte difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Integer getOutputLimit() {
        return outputLimit;
    }

    public void setOutputLimit(Integer outputLimit) {
        this.outputLimit = outputLimit;
    }

    public Byte getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(Byte judgeType) {
        this.judgeType = judgeType;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
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

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserInfoVO getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfoVO createdBy) {
        this.createdBy = createdBy;
    }
}
