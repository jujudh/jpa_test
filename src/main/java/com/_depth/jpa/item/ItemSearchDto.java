package com._depth.jpa.item;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    private int limit = 10;
    private int offset = 0;
}
