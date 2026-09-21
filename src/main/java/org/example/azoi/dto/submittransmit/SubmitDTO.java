package org.example.azoi.dto.submittransmit;

public class SubmitDTO {
    private Long userId;
    private Long problemId;
    private Long contestId;
    private String language;
    private String code;

    public SubmitDTO() {
    }

    public SubmitDTO(Long userId, Long problemId, Long contestId, String language, String code) {
        this.userId = userId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.language = language;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

}
