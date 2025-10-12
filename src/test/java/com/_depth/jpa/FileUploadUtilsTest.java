package com._depth.jpa;


import com.util.FileUploadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FileUploadUtilsTest {

    @Test
    void testFileUpload() throws IOException {
        // given
        String uploadRoot = "C:/upload-test"; // ✅ 테스트용 폴더 (임시)
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();

        // 가짜 파일 생성 (MockMultipartFile)
        MockMultipartFile mockFile = new MockMultipartFile(
                "img_file",                     // input name
                "example.txt",                  // 원본 파일명
                "text/plain",                   // MIME 타입
                "This is a test file.".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        // request에 파일 추가
        request.addFile(mockFile);

        // when
        List<Map<String, Object>> result = FileUploadUtils.uploadFiles(request, uploadRoot);

        // then
        assertThat(result).isNotEmpty();
        Map<String, Object> fileInfo = result.get(0);

        // 실제 파일이 생성되었는지 확인
        File savedFile = new File((String) fileInfo.get("savedPath"));
        assertThat(savedFile.exists()).isTrue();
        assertThat(savedFile.length()).isGreaterThan(0);

        // 파일명/타입 등 검증
        assertThat(fileInfo.get("originalName")).isEqualTo("example.txt");
        assertThat(fileInfo.get("contentType")).isEqualTo("text/plain");

        // ✅ 테스트 끝난 후 파일/폴더 정리 (옵션)
        savedFile.delete();
        savedFile.getParentFile().delete(); // 20251010 폴더
    }
}

