package org.example.azoi.dto.problemtransmit.othertransmit;

import jakarta.persistence.Column;
import org.example.azoi.model.problem_model.Tag;

import java.time.Instant;

public class TagVO {
    private Long tagId;
    private String name;
    private String color;
    private Instant createdAt;

    public TagVO() {
    }

    public TagVO(Tag tag){
        this.tagId = tag.getId();
        this.name = tag.getName();
        this.color = tag.getColor();
        this.createdAt = tag.getCreatedAt();
    }

    public TagVO(Long tagId, String name, String color, Instant createdAt) {
        this.tagId = tagId;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
