package com._depth.jpa.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardSearchDto {
    private String searchTitle;
    private int limit = 15;
    private int offset = 0;
}