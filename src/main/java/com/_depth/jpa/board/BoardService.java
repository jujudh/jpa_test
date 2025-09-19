package com._depth.jpa.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;


    public void save(Long id, String title, String content) {
        Board board;
        //수정
        if(id != null){
            board  = boardRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Invalid id"));
        } else{ //등록
            board = new Board();
        }
        board.setTitle(title);
        board.setContent(content);
        boardRepository.save(board);
    }
}
