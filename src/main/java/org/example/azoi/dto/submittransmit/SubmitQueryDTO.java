package org.example.azoi.dto.submittransmit;

public class SubmitQueryDTO {
    private Long userId;
    private Long problemId;
    private Long contestId;
    private Byte status;
    private int page = 1;
    private int size = 20;

    public SubmitQueryDTO() {
    }

    public SubmitQueryDTO(Long userId, Long problemId, Long contestId, Byte status, int page, int size) {
        this.userId = userId;
        this.problemId = problemId;
        this.contestId = contestId;
        this.status = status;
        this.page = page;
        this.size = size;
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

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
