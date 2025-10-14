package com._depth.jpa;

import com.util.ZipUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ZipUtilTests {

    private final Path baseDir = Paths.get("C:/upload/test");
    private final Path file1 = baseDir.resolve("file1.txt");
    private final Path file2 = baseDir.resolve("file2.txt");
    private final Path folder1 = baseDir.resolve("folder1");
    private final Path folder2 = baseDir.resolve("folder2");
    private final Path folder1File = folder1.resolve("nested1.txt");
    private final Path folder2File = folder2.resolve("nested2.txt");

    @BeforeEach
    void setUp() throws IOException {
        // 기존 테스트 폴더가 있으면 삭제
        if (Files.exists(baseDir)) {
            deleteRecursively(baseDir);
        }

        // 테스트용 폴더 생성
        Files.createDirectories(baseDir);

        // 파일 생성
        Files.writeString(file1, "파일 1 내용");
        Files.writeString(file2, "파일 2 내용");

        // 폴더 생성 및 내부 파일 생성
        Files.createDirectories(folder1);
        Files.createDirectories(folder2);
        Files.writeString(folder1File, "폴더1 내부 파일");
        Files.writeString(folder2File, "폴더2 내부 파일");
    }

    @AfterEach
    void tearDown() throws IOException {
        // 테스트 후 baseDir 삭제 (압축 ZIP 파일은 남겨둠)
/*        if (Files.exists(baseDir)) {
            deleteRecursively(baseDir);
        }*/
    }

    private void deleteRecursively(Path path) throws IOException {
        Files.walk(path)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    // ---------------- 테스트 : 파일 여러 개 압축 ----------------
    @Test
    void testMultipleFilesCompression() throws IOException {
        Path zipPath = Paths.get("C:/upload/files.zip");  // 실제 폴더에 생성
        Files.deleteIfExists(zipPath);

        List<File> files = List.of(file1.toFile(), file2.toFile());
        byte[] zipBytes = ZipUtil.zipFilesAndFolders(files);
        Files.write(zipPath, zipBytes);

        System.out.println("압축 완료 → " + zipPath.toAbsolutePath());

        assertThat(zipPath).exists();
        assertThat(Files.size(zipPath)).isGreaterThan(0);
    }

    // ---------------- 테스트 : 폴더 여러 개 압축 ----------------
    @Test
    void testMultipleFoldersCompression() throws IOException {
        Path zipPath = Paths.get("C:/upload/folders.zip");  // 실제 폴더에 생성
        Files.deleteIfExists(zipPath);

        List<File> folders = List.of(folder1.toFile(), folder2.toFile());
        byte[] zipBytes = ZipUtil.zipFilesAndFolders(folders);
        Files.write(zipPath, zipBytes);

        System.out.println("압축 완료 → " + zipPath.toAbsolutePath());

        assertThat(zipPath).exists();
        assertThat(Files.size(zipPath)).isGreaterThan(0);
    }

    // ---------------- 테스트 : 파일 + 폴더 같이 압축 ----------------
    @Test
    void testFilesAndFoldersCompression() throws IOException {
        Path zipPath = Paths.get("C:/upload/mixed.zip");  // 실제 폴더에 생성
        Files.deleteIfExists(zipPath);

        List<File> mixed = List.of(file1.toFile(), folder2.toFile());
        byte[] zipBytes = ZipUtil.zipFilesAndFolders(mixed);
        Files.write(zipPath, zipBytes);

        System.out.println("압축 완료 → " + zipPath.toAbsolutePath());

        assertThat(zipPath).exists();
        assertThat(Files.size(zipPath)).isGreaterThan(0);
    }


}
