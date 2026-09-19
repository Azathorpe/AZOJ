package org.example.azoi.model.team_model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "teams", schema = "azoi")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    //0: 团队 1:班级
    @Column(name = "type", nullable = false)
    private Byte type;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public Team() {
    }

    public Team(Long id, String name, String description, Long ownerId, Byte type, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.type = type;
        this.createdAt = createdAt;
    }

    @PrePersist
    public void onCreated(){
        this.createdAt = Instant.now();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}