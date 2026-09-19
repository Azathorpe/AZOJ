package org.example.azoi.dto.problemtransmit.othertransmit;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

public class ProblemFileDTO {
    private Long problemId;
    private Byte fileType;
    private MultipartFile file;

    public ProblemFileDTO() {
    }

    public ProblemFileDTO(Long problemId, Byte fileType, MultipartFile file) {
        this.problemId = problemId;
        this.fileType = fileType;
        this.file = file;
    }

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public Byte getFileType() {
        return fileType;
    }

    public void setFileType(Byte fileType) {
        this.fileType = fileType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
