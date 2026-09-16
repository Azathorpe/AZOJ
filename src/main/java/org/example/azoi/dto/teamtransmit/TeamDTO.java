package org.example.azoi.dto.teamtransmit;


/**
 * 用于传输团队信息的DTO类
 * 包含Team信息和创建者
 */
public class TeamDTO {
    private String name;
    private String description;
    private Byte type;

    Long ownerId;


    public TeamDTO() {
    }

    public TeamDTO(String name, String description, Byte type, Long ownerId) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.ownerId = ownerId;
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

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}
