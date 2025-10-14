package com._depth.jpa;


import com.util.ZipUtil;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class ZipUtilTest {

    @Test
    void directoryZippingTest() throws IOException {
        // given
        final Path file1 = Path.of("C:/upload/file1.txt");
        final Path file2 = Path.of("C:/upload/file2.txt");
        final Path zipPath = Path.of("C:/upload/test.zip");

        // 테스트를 위해 임시 파일 생성 (없으면)
        if (!Files.exists(file1)) {
            Files.writeString(file1, "테스트 파일 1 내용입니다.");
        }
        if (!Files.exists(file2)) {
            Files.writeString(file2, "테스트 파일 2 내용입니다.");
        }

        // 기존 ZIP 파일 삭제
        Files.deleteIfExists(zipPath);

        // when (두 파일을 압축)
        try (ZipOutputStream zipOut = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            for (Path file : List.of(file1, file2)) {
                try (FileInputStream fis = new FileInputStream(file.toFile())) {
                    ZipEntry zipEntry = new ZipEntry(file.getFileName().toString());
                    zipOut.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) >= 0) {
                        zipOut.write(buffer, 0, length);
                    }
                    zipOut.closeEntry();
                }
            }
        }

        System.out.println("압축 완료 → " + zipPath.toAbsolutePath());

        // then (결과 확인)
        assertThat(zipPath).exists();
        assertThat(Files.size(zipPath)).isGreaterThan(0);

/*        // when
        byte[] bytes = ZipUtil.zipFile(zipFilePath.toFile());
        Files.write(testZipPath, bytes);
        System.out.println("완료");

        // then
        assertThat(testZipPath).exists();


        byte[] zipBytes = ZipUtil.zipFile(zipFilePath.toFile());
        Files.write(testZipPath, zipBytes); // 실제 ZIP 파일 생성
        System.out.println("압축 완료 → " + testZipPath.toAbsolutePath());

        // then (결과 확인)
        assertThat(testZipPath).exists();
        assertThat(Files.size(testZipPath)).isGreaterThan(0);*/
    }

    @Test
    void directoryZippingTest2() throws IOException {
        // given
        final Path zipFilePath = Path.of("C:/upload/test");
        final Path testZipPath = Path.of("C:/upload/test2.zip");


        // when
        byte[] bytes = ZipUtil.zipFile(zipFilePath.toFile());
        Files.write(testZipPath, bytes);
        System.out.println("완료");

        // then
        assertThat(testZipPath).exists();


        byte[] zipBytes = ZipUtil.zipFile(zipFilePath.toFile());
        Files.write(testZipPath, zipBytes); // 실제 ZIP 파일 생성
        System.out.println("압축 완료 → " + testZipPath.toAbsolutePath());

        // then (결과 확인)
        assertThat(testZipPath).exists();
        assertThat(Files.size(testZipPath)).isGreaterThan(0);
    }

}
