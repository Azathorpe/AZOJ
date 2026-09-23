package org.example.azoi.dto.submittransmit;

import org.example.azoi.model.Submission;

import java.time.Instant;

public class SubmitVO {
    private Long submissionId;
    private Long userId;
    private String username;         // 提交者用户名
    private Long problemId;
    private String problemTitle;     // 题目标题
    private String language;
    private String code;             // 代码（详情用）
    private Byte status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private String judgeLog;         // 每个测试点结果
    private Instant createdAt;
    private Instant judgedAt;

    public SubmitVO() {
    }

    public SubmitVO(Long submissionId, Long userId, String username, Long problemId, String problemTitle, String language, String code, Byte status, Integer score, Integer timeUsed, Integer memoryUsed, String judgeLog, Instant createdAt, Instant judgedAt) {
        this.submissionId = submissionId;
        this.userId = userId;
        this.username = username;
        this.problemId = problemId;
        this.problemTitle = problemTitle;
        this.language = language;
        this.code = code;
        this.status = status;
        this.score = score;
        this.timeUsed = timeUsed;
        this.memoryUsed = memoryUsed;
        this.judgeLog = judgeLog;
        this.createdAt = createdAt;
        this.judgedAt = judgedAt;
    }

    public SubmitVO(Submission submission){
        this.submissionId = submission.getId();
        this.userId = submission.getUserId();
        this.problemId = submission.getProblemId();
        this.language = submission.getLanguage();
        this.code = submission.getCode();
        this.status = submission.getStatus();
        this.score = submission.getScore();
        this.timeUsed = submission.getTimeUsed();
        this.memoryUsed = submission.getMemoryUsed();
        this.judgeLog = submission.getJudgeLog();
        this.createdAt = submission.getCreatedAt();
        this.judgedAt = submission.getJudgedAt();
    }

    public SubmitVO(Submission submission, String username, String problemTitle){
        this.submissionId = submission.getId();
        this.userId = submission.getUserId();
        this.username = username;
        this.problemId = submission.getProblemId();
        this.problemTitle = problemTitle;
        this.language = submission.getLanguage();
        this.code = submission.getCode();
        this.status = submission.getStatus();
        this.score = submission.getScore();
        this.timeUsed = submission.getTimeUsed();
        this.memoryUsed = submission.getMemoryUsed();
        this.judgeLog = submission.getJudgeLog();
        this.createdAt = submission.getCreatedAt();
        this.judgedAt = submission.getJudgedAt();
    }

    public SubmitVO from(Submission submission){
        this.submissionId = submission.getId();
        this.userId = submission.getUserId();
        this.problemId = submission.getProblemId();
        this.language = submission.getLanguage();
        this.code = submission.getCode();
        this.status = submission.getStatus();
        this.score = submission.getScore();
        this.timeUsed = submission.getTimeUsed();
        this.memoryUsed = submission.getMemoryUsed();
        this.judgeLog = submission.getJudgeLog();
        this.createdAt = submission.getCreatedAt();
        this.judgedAt = submission.getJudgedAt();
        return this;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTimeUsed() {
        return timeUsed;
    }

    public void setTimeUsed(Integer timeUsed) {
        this.timeUsed = timeUsed;
    }

    public Integer getMemoryUsed() {
        return memoryUsed;
    }

    public void setMemoryUsed(Integer memoryUsed) {
        this.memoryUsed = memoryUsed;
    }

    public String getJudgeLog() {
        return judgeLog;
    }

    public void setJudgeLog(String judgeLog) {
        this.judgeLog = judgeLog;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getJudgedAt() {
        return judgedAt;
    }

    public void setJudgedAt(Instant judgedAt) {
        this.judgedAt = judgedAt;
    }
}
