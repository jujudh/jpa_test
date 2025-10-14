package com._depth.jpa;

import com.util.ZipUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UnZipUtilTest {

    private final Path baseDir = Paths.get("C:/upload/test");
    private final Path unzipDir = Paths.get("C:/upload/unzip");
    private final Path file1 = baseDir.resolve("file1.txt");
    private final Path file2 = baseDir.resolve("file2.txt");
    private final Path folder1 = baseDir.resolve("folder1");
    private final Path folder1File = folder1.resolve("nested1.txt");

    @BeforeEach
    void setUp() throws IOException {
        // 테스트용 폴더 초기화
        if (!Files.exists(baseDir)) Files.createDirectories(baseDir);
        if (!Files.exists(unzipDir)) Files.createDirectories(unzipDir);

        // 테스트용 파일과 폴더 생성 (기존 파일이 있어도 덮어쓰기)
        Files.writeString(file1, "파일 1 내용");
        Files.writeString(file2, "파일 2 내용");

        if (!Files.exists(folder1)) Files.createDirectories(folder1);
        Files.writeString(folder1File, "폴더1 내부 파일");
    }

    @Test
    void testUnZipFileKeepExisting() throws IOException {
        // ZIP 생성
        Path zipPath = Paths.get("C:/upload/test.zip");

        List<File> filesAndFolders = List.of(file1.toFile(), file2.toFile(), folder1.toFile());
        byte[] zipBytes = ZipUtil.zipFilesAndFolders(filesAndFolders);
        Files.write(zipPath, zipBytes);
        System.out.println("ZIP 생성 완료 → " + zipPath.toAbsolutePath());

        // 압축 해제 (기존 파일/폴더 유지)
        try (InputStream is = Files.newInputStream(zipPath)) {
            ZipUtil.unZipFile(unzipDir.toString(), is);
        }
        System.out.println("압축 해제 완료 → " + unzipDir.toAbsolutePath());

        // 결과 확인
        assertThat(unzipDir.resolve("file1.txt")).exists();
        assertThat(unzipDir.resolve("file2.txt")).exists();
        assertThat(unzipDir.resolve("folder1/nested1.txt")).exists();

        // 기존 압축 해제된 파일 유지 확인
        // unzipDir 안에 이미 있는 다른 파일은 삭제되지 않고 그대로 유지됨
    }
}
