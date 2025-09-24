package com._depth.jpa.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentDto {
    private Long commentId;
    private String commentName;
    private String commentDescription;
}
