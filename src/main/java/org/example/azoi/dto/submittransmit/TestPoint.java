package org.example.azoi.dto.submittransmit;

import org.example.azoi.model.Submission;

public class TestPoint {
    //AC or WA
    private byte status;
    private String judgeLog;

    public TestPoint() {
    }

    public TestPoint(byte status, String judgeLog) {
        this.status = status;
        this.judgeLog = judgeLog;
    }

    @Override
    public String toString() {
        String stat = Submission.parseStatus(status);
        return "This Point is: " + stat + " Reason: " + judgeLog;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getJudgeLog() {
        return judgeLog;
    }

    public void setJudgeLog(String judgeLog) {
        this.judgeLog = judgeLog;
    }
}
