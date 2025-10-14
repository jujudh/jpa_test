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

    private static String generateUniqueFileName(String originalFileName) {
        String ext = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex != -1) {
            ext = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString().replace("-", "") + ext;
    }


    /**
     * 파일 다운로드
     */
    public static void downloadFile(String uploadDirectory,String relativeFilePath, HttpServletResponse response) throws IOException {
        //uploadDirectory와 db에 저장된 파일 경로를 결합.
        Path filePath = Paths.get(uploadDirectory).resolve(relativeFilePath).normalize();
        File file = filePath.toFile();
        System.out.println("filepath = " + file.getAbsolutePath());
        
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