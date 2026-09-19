package org.example.azoi.model.problem_model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "tags", schema = "azoi")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    private String name;

    @Column(name = "color", length = 16)
    private String color;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreated() {
        createdAt = Instant.now();
    }

    public Tag() {
    }

    public Tag(Long id, String name, String color, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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