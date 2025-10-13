package com._depth.jpa.file;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FileService {

    private final FileInfoRepository fileInfoRepository;

    public FileService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @Transactional
    public void saveFiles(List<FileDto> fileDtos) {
        if (fileDtos == null) return;
        for (FileDto dto : fileDtos) {
            fileInfoRepository.save(dto.toEntity());
        }
    }

    public FileInfo getFileInfo(Long id) {
        return fileInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일 없음"));
    }
}
