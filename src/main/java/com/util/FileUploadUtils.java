package com.util;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class FileUploadUtils {

    /**
     * MultipartHttpServletRequest에서 업로드된 파일을 처리하고
     * 파일 정보를 List<Map<String,Object>> 형태로 반환
     */
    public static List<Map<String, Object>> uploadFiles(
            MultipartHttpServletRequest request,
            String uploadRoot
    ) throws IOException {

        List<Map<String, Object>> fileInfoList = new ArrayList<>();

        // 날짜별 폴더 생성 예: /upload/2025/10/01
        String datePath = LocalDate.now().toString().replace("-", File.separator);
        File uploadDir = new File(uploadRoot, datePath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs(); // ✅ File + mkdirs() 방식
        }

        Iterator<String> fileNames = request.getFileNames();

        while (fileNames.hasNext()) {
            String inputName = fileNames.next();
            List<MultipartFile> files = request.getFiles(inputName);

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                // 원본 파일명 & 저장 파일명
                String originalName = file.getOriginalFilename();
                String savedName = UUID.randomUUID().toString().replace("-", "");
                if (originalName != null && originalName.contains(".")) {
                    savedName += originalName.substring(originalName.lastIndexOf("."));
                }

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