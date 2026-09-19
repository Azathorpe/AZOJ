package org.example.azoi.model.problem_model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ProblemTagId implements Serializable {
    private static final long serialVersionUID = 636563183824412742L;
    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    public ProblemTagId() {
    }

    public ProblemTagId(Long problemId, Long tagId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProblemTagId entity = (ProblemTagId) o;
        return Objects.equals(this.problemId, entity.problemId) &&
                Objects.equals(this.tagId, entity.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(problemId, tagId);
    }
}