package com._depth.jpa.board;

import com._depth.jpa.file.FileDto;
import com._depth.jpa.file.FileInfo;
import com._depth.jpa.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileService fileService;

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
    public void save(Long id, String title, String content, List<FileDto> fileInfoList) {

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

        if (fileInfoList != null && !fileInfoList.isEmpty()) {
            // 첫 번째 파일만 처리 (1:1 가정)
            FileDto fileDto = fileInfoList.get(0);

            // 1) FileInfo 엔티티로 변환
            FileInfo fileEntity = fileDto.toEntity();

            // 2) Board와 FileInfo 연결
            board.setFile(fileEntity);

            // 3) img_file 컬럼에도 저장하고 싶다면
            board.setImg_file(fileDto.getSavedName());
        }

        // Board 저장 → CascadeType.ALL로 FileInfo도 같이 저장됨
        boardRepository.save(board);
    }


    public List<BoardDto> search(BoardSearchDto searchDto) {
        return boardMapper.search(searchDto);
    }
}
