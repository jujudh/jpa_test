package com._depth.jpa.board;

import com._depth.jpa.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final BoardMapper boardMapper;

/*    public Page<BoardResponseDto> getBoards(String title, Pageable pageable) {
        return boardRepository.searchBoards(title, pageable);
    }*/

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

    public List<BoardDto> search(BoardSearchDto searchDto) {
        return boardMapper.search(searchDto);
    }
}
