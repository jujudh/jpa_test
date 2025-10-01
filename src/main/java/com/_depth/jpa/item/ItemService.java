package com._depth.jpa.item;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;


    public List<ItemDto> selectItemList(ItemSearchDto itemSearchDto) {

        return itemMapper.selectItemList(itemSearchDto);
    }
}
