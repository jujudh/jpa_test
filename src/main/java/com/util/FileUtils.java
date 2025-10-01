package com.util;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileUtils {

    /**
     * MultipartRequest를 받아서 파일들을 지정 경로에 저장하고
     * 파일 정보 리스트를 반환하는 유틸 메서드
     */
    public static List<Map<String, Object>> uploadFiles(
            MultipartHttpServletRequest multipartRequest,
            String uploadDirPath
    ) throws IOException {

        List<Map<String, Object>> fileInfoList = new ArrayList<>();

        // 날짜별 폴더 생성 예: /uploads/2025/10/01
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        File uploadDir = new File(uploadDirPath, datePath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        Iterator<String> fileNames = multipartRequest.getFileNames();

        while (fileNames.hasNext()) {
            String inputName = fileNames.next();  // form input name
            List<MultipartFile> files = multipartRequest.getFiles(inputName);

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // 원본 파일명 & 저장 파일명
                String originalName = file.getOriginalFilename();
                String extension = "";
                if (originalName != null && originalName.contains(".")) {
                    extension = originalName.substring(originalName.lastIndexOf("."));
                }
                String savedName = UUID.randomUUID().toString().replace("-", "") + extension;

                // 실제 저장
                File dest = new File(uploadDir, savedName);
                file.transferTo(dest);

                // 파일 정보 저장
                Map<String, Object> fileInfo = new HashMap<>();
                fileInfo.put("inputName", inputName);
                fileInfo.put("originalName", originalName);
                fileInfo.put("savedName", savedName);
                fileInfo.put("savedPath", dest.getAbsolutePath());
                fileInfo.put("size", file.getSize());
                fileInfo.put("contentType", file.getContentType());
                fileInfo.put("uploadDate", LocalDate.now());

                fileInfoList.add(fileInfo);
            }
        }

        return fileInfoList;
    }

}
