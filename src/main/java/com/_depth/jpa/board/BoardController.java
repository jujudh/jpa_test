package com._depth.jpa.board;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;

    private final BoardService boardService;


    @GetMapping("/list")
    String list(Model model) {
        //model.addAttribute("boards", boardRepository.findAll());
        List<Board> boards = boardRepository.findAll();
        model.addAttribute("boards", boards);
        return "board/list.html";
    }

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
    String insert(@RequestParam String title, @RequestParam String content) {
        boardService.save(null, title, content);
        return "redirect:/list";
    }

    @GetMapping("/updateForm/{id}")
    String updateForm(@PathVariable Long id, Model model) {
        Optional<Board> board = boardRepository.findById(id);

        model.addAttribute("detail", board.get());
        return "board/updateForm.html";
    }

    @PostMapping("/update")
    String update(@RequestParam Long id, @RequestParam String title, @RequestParam String content) {
        boardService.save(id, title, content);
        return "redirect:/list";
    }

    @PostMapping("/delete")
    ResponseEntity<Object> delete(@RequestParam Long id) {
        boardRepository.deleteById(id);
        return ResponseEntity.status(200).body("삭제완료");
    }

}
