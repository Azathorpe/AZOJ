package org.example.azoi.model.problem_model;

import jakarta.persistence.*;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemSampleDTO;

@Entity
@Table(name = "problem_samples", schema = "azoi")
public class ProblemSample {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Lob
    @Column(name = "input", nullable = false)
    private String input;

    @Lob
    @Column(name = "output", nullable = false)
    private String output;

    @Lob
    @Column(name = "explanation")
    private String explanation;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    public ProblemSample() {
    }

    public ProblemSample(ProblemSampleDTO sample) {
        this.input = sample.getInput();
        this.output = sample.getOutput();
        this.explanation = sample.getExplanation();
        this.sortOrder = sample.getSortOrder();
    }

    public ProblemSample(Long id, Long problemId, String input, String output, String explanation, Integer sortOrder) {
        this.id = id;
        this.problemId = problemId;
        this.input = input;
        this.output = output;
        this.explanation = explanation;
        this.sortOrder = sortOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}