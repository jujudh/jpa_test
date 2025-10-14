package com._depth.jpa.file;

import com.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;


        public void save(Long id, String title, String content) {
        File file;
        //수정
        if(id != null){
            file  = fileRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid id"));
        } else{ //등록
            file = new File();
        }
            fileRepository.save(file);
    }

    public void saveFiles(Long id,MultipartHttpServletRequest multipartRequest) throws IOException {

        File file;

        // 1️⃣ 기존 파일인지 확인 (업데이트)
        if (id != null) {
            file = fileRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid id"));
        } else {
            // 2️⃣ 새 파일 등록
            file = new File();
        }

        String dir = "C:/upload";
        // 3️⃣ 파일 업로드
        List<FileDto> fileInfoList = FileUtil.uploadFiles(dir,multipartRequest);

        // 단일 파일만 처리한다고 가정
        if (!fileInfoList.isEmpty()) {
            FileDto dto = fileInfoList.get(0);
            file.setFileName(dto.getOriginalName());
            file.setFilePath(dto.getSavedName());
            file.setFileSize(dto.getSize());
            file.setContentType(dto.getContentType());
            file.setUploadDate(dto.getUploadDate());

            file.setFileValue(dto.getSavedName());
        }

        // 4️⃣ DB 저장 (새 파일이면 insert, 기존이면 update)
        fileRepository.save(file);
    }

}
