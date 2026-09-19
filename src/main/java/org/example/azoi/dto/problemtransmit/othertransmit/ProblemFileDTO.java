package org.example.azoi.dto.problemtransmit.othertransmit;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

public class ProblemFileDTO {
    private Byte fileType;
    private MultipartFile file;

    public ProblemFileDTO() {
    }

    public ProblemFileDTO(Byte fileType, MultipartFile file) {
        this.fileType = fileType;
        this.file = file;
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
