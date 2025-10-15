package com.util;

import com._depth.jpa.file.FileDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;

public class FileUtil {
    /**
     * MultipartHttpServletRequest를 통해 업로드된 파일들을 서버에 저장하고,
     * 파일 정보를 FileDto 리스트로 반환하는 메서드.
     *
     * 업로드 파일은 날짜별 폴더(yyyyMMdd) 아래에 저장되며,
     * 각 파일에 대해 FileDto 객체를 생성하여 파일명, 경로, 크기, MIME 타입 등을 담습니다.</p>
     *
     * @param request 업로드된 파일들을 포함하는 MultipartHttpServletRequest
     * @return 업로드된 파일들의 메타데이터를 담은 FileDto 객체 리스트
     * @throws IOException 파일 저장 중 문제가 발생하면 IOException 발생
     */
    public static List<FileDto> uploadFiles(String uploadDirectory,
            MultipartHttpServletRequest request
    ) throws IOException {

        List<FileDto> fileInfoList = new ArrayList<>();

        // 날짜별 폴더 생성
        String datePath = LocalDate.now().toString().replace("-", "");
        File uploadDir = new File(uploadDirectory, datePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Iterator<String> fileNames = request.getFileNames();

        while (fileNames.hasNext()) {
            String inputName = fileNames.next();
            List<MultipartFile> files = request.getFiles(inputName);

            for (MultipartFile file : files) {
                FileDto fileInfo = getFileDto(uploadDirectory,file, datePath, inputName);
                if (fileInfo == null) continue;

                fileInfoList.add(fileInfo);
            }
        }

        return fileInfoList;
    }

    /**
     * 단일 MultipartFile을 서버에 저장하고, 해당 파일의 정보를 FileDto 객체로 반환하는 메서드.
     *
     * 업로드 파일은 날짜별 폴더(yyyyMMdd) 아래에 저장되며,
     * 저장 경로, 원본 파일명, 파일 크기, MIME 타입, 업로드 날짜 등을 FileDto에 담아 반환합니다.
     *
     * @param uploadDirectory 서버의 최상위 업로드 디렉토리 경로
     * @param file 업로드된 MultipartFile 객체
     * @param datePath 날짜 기반 폴더명 (yyyyMMdd)
     * @param inputName 폼에서 사용된 input 필드명
     * @return 업로드된 파일 정보를 담은 FileDto 객체, 파일이 비어있거나 파일명이 없으면 null
     * @throws IOException 파일 저장 중 오류가 발생할 경우 발생
     */
    private static FileDto getFileDto(String uploadDirectory,MultipartFile file, String datePath, String inputName) throws IOException {
        if (file.isEmpty()) return null;

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) return null;

/*
        if (file.getSize() > maxFileSize) {
            throw new IOException("파일 용량은 " + (maxFileSize / (1024 * 1024)) + "MB 이하만 가능합니다.");
        }*/

        // 파일 저장
        String newFileName = generateUniqueFileName(originalFileName);
        Path filePath = Paths.get(uploadDirectory, datePath, newFileName);
        file.transferTo(filePath.toFile());

        String relativeFilePath = datePath + "/" + newFileName;

        FileDto fileInfo = new FileDto(
                inputName,
                originalFileName,
                relativeFilePath,
                filePath.toString(),
                file.getSize(),
                file.getContentType(),
                LocalDate.now()
        );
        return fileInfo;
    }

    /**
     * 원본 파일명을 기반으로 고유한 파일명을 생성하는 메서드.
     *
     * UUID를 사용하여 중복 가능성을 최소화하며, 원본 파일의 확장자는 그대로 유지합니다.
     *
     * @param originalFileName 원본 파일명 (예: "image.jpg")
     * @return 중복되지 않는 고유 파일명 (예: "a3f1b2c4d5e6f7g8h9i0.jpg")
     */
    private static String generateUniqueFileName(String originalFileName) {
        String ext = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            ext = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }


    /**
     * 서버에 저장된 파일을 HTTP 응답으로 전송하여 클라이언트가 다운로드할 수 있도록 처리하는 메서드.
     *
     * 업로드 디렉토리와 DB에 저장된 상대 경로를 결합하여 실제 파일 경로를 확인하고,
     * 파일 존재 여부, 접근 권한 등을 검증한 후 파일 스트림을 통해 전송합니다.
     *
     * 한글 파일명 깨짐 방지를 위해 URL 인코딩 처리하며, MIME 타입은 자동으로 지정합니다.
     * MIME 타입이 확인되지 않으면 "application/octet-stream"으로 설정합니다.
     *
     * @param uploadDirectory 서버에 파일이 저장된 최상위 디렉토리 경로
     * @param relativeFilePath DB에 저장된 파일의 상대 경로
     * @param response HttpServletResponse 객체, 파일을 출력 스트림으로 전송
     * @throws IOException 파일 읽기/쓰기 또는 스트림 처리 중 문제가 발생하면 IOException 발생
     */
    public static void downloadFile(String uploadDirectory,String relativeFilePath, HttpServletResponse response) throws IOException {
        //uploadDirectory와 db에 저장된 파일 경로를 결합.
        Path filePath = Paths.get(uploadDirectory).resolve(relativeFilePath).normalize();
        File file = filePath.toFile();
        
        //해당 파일경로가 uploadDirectory 하위에 위치하고 있는지 확인
        if (!filePath.startsWith(Paths.get(uploadDirectory).toAbsolutePath())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "잘못된 접근입니다.");
            return;
        }

        // 파일 존재 확인
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일이 존재하지 않습니다.");
            return;
        }

        // MIME 타입 자동 지정
        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        // 한글 파일명 깨짐 방지
        String encodedFileName = URLEncoder.encode(file.getName(), "UTF-8")
                .replaceAll("\\+", "%20");

        // 응답 헤더 설정
        response.setContentType(contentType);
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + encodedFileName + "\"");
        response.setContentLengthLong(file.length());

        // 파일 스트림 전송
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream())) {

            byte[] buffer = new byte[8192]; // 8KB 버퍼
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}