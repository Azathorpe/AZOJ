package org.example.azoi.model.problem_model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "problem_files", schema = "azoi")
public class ProblemFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "problem_id", nullable = false)
    private Long problemId;

    @Column(name = "filename", nullable = false, length = 128)
    private String filename;

    /**
     * file类型(后面可扩展为交互题 普通题)
     * 0: 测试点输入 后缀为.in
     * 1: 测试点输出 后缀为.out
     */
    @Column(name = "file_type", nullable = false)
    private Byte fileType;

    /**
     * 这个路径是我们自己选择的
     */
    @Column(name = "storage_path", nullable = false, length = 512)
    private String storagePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "md5", length = 32)
    private String md5;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreated(){
        this.createdAt = Instant.now();
    }

    public static String parseType(Byte fileType){
        return switch (fileType) {
            case 0 -> "in";
            case 1 -> "out";
            default -> "err";
        };
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
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

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}