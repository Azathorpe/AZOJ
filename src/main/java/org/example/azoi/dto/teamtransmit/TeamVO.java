package org.example.azoi.dto.teamtransmit;

import org.example.azoi.dto.usertransmit.UserInfoVO;
import org.example.azoi.dto.usertransmit.UserSimpleInfoVO;
import org.example.azoi.model.team_model.Team;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class TeamVO {
    private String name;
    private String description;
    private Byte type;
    private Instant createdAt;

    private UserInfoVO creatorInfo;
    private List<UserSimpleInfoVO> members = new ArrayList<>();

    public TeamVO() {
    }

    public TeamVO(String name, String description, UserInfoVO creatorInfo, Byte type, Instant createdAt, List<UserSimpleInfoVO> members) {
        this.name = name;
        this.description = description;
        this.creatorInfo = creatorInfo;
        this.type = type;
        this.createdAt = createdAt;
        this.members = members;
    }

    public void setTeamInfo(Team team){
        this.name = team.getName();
        this.description = team.getDescription();
        this.type = team.getType();
        this.createdAt = team.getCreatedAt();
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

    public UserInfoVO getCreatorInfo() {
        return creatorInfo;
    }

    public void setCreatorInfo(UserInfoVO creatorInfo) {
        this.creatorInfo = creatorInfo;
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

    public List<UserSimpleInfoVO> getMembers() {
        return members;
    }

    public void setMembers(List<UserSimpleInfoVO> members) {
        this.members = members;
    }
}
