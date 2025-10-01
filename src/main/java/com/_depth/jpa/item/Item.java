package com._depth.jpa.item;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ITEM_ID")
    public Long ItemId;

    @Column(name = "ITEM_NAME")
    public String ItemName;

    @Column(name = "ITEM_CMT")
    public String ItemCmt;
}
