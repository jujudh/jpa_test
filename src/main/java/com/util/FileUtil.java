package com.util;

import com._depth.jpa.file.FileDto;
import com._depth.jpa.file.FileInfo;
import com._depth.jpa.file.FileInfoRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
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

@Component("FileUtil")
public class FileUtil {


    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @Value("${file.max-size}")
    private Long maxFileSize;

    private final FileInfoRepository fileInfoRepository;

    public FileUtil(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    /**
     * 파일 업로드
     * 파일 정보를 List<Map<String,Object>> 형태로 반환
     */
    public List<FileDto> uploadFiles(MultipartHttpServletRequest request) throws IOException {
        List<FileDto> fileInfoList = new ArrayList<>();
        String datePath = LocalDate.now().toString().replace("-", "");
        File uploadDir = new File(uploadDirectory, datePath);
        if (!uploadDir.exists()) uploadDir.mkdirs();

        Iterator<String> fileNames = request.getFileNames();
        while (fileNames.hasNext()) {
            String inputName = fileNames.next();
            List<MultipartFile> files = request.getFiles(inputName);

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String originalFileName = file.getOriginalFilename();
                if (originalFileName == null) continue;

                if (file.getSize() > maxFileSize) {
                    throw new IOException("파일 용량 초과");
                }

                String newFileName = UUID.randomUUID().toString().replace("-", "")
                        + originalFileName.substring(originalFileName.lastIndexOf("."));
                Path filePath = Paths.get(uploadDirectory, datePath, newFileName);
                file.transferTo(filePath.toFile());
                String relativeFilePath = datePath + "/" + newFileName;

                FileDto fileDto = new FileDto();
                fileDto.setInputName(inputName);
                fileDto.setOriginalName(originalFileName);
                fileDto.setSavedName(relativeFilePath);
                fileDto.setSavedPath(filePath.toString());
                fileDto.setSize(file.getSize());
                fileDto.setContentType(file.getContentType());
                fileDto.setUploadDate(LocalDate.now());

                fileInfoList.add(fileDto);
            }
        }
        return fileInfoList;
    }


    private String generateUniqueFileName(String originalFileName) {
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
    public void downloadFile(FileInfo fileInfo, HttpServletResponse response) throws IOException {
        // DB에서 저장된 상대경로
        Path filePath = Paths.get(uploadDirectory).resolve(fileInfo.getFilePath()).normalize();
        File file = filePath.toFile();

        // 파일 존재 확인
        if (!file.exists() || !file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "파일이 존재하지 않습니다.");
            return;
        }

        // DB에서 가져온 contentType 사용
        String contentType = fileInfo.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream"; // fallback
        }

        // 한글 파일명 깨짐 방지 (원본 파일명 사용)
        String encodedFileName = URLEncoder.encode(fileInfo.getFileName(), "UTF-8")
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