package org.example.azoi.dto.problemtransmit;

import org.example.azoi.dto.problemtransmit.othertransmit.ProblemFileDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemSampleDTO;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemTagDTO;
import org.example.azoi.model.problem_model.ProblemFile;
import org.example.azoi.model.problem_model.ProblemSample;
import org.example.azoi.model.problem_model.ProblemTag;

import java.util.ArrayList;
import java.util.List;

public class ProblemCreateDTO {
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 输入格式
     */
    private String inputFormat;
    /**
     * 输出格式
     */
    private String outputFormat;
    /**
     * 提示
     */
    private String hint;
    /**
     * 难度
     */
    private Byte difficulty;
    /**
     * 时间限制
     */
    private Integer timeLimit;
    /**
     * 内存限制
     */
    private Integer memoryLimit;
    /**
     * 输出限制
     */
    private Integer outputLimit;
    /**
     * 判题类型
     */
    private Byte judgeType;
    /**
     * 是否可见？
     */
    private Boolean isVisible;
    /**
     * 作者Id
     */
    private Long createdBy = -1L;

    /**
     * 样例
     */
    private List<ProblemSampleDTO> samples = new ArrayList<>();

    /**
     * 题目标签
     */
    private List<ProblemTagDTO> problemTags = new ArrayList<>();

    public ProblemCreateDTO() {
    }

    public ProblemCreateDTO(String title, String description, String inputFormat, String outputFormat, String hint, Byte difficulty, Integer timeLimit, Integer memoryLimit, Integer outputLimit, Byte judgeType, Boolean isVisible, Long createdBy, List<ProblemSampleDTO> samples, List<ProblemTagDTO> problemTags) {
        this.title = title;
        this.description = description;
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
        this.hint = hint;
        this.difficulty = difficulty;
        this.timeLimit = timeLimit;
        this.memoryLimit = memoryLimit;
        this.outputLimit = outputLimit;
        this.judgeType = judgeType;
        this.isVisible = isVisible;
        this.createdBy = createdBy;
        this.samples = samples;
        this.problemTags = problemTags;
    }

    public List<ProblemTagDTO> getProblemTags() {
        return problemTags;
    }

    public void setProblemTags(List<ProblemTagDTO> problemTags) {
        this.problemTags = problemTags;
    }

    public List<ProblemSampleDTO> getSamples() {
        return samples;
    }

    public void setSamples(List<ProblemSampleDTO> samples) {
        this.samples = samples;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(String inputFormat) {
        this.inputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public Byte getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Byte difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public Integer getMemoryLimit() {
        return memoryLimit;
    }

    public void setMemoryLimit(Integer memoryLimit) {
        this.memoryLimit = memoryLimit;
    }

    public Integer getOutputLimit() {
        return outputLimit;
    }

    public void setOutputLimit(Integer outputLimit) {
        this.outputLimit = outputLimit;
    }

    public Byte getJudgeType() {
        return judgeType;
    }

    public void setJudgeType(Byte judgeType) {
        this.judgeType = judgeType;
    }

    public Boolean getVisible() {
        return isVisible;
    }

    public void setVisible(Boolean visible) {
        isVisible = visible;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
