package com._depth.jpa.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {
    private Long boardId;              // b.id
    private String title;              // b.title
    private String content;            // b.content

    private String writerId;           // m.usr_id
    private String writerName;         // m.usr_nm

    private Long commentId;            // c.cmt_id
    private String commentName;        // c.cmt_name
    private String commentDescription; // c.cmt_description

    private int commentCount;
    private int writerBoardCount;
}