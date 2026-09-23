package org.example.azoi.dto.problemtransmit.othertransmit;

import org.example.azoi.model.problem_model.ProblemFile;

/**
 * 这个没什么好返回的，只需要返回状态就可以了
 */
public class ProblemFileVO {
    private Long fileId;
    private String filename;
    private Byte fileType;
    private Long fileSize;
    private String md5;

    public ProblemFileVO() {
    }

    public ProblemFileVO(ProblemFile pf){
        this.fileId = pf.getId();
        this.filename = pf.getFilename();
        this.fileType = pf.getFileType();
        this.fileSize = pf.getFileSize();
        this.md5 = pf.getMd5();
    }

    public ProblemFileVO(Long fileId, String filename, Byte fileType, Long fileSize, String md5) {
        this.fileId = fileId;
        this.filename = filename;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.md5 = md5;
    }

    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Byte getFileType() {
        return fileType;
    }

    public void setFileType(Byte fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}