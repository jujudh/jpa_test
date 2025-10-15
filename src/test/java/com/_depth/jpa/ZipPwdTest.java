package com._depth.jpa;

import com.util.ZipUtil;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZipPwdTest {
    @Test
    void testZipAndUnzipSingleFile() throws Exception {
        File source = new File("C:/upload/test1.txt");
        File zip = new File("C:/upload/test.zip");

        // 단일 파일 압축
        byte[] bytes = ZipUtil.zipFile(source);
        Files.write(zip.toPath(), bytes);
        assertTrue(zip.exists());

        // 압축 해제
        ZipUtil.unZipFile("C:/upload/unzip-single", new FileInputStream(zip));
        assertTrue(Files.exists(Path.of("C:/upload/unzip-single/test1.txt")));
        System.out.println("단일 파일 압축/해제 완료");
    }

    @Test
    void testZipAndUnzipWithPassword() throws Exception {
        File source = new File("C:/upload/test-dir");
        File zip = new File("C:/upload/test-zip.zip");

        // 비밀번호 압축
        ZipUtil.zipFileWithPassword(source, zip, "1234");
        assertTrue(zip.exists());
        System.out.println("비밀번호 ZIP 압축완료");

        // 비밀번호 압축 해제
        ZipUtil.unZipFileWithPassword(zip, "C:/upload/unzip-protected", "1234");
        //assertTrue(Files.exists(Path.of("C:/upload/unzip-protected/test-dir")));
        System.out.println("비밀번호 ZIP 해제 완료");
    }

    @Test
    void testZipMultipleFilesWithPassword() throws Exception {
        List<File> files = List.of(
                new File("C:/upload/a.txt"),
                new File("C:/upload/b.txt"),
                new File("C:/upload/folder1")
        );
        File zip = new File("C:/upload/multi-protected.zip");

        // 여러 파일/폴더 비밀번호 압축
        ZipUtil.zipFilesAndFoldersWithPassword(files, zip, "abcd");
        assertTrue(zip.exists());
        System.out.println("여러 파일/폴더 비밀번호 ZIP 압축 완료");

        // 비밀번호 압축 해제
        ZipUtil.unZipFileWithPassword(zip, "C:/upload/unzip-multi", "abcd");
        assertTrue(Files.exists(Path.of("C:/upload/unzip-multi/a.txt")));
        System.out.println("여러 파일/폴더 비밀번호 ZIP 해제 완료");
    }

}
