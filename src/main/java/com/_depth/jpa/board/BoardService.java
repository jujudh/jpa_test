package com._depth.jpa.board;

import com._depth.jpa.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final BoardMapper boardMapper;

/*    public Page<BoardResponseDto> getBoards(String title, Pageable pageable) {
        return boardRepository.searchBoards(title, pageable);
    }*/

/*    public void save(Long id, String title, String content) {
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
    }*/

    /**
     * 게시글 등록/수정 + 첨부파일 저장
     *
     * @param id        수정할 게시글 id (등록이면 null)
     * @param title     게시글 제목
     * @param content   게시글 내용
     * @param fileInfoList 업로드된 파일 정보 리스트
     */
    public void save(Long id, String title, String content, List<Map<String, Object>> fileInfoList) {

        Board board;

        if (id != null) {
            // 수정
            board = boardRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid id"));
        } else {
            // 신규 등록
            board = new Board();
        }

        board.setTitle(title);
        board.setContent(content);

        // 파일 정보 처리 (첫 번째 파일만 저장)
        if (fileInfoList != null && !fileInfoList.isEmpty()) {
            // DB 컬럼이 단일 String이므로 저장 파일명만 넣음
            board.setImg_file((String) fileInfoList.get(0).get("savedName"));
        }

        boardRepository.save(board);
    }

    public List<BoardDto> search(BoardSearchDto searchDto) {
        return boardMapper.search(searchDto);
    }
}
