package com._depth.jpa.file;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDto {

    private String inputName;
    private String originalName;
    private String savedName;
    private String savedPath;
    private long size;
    private String contentType;
    private LocalDate uploadDate;


    public FileInfo toEntity() {
        FileInfo entity = new FileInfo();
        entity.setFileName(this.getOriginalName());
        entity.setFilePath(this.getSavedName()); // 상대경로
        entity.setFileSize(this.getSize());
        entity.setContentType(this.getContentType());
        entity.setUploadDate(this.getUploadDate());
        return entity;
    }

}


