package com.util;

import io.micrometer.common.lang.Nullable;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 파일 또는 폴더를 ZIP으로 압축 및 해제하는 유틸 클래스
 */
public class ZipUtil {

    //파일 읽고 쓸 때 사용하는 버퍼 크기
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    /**
     * 파일(또는 폴더)을 압축하여 byte 배열로 반환
     *
     * @param sourceFile 압축 대상 파일 또는 폴더
     * @throws RuntimeException 파일 읽기/쓰기 중 오류 발생 시
     */
    // ---------------- 단일 파일/폴더 압축 ----------------
    public static byte[] zipFile(File sourceFile) {
        //ZIP 압축 데이터를 메모리에 바이트 배열로 저장하기 위함.
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             //실제 ZIP 형식으로 데이터 변환.
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            addFileToZip(sourceFile, zos, null);
            
            //ZIP 압축 마무리.
            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("파일 압축 중 오류 발생", e);
        }
    }

    /**
     * 여러 파일 또는 폴더를 단일 ZIP 파일로 압축하여 byte 배열로 반환합니다.
     * 내부적으로 {@link #addFileToZip(File, ZipOutputStream, String)}를 사용하여
     * 각 파일/폴더를 재귀적으로 ZIP 스트림에 추가합니다.
     *
     * @param sources 압축할 파일 또는 폴더 목록
     * @return ZIP 압축 결과가 담긴 byte 배열
     * @throws RuntimeException 파일 읽기/쓰기 중 오류 발생 시
     */
    public static byte[] zipFilesAndFolders(List<File> sources) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            for (File file : sources) {
                addFileToZip(file, zos, null);
            }

            zos.finish();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("파일 압축 중 오류 발생", e);
        }
    }
    /**
     * 단일 파일/폴더를 비밀번호를 걸어서 압축합니다 (Zip4j 사용)
     *
     * @param sourceFile 압축 대상 파일 또는 폴더
     * @param targetZip  생성될 ZIP 파일 경로
     * @param password   설정할 비밀번호
     */
    public static void zipFileWithPassword(File sourceFile, File targetZip, String password) {
        try {
            ZipFile zipFile = new ZipFile(targetZip, password.toCharArray());
            ZipParameters params = new ZipParameters();
            params.setEncryptFiles(true);
            params.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 또는 AES

            if (sourceFile.isDirectory()) {
                zipFile.addFolder(sourceFile, params);
            } else {
                zipFile.addFile(sourceFile, params);
            }
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 압축 중 오류 발생", e);
        }
    }

    /**
     * 여러 파일/폴더를 비밀번호를 걸어서 압축합니다 (Zip4j 사용)
     *
     * @param sources   압축 대상 파일/폴더 목록
     * @param targetZip 생성될 ZIP 파일 경로
     * @param password  설정할 비밀번호
     */
    public static void zipFilesAndFoldersWithPassword(List<File> sources, File targetZip, String password) {
        try {
            ZipFile zipFile = new ZipFile(targetZip, password.toCharArray());
            ZipParameters params = new ZipParameters();
            params.setEncryptFiles(true);
            params.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD); // 또는 AES

            for (File file : sources) {
                if (file.isDirectory()) {
                    zipFile.addFolder(file, params);
                } else {
                    zipFile.addFile(file, params);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("비밀번호 압축 중 오류 발생", e);
        }
    }

    /**
     * 재귀 방식으로 단일 파일 또는 폴더를 ZIP 스트림에 추가합니다.
     * ZIP 내부 경로를 유지하며, 폴더라면 하위 파일/폴더를 재귀적으로 처리합니다.
     * 파일은 버퍼를 이용해 스트림에 기록됩니다.
     *
     * @param file             압축할 파일 또는 폴더
     * @param zos              ZIP 출력 스트림 (ZipOutputStream)
     * @param parentFolderName ZIP 내부 상위 폴더 경로, null이면 최상위로 추가
     * @throws RuntimeException 파일 읽기/쓰기 중 오류 발생 시
     */
    private static void addFileToZip(File file, ZipOutputStream zos, String parentFolderName) {
        /* ZIP 내부 경로 결정 */
        String currentPath = StringUtils.hasText(parentFolderName)
                ? parentFolderName + "/" + file.getName()
                : file.getName();
        /* 파일이 폴더형태라면 폴더 안에 있는 파일/폴더를 재귀적으로 호출. */
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToZip(child, zos, currentPath);
                }
            }
            /* 버퍼 생성하여 ZIP 내부에 실제 파일 데이터를 넣는 부분 */
        } else {
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            try (FileInputStream fis = new FileInputStream(file)) {
                zos.putNextEntry(new ZipEntry(currentPath));

                int length;
                while ((length = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, length);
                }

                zos.closeEntry();
            } catch (IOException e) {
                throw new RuntimeException("파일 압축 중 오류 발생: " + file.getAbsolutePath(), e);
            }
        }
    }

    /**
     * ZIP 파일을 지정된 경로로 압축 해제하는 유틸 메서드
     *
     * 기존 파일/폴더를 삭제하지 않고, ZIP 내 파일과 폴더를 dstPath에 생성합니다.
     * 같은 이름의 파일이 존재하면 덮어쓰기합니다.
     *
     * @param dstPath       압축 해제될 최상위 경로
     * @param inputStream   ZIP 데이터가 담긴 InputStream
     * @throws IOException  파일 읽기/쓰기 오류 발생 시
     */
    public static void unZipFile(String dstPath, InputStream inputStream) throws IOException {
        unZipFile(dstPath, inputStream, null);
    }

    /**
     * ZIP 파일을 지정된 경로로 압축 해제하는 유틸 메서드 (중복 파일 이름 처리 가능)
     *
     * renameDuplicatedFilename이 제공되면, 압축 내 파일 이름이 중복될 때 해당 함수로
     * 새 경로를 결정합니다. null이면 기존 파일 덮어쓰기 방식입니다.
     *
     * @param dstPath                   압축 해제될 최상위 경로
     * @param inputStream               ZIP 데이터가 담긴 InputStream
     * @param renameDuplicatedFilename  중복 파일 이름 처리 함수, null이면 덮어쓰기
     * @throws IOException              파일 읽기/쓰기 오류 발생 시
     */
    private static void unZipFile(String dstPath, InputStream inputStream, @Nullable Function<Path, Path> renameDuplicatedFilename) throws IOException {

        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream, Charset.forName("EUC-KR"))) {
            ZipEntry entry;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

            while ((entry = zipInputStream.getNextEntry()) != null) {
                final String entryName = entry.getName();
                if (entry.isDirectory() || entryName.startsWith("__MACOSX")) continue;

                /* dstPath + entryName → 압축 해제될 실제 파일 경로 */
                File entryFile;
                if (renameDuplicatedFilename == null) {
                    entryFile = new File(dstPath, entryName);
                } else {
                    Path path = Path.of(dstPath, entryName);
                    /* 압축 해제 시 같은 이름의 파일이 이미 존재하면 새 이름을 만들어서 덮어쓰기 방지 */
                    entryFile = renameDuplicatedFilename.apply(path).toFile();
                }
                /* ZIP 내 폴더 구조를 그대로 재현 */
                Files.createDirectories(entryFile.getParentFile().toPath());

                try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(entryFile))) {
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
                zipInputStream.closeEntry();
            }
        }
    }

    /**
     * 비밀번호가 설정된 ZIP 파일을 지정 경로에 압축 해제합니다.
     * 기존 파일/폴더를 삭제하지 않고, 중복 시 덮어씁니다.
     *
     * @param zipFile  압축 파일
     * @param dstPath  압축 해제될 경로
     * @param password ZIP 파일 비밀번호
     * @throws ZipException 압축 해제 중 오류 발생 시
     */
    public static void unZipFileWithPassword(File zipFile, String dstPath, String password) throws ZipException {
        ZipFile zip = new ZipFile(zipFile, password.toCharArray());

        /* ZIP 안에 있는 파일/폴더 정보를 가져온다. */
        List<FileHeader> headers = zip.getFileHeaders();
        for (FileHeader header : headers) {
            if (header.isDirectory()) continue;
            /* 해당 파일을 dsPath 위치로 추출 */
            zip.extractFile(header, dstPath);
        }
    }
}
