package org.example.azoi.model.problem_model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "problem_tags", schema = "azoi")
public class ProblemTag {
    @EmbeddedId
    private ProblemTagId id;

    public ProblemTagId getId() {
        return id;
    }

    public void setId(ProblemTagId id) {
        this.id = id;
    }

    //TODO [逆向工程] 从数据库生成列
}