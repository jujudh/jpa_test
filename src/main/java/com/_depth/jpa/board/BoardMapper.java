package com._depth.jpa.board;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<BoardDto> search(BoardSearchDto searchDto);
}