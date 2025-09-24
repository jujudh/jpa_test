package com._depth.jpa.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BoardResponseDto {
    private Long boardId;
    private String title;
    private String content;
    private Integer writerId;
    private String writerName;
    private Long commentCount;
    private Long writerBoardCount;
    private List<CommentDto> comments;
}

