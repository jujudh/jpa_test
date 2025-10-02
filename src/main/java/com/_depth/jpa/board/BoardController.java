package com._depth.jpa.board;


import com.util.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BoardController {

    public record BoardReqeust(String id, String comment, LocalDateTime createdAt){};

    private final BoardRepository boardRepository;

    private final BoardService boardService;


/*    @GetMapping("/list")
    String list(Model model) {
        //model.addAttribute("boards", boardRepository.findAll());
        List<Board> boards = boardRepository.findAll();
        model.addAttribute("boards", boards);
        return "board/list.html";
    }*/

    @GetMapping("/list")
    String list(@ModelAttribute BoardSearchDto searchDto, Model model) {
        List<BoardDto> boards = boardService.search(searchDto);
        model.addAttribute("boards", boards);
        model.addAttribute("searchDto", searchDto);
        return "board/list.html";
    }

/*    @GetMapping("/list")
    public String list(@RequestParam(defaultValue = "") String title,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<BoardResponseDto> boards = boardService.getBoards(title, pageable);

        model.addAttribute("boards", boards.getContent());
        System.out.println("boards: " + boards.getContent());
        model.addAttribute("page", boards);

        return "board/list.html";
    }*/

    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, Model model) {
        //var detail = boardRepository.findById(id);
        Optional<Board> detail = boardRepository.findById(id);
        System.out.println("detail = " + detail);
        System.out.println("detail = " + detail.get());
        System.out.println("detail = " + detail.get().title);

        model.addAttribute("detail", detail.get());
        return "board/detail.html";
    }

    @GetMapping("/insertForm")
    String insertForm(Model model) {

        return "board/insertForm.html";
    }

    @PostMapping("/insert")
    String insert(@RequestParam String title, @RequestParam String content,
                  MultipartHttpServletRequest multipartRequest) throws IOException {

        // 파일 업로드 실행
        List<Map<String, Object>> fileInfoList = FileUploadUtils.uploadFiles(multipartRequest, "C:/upload");

        boardService.save(null, title, content,fileInfoList);
        return "redirect:/list";
    }

    @GetMapping("/updateForm/{id}")
    String updateForm(@PathVariable Long id, Model model) {
        Optional<Board> board = boardRepository.findById(id);

        model.addAttribute("detail", board.get());
        return "board/updateForm.html";
    }

    @PostMapping("/update")
    String update(@RequestParam Long id, @RequestParam String title, @RequestParam String content
            ,MultipartHttpServletRequest multipartRequest) throws IOException {
        List<Map<String, Object>> fileInfoList = FileUploadUtils.uploadFiles(multipartRequest, "C:/upload");
        boardService.save(id, title, content,fileInfoList);
        return "redirect:/list";
    }

/*    @PostMapping("/delete")
    ResponseEntity<Object> delete(@RequestParam Long id) {
        boardRepository.deleteById(id);
        return ResponseEntity.status(200).body("삭제완료");
    }*/

    @PostMapping("/delete")
    ResponseEntity<Object> delete(Board board) {
        boardRepository.delete(board);
        return ResponseEntity.status(200).body("삭제완료");
    }

}
