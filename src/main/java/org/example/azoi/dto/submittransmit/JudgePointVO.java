package org.example.azoi.dto.submittransmit;

public class JudgePointVO {
    private Integer testPoint;   // 测试点编号
    private Byte status;         // 状态码
    private Integer time;        // ms
    private Integer memory;      // KB
    private String message;      // 错误信息

    public JudgePointVO() {
    }

    public JudgePointVO(Integer testPoint, Byte status, Integer time, Integer memory, String message) {
        this.testPoint = testPoint;
        this.status = status;
        this.time = time;
        this.memory = memory;
        this.message = message;
    }

    public Integer getTestPoint() {
        return testPoint;
    }

    public void setTestPoint(Integer testPoint) {
        this.testPoint = testPoint;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getMemory() {
        return memory;
    }

    public void setMemory(Integer memory) {
        this.memory = memory;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}