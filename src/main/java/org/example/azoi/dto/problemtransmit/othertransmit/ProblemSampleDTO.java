package org.example.azoi.dto.problemtransmit.othertransmit;

import jakarta.persistence.*;

public class ProblemSampleDTO {
    private String input;
    private String output;
    private String explanation;
    private Integer sortOrder;

    public ProblemSampleDTO() {
    }

    public ProblemSampleDTO(String input, String output, String explanation, Integer sortOrder) {
        this.input = input;
        this.output = output;
        this.explanation = explanation;
        this.sortOrder = sortOrder;
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
