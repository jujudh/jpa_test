package com._depth.jpa.file;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "file_value")
    private String fileValue;        // 업로드 당시 원본 파일명

    @Column(name = "file_name", nullable = false)
    private String fileName;        // 업로드 당시 원본 파일명

    @Column(name = "file_path", nullable = false)
    private String filePath;        // 서버 내부 저장 경로 (상대경로)

    @Column(name = "file_size", nullable = false)
    private Long fileSize;          // 파일 크기

    @Column(name = "content_type", nullable = false)
    private String contentType;     // MIME 타입

    @Column(name = "upload_date", nullable = false)
    private LocalDate uploadDate;   // 업로드 날짜
}
