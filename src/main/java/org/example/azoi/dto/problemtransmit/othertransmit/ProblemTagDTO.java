package org.example.azoi.dto.problemtransmit.othertransmit;

import jakarta.persistence.Column;

public class ProblemTagDTO {
    private Long problemId;
    private Long tagId;

    public ProblemTagDTO() {
    }

    public ProblemTagDTO(Long problemId, Long tagId) {
        this.problemId = problemId;
        this.tagId = tagId;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }
}
