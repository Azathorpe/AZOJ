package org.example.azoi.model.problem_model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.example.azoi.dto.problemtransmit.othertransmit.ProblemTagDTO;

@Entity
@Table(name = "problem_tags", schema = "azoi")
public class ProblemTag {
    @EmbeddedId
    private ProblemTagId id;

    public ProblemTag() {
    }

    public ProblemTag(ProblemTagDTO problemTagDTO){
        this.id = new ProblemTagId(problemTagDTO.getProblemId(), problemTagDTO.getTagId());
    }

    public ProblemTag(Long problemId, Long tagId){
        this.id = new ProblemTagId(problemId, tagId);
    }

    public ProblemTag(ProblemTagId id) {
        this.id = id;
    }

    public ProblemTagId getId() {
        return id;
    }

    public void setId(ProblemTagId id) {
        this.id = id;
    }

    //TODO [逆向工程] 从数据库生成列
}