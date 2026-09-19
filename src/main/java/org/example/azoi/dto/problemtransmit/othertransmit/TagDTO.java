package org.example.azoi.dto.problemtransmit.othertransmit;

import jakarta.persistence.Column;

public class TagDTO {
    private String name;
    private String color;

    public TagDTO() {
    }

    public TagDTO(String name, String color) {
        this.name = name;
        this.color = color;
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
}
