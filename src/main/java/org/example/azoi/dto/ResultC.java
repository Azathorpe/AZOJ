package org.example.azoi.dto;

/**
 * 编译时单独用的Result交换
 */
public class ResultC {
    String ans;
    String log;
    Byte status;

    public ResultC() {
    }

    public ResultC(String ans, String log, Byte status) {
        this.ans = ans;
        this.log = log;
        this.status = status;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }
}
