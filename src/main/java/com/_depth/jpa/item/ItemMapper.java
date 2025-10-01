package com._depth.jpa.item;


import com._depth.jpa.board.BoardDto;
import com._depth.jpa.board.BoardSearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ItemMapper {


    List<ItemDto> selectItemList(ItemSearchDto itemSearchDto);
}
