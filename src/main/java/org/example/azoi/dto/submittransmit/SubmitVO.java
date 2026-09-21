package org.example.azoi.dto.submittransmit;

import org.example.azoi.model.Submission;

import java.time.Instant;

public class SubmitVO {
    private Long id;
    private Long userId;
    private Long contestId;
    private String language;
    private Long answerFileSize;
    private Byte status;
    private Integer score;
    private Integer timeUsed;
    private Integer memoryUsed;
    private String judgeLog;
    private Instant judgedAt;
    private String problemTitle;
    private String code;

    public SubmitVO() {
    }

    public SubmitVO(Submission sub){
        this.id = sub.getId();
        this.userId = sub.getUserId();
        this.contestId = sub.getContestId();
        this.language = sub.getLanguage();
        this.answerFileSize = sub.getAnswerFileSize();
        this.status = sub.getStatus();
        this.score = sub.getScore();
        this.timeUsed = sub.getTimeUsed();
        this.memoryUsed = sub.getMemoryUsed();
        this.judgeLog = sub.getJudgeLog();
        this.judgedAt = sub.getJudgedAt();
        this.code = sub.getCode();
    }

    public SubmitVO(Long id,Long userId, String code, String problemTitle, Long contestId, String language, Long answerFileSize, Byte status, Integer score, Integer timeUsed, Integer memoryUsed, String judgeLog, Instant judgedAt) {
        this.id = id;
        this.code = code;
        this.problemTitle = problemTitle;
        this.userId = userId;
        this.contestId = contestId;
        this.language = language;
        this.answerFileSize = answerFileSize;
        this.status = status;
        this.score = score;
        this.timeUsed = timeUsed;
        this.memoryUsed = memoryUsed;
        this.judgeLog = judgeLog;
        this.judgedAt = judgedAt;
    }

    public SubmitVO from(Submission sub){
        this.id = sub.getId();
        this.userId = sub.getUserId();
        this.contestId = sub.getContestId();
        this.language = sub.getLanguage();
        this.answerFileSize = sub.getAnswerFileSize();
        this.status = sub.getStatus();
        this.score = sub.getScore();
        this.timeUsed = sub.getTimeUsed();
        this.memoryUsed = sub.getMemoryUsed();
        this.judgeLog = sub.getJudgeLog();
        this.judgedAt = sub.getJudgedAt();
        this.code = sub.getCode();
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProblemTitle() {
        return problemTitle;
    }

    public void setProblemTitle(String problemTitle) {
        this.problemTitle = problemTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getAnswerFileSize() {
        return answerFileSize;
    }

    public void setAnswerFileSize(Long answerFileSize) {
        this.answerFileSize = answerFileSize;
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

    public Instant getJudgedAt() {
        return judgedAt;
    }

    public void setJudgedAt(Instant judgedAt) {
        this.judgedAt = judgedAt;
    }
}
