package com._depth.jpa.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BoardRepositoryCustom {

    Page<BoardResponseDto> searchBoards(String title, Pageable pageable);
}
