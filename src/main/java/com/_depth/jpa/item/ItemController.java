package com._depth.jpa.item;


import com._depth.jpa.board.BoardSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemRepository itemRepository;

    private final ItemService itemService;

    @GetMapping("/itemlist")
    String ItemList(@ModelAttribute ItemSearchDto itemSearchDto, Model model) {
        List<ItemDto> items = itemService.selectItemList(itemSearchDto);
        model.addAttribute("items", items);
        return "item/list.html";
    }
}
