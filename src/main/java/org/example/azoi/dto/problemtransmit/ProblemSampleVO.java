package org.example.azoi.dto.problemtransmit;

import org.example.azoi.model.problem_model.ProblemSample;

public class ProblemSampleVO {
    private String input;
    private String output;
    private String explanation;
    private int sort_order;

    public ProblemSampleVO() {
    }

    public ProblemSampleVO(String input, String output, String explanation, int sort_order) {
        this.input = input;
        this.output = output;
        this.explanation = explanation;
        this.sort_order = sort_order;
    }

    public ProblemSampleVO(ProblemSample problemSample) {
        this.input = problemSample.getInput();
        this.output = problemSample.getOutput();
        this.explanation = problemSample.getExplanation();
        this.sort_order = problemSample.getSortOrder();
    }

    public int getSort_order() {
        return sort_order;
    }

    public void setSort_order(int sort_order) {
        this.sort_order = sort_order;
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
}
