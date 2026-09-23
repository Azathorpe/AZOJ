package org.example.azoi.model;

import jakarta.persistence.*;
import org.example.azoi.utils.exception.BusinessException;

import java.time.Instant;

@Entity
@Table(name = "submissions", schema = "azoi")
public class Submission {
    public static final byte STATUS_OK = -1;
    public static final byte STATUS_PENDING = 0;
    public static final byte STATUS_JUDGING = 1;
    public static final byte STATUS_AC = 2;
    public static final byte STATUS_WA = 3;
    public static final byte STATUS_TLE = 4;
    public static final byte STATUS_MLE = 5;
    public static final byte STATUS_RE = 6;
    public static final byte STATUS_CE = 7;
    public static final byte STATUS_OLE = 8;
    public static final byte STATUS_UKE = 9;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "contest_id")
    private Long contestId;

    @Column(name = "language", length = 16)
    private String language;

    @Column(name = "answer_file_path", length = 512)
    private String answerFilePath;

    @Column(name = "answer_file_size")
    private Long answerFileSize;

    @Column(name = "answer_md5", length = 32)
    private String answerMd5;

    @Column(name = "status", nullable = false)
    private Byte status = STATUS_PENDING;

    @Column(name = "score", nullable = false)
    private Integer score = 0;

    @Column(name = "time_used")
    private Integer timeUsed;

    @Column(name = "memory_used")
    private Integer memoryUsed;

    @Lob
    @Column(name = "judge_log")
    private String judgeLog;

    @Column(name = "judge_id", length = 64)
    private String judgeId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "judged_at")
    private Instant judgedAt;
    @Lob
    @Column(name = "code")
    private String code;

    @PrePersist
    public void onCreated() {
        createdAt = Instant.now();
    }

    public Submission() {
    }

    public Submission(Long id, Long userId, Long problemId, Long contestId, String language, String answerFilePath, Long answerFileSize, String answerMd5, Byte status, Integer score, Integer timeUsed, Integer memoryUsed, String judgeLog, String judgeId, Instant createdAt, Instant judgedAt, String code) {
        this.id = id;
        this.userId = userId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
        this.answerFilePath = answerFilePath;
        this.answerFileSize = answerFileSize;
        this.answerMd5 = answerMd5;
        this.status = status;
        this.score = score;
        this.timeUsed = timeUsed;
        this.memoryUsed = memoryUsed;
        this.judgeLog = judgeLog;
        this.judgeId = judgeId;
        this.createdAt = createdAt;
        this.judgedAt = judgedAt;
        this.code = code;
    }

    public static byte toStatus(String status) {
        switch (status) {
            case "0" -> {
                return STATUS_PENDING;
            }
            case "1" -> {
                return STATUS_JUDGING;
            }
            case "2" -> {
                return STATUS_AC;
            }
            case "3" -> {
                return STATUS_WA;
            }
            case "4" -> {
                return STATUS_TLE;
            }
            case "5" -> {
                return STATUS_MLE;
            }
            case "6" -> {
                return STATUS_RE;
            }
            case "7" -> {
                return STATUS_CE;
            }
            case "8" -> {
                return STATUS_OLE;
            }
            case "9" -> {
                return STATUS_UKE;
            }
            default -> throw new BusinessException("Unexpected value: " + status);
        }
    }

    public static String parseStatus(String status) {
        return parseStatus(Byte.parseByte(status));
    }

    public static String parseStatus(byte status) {
        switch (status) {
            case STATUS_PENDING -> {
                return "PENDING";
            }
            case STATUS_JUDGING -> {
                return "JUDGING";
            }
            case STATUS_AC -> {
                return "AC";
            }
            case STATUS_WA -> {
                return "WA";
            }
            case STATUS_TLE -> {
                return "TLE";
            }
            case STATUS_MLE -> {
                return "MLE";
            }
            case STATUS_RE -> {
                return "RE";
            }
            case STATUS_CE -> {
                return "CE";
            }
            case STATUS_OLE -> {
                return "OLE";
            }
            case STATUS_UKE -> {
                return "UKE";
            }
            default -> throw new BusinessException("Unexpected value: " + status);
        }
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

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
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

    public String getAnswerFilePath() {
        return answerFilePath;
    }

    public void setAnswerFilePath(String answerFilePath) {
        this.answerFilePath = answerFilePath;
    }

    public Long getAnswerFileSize() {
        return answerFileSize;
    }

    public void setAnswerFileSize(Long answerFileSize) {
        this.answerFileSize = answerFileSize;
    }

    public String getAnswerMd5() {
        return answerMd5;
    }

    public void setAnswerMd5(String answerMd5) {
        this.answerMd5 = answerMd5;
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

    public String getJudgeId() {
        return judgeId;
    }

    public void setJudgeId(String judgeId) {
        this.judgeId = judgeId;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}