package com._depth.jpa.file;

import com.util.FileUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileRepository fileRepository;

    @GetMapping("/file-list")
    String list(Model model) {
        List<File> files = fileRepository.findAll();
        model.addAttribute("files", files);
        return "file/fileList.html";
    }

    @GetMapping("/file-detail/{id}")
    String detail(@PathVariable Long id, Model model) {
        Optional<File> detail = fileRepository.findById(id);
        model.addAttribute("detail", detail.get());
        return "file/fileDetail.html";
    }

    @GetMapping("/file-insertForm")
    String insertForm(Model model) {

        return "file/fileInsertForm.html";
    }

    @PostMapping("/file-insert")
    String insert(MultipartHttpServletRequest multipartRequest) throws IOException {

        //List<FileDto> fileInfoList = fileUtil.uploadFiles(multipartRequest);

        fileService.saveFiles(null,multipartRequest);
        return "redirect:/file-list";
    }

    @GetMapping("/file-download/{id}")
    public void downloadFile(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<File> file = fileRepository.findById(id);

        FileUtil.downloadFile("C:/upload",file.get().getFilePath(), response);
    }
}
