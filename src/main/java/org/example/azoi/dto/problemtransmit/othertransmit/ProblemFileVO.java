package org.example.azoi.dto.problemtransmit.othertransmit;

/**
 * 这个没什么好返回的，只需要返回状态就可以了
 */
public class ProblemFileVO {
    public static final int SUCCESS = 1;
    public static final int FAIL = -1;

    private String filename;
    private Byte fileType;
    private int code;
    private String msg;

    public ProblemFileVO() {
    }

    public ProblemFileVO(String filename, Byte fileType, int code, String msg) {
        this.filename = filename;
        this.fileType = fileType;
        this.code = code;
        this.msg = msg;
    }

    public String getFilename() {
        return filename;
    }

    public Byte getFileType() {
        return fileType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
